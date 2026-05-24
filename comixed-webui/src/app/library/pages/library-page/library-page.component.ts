/*
 * ComiXed - A digital comic book library management application.
 * Copyright (C) 2026, The ComiXed Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses>
 */

import { Component, inject } from '@angular/core';
import { DisplayableComic } from '@app/comic-books/models/displayable-comic';
import { ComicDataViewComponent } from '@app/library/components/comic-data-view/comic-data-view.component';
import { LoggerService } from '@angular-ru/cdk/logger';
import { Store } from '@ngrx/store';
import { loadComicsByFilter } from '@app/comic-books/actions/comic-list.actions';
import { QueryParameterService } from '@app/core/services/query-parameter.service';
import { ComicState } from '@app/comic-books/models/comic-state';
import { selectComicList } from '@app/comic-books/selectors/comic-list.selectors';
import { MatToolbar } from '@angular/material/toolbar';
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { QUERY_PARAM_COMICS_AS_GRID } from '@app/core';
import { ActivatedRoute } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import { AsyncPipe } from '@angular/common';

@Component({
  selector: 'cx-library-page',
  imports: [
    ComicDataViewComponent,
    MatToolbar,
    MatIconButton,
    MatIcon,
    AsyncPipe
  ],
  templateUrl: './library-page.component.html',
  styleUrl: './library-page.component.scss'
})
export class LibraryPageComponent {
  logger = inject(LoggerService);
  store = inject(Store);
  activatedRoute = inject(ActivatedRoute);

  queryParameterService = inject(QueryParameterService);
  targetComicState: ComicState | null = null;
  selectedOnly = false;
  missingOnly = false;
  unscrapedOnly = false;
  comics: DisplayableComic[] = [];
  gridView$ = new BehaviorSubject<boolean>(false);

  constructor() {
    this.logger.debug('Subscribing to query parameter updates');
    this.activatedRoute.queryParams.subscribe(params => {
      this.gridView$.next(params[QUERY_PARAM_COMICS_AS_GRID] === `${true}`);
    });
    this.logger.debug('Subscribing to comic list updates');
    this.store
      .select(selectComicList)
      .subscribe(comics => (this.comics = comics));
    this.logger.debug('Loading comic books');
    this.store.dispatch(
      loadComicsByFilter({
        pageSize: this.queryParameterService.pageSize$.value,
        pageIndex: this.queryParameterService.pageIndex$.value,
        coverYear: this.queryParameterService.coverYear$?.value?.year,
        coverMonth: this.queryParameterService.coverYear$?.value?.month,
        archiveType: this.queryParameterService.archiveType$.value,
        comicType: this.queryParameterService.comicType$.value,
        comicState: this.targetComicState,
        selected: this.selectedOnly,
        missing: this.missingOnly,
        unscrapedState: this.unscrapedOnly,
        searchText: this.queryParameterService.filterText$.value,
        publisher: null,
        series: null,
        volume: null,
        pageCount: this.queryParameterService.pageCount$.value,
        sortBy: this.queryParameterService.sortBy$.value,
        sortDirection: this.queryParameterService.sortDirection$.value
      })
    );
  }

  protected onToggleGridView() {
    this.queryParameterService.updateQueryParam([
      {
        name: QUERY_PARAM_COMICS_AS_GRID,
        value: `${this.gridView$.value === false}`
      }
    ]);
  }
}
