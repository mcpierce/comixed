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

import { Component, inject, signal } from '@angular/core';
import { Card } from '@openng/optimus-ui/card';
import { Breadcrumb } from '@openng/optimus-ui/breadcrumb';
import { MenuItem } from '@openng/optimus-ui/api';
import { LoggerService } from '@angular-ru/cdk/logger';
import { TranslateService } from '@ngx-translate/core';
import { TitleService } from '@app/layout/services/title-service';
import { ComicTitlePipe } from '@app/library/pipes/comic-title-pipe';
import { ComicDetailStore } from '@app/library/stores/comic-detail-store';
import { ActivatedRoute, Router } from '@angular/router';
import { ComicService } from '@app/library/services/comic-service';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';

@Component({
  imports: [Card, Breadcrumb],
  selector: 'app-comic-detail-page',
  styleUrl: './comic-detail-page.scss',
  templateUrl: './comic-detail-page.html'
})
export class ComicDetailPage {
  readonly logger = inject(LoggerService);
  readonly translateService = inject(TranslateService);
  readonly activatedRoute = inject(ActivatedRoute);
  readonly router = inject(Router);
  readonly titleService = inject(TitleService);
  readonly comicService = inject(ComicService);
  readonly comicDetailStore = inject(ComicDetailStore);
  readonly comicTitlePipe = new ComicTitlePipe();

  comicId = 0;
  trail = signal<MenuItem[]>([]);
  home: MenuItem = { icon: 'pi pi-home', routerLink: ['/'] };

  constructor() {
    toObservable(this.comicDetailStore.comic)
      .pipe(takeUntilDestroyed())
      .subscribe({
        next: () => {
          this.logger.debug('Comic updated:', this.comicDetailStore.comic());
          this.doLoadTranslations();
        }
      });
    this.activatedRoute.params.subscribe({
      next: value => {
        this.logger.trace('Extracting comic id');
        this.comicId = +value['comicId'];
        this.logger.trace('Loading comic');
        this.comicService.loadComic({ comicId: this.comicId }).subscribe({
          error: error => {
            this.logger.trace('Failed to load comic: returning to home page');
            this.router.navigateByUrl('/');
          }
        });
      }
    });
    this.translateService.onLangChange.subscribe({
      next: () => this.doLoadTranslations()
    });
    this.doLoadTranslations();
  }

  private doLoadTranslations() {
    const comic = this.comicDetailStore.comic();
    const pageTitle = this.comicTitlePipe.transform(comic);
    this.titleService.setTitle(pageTitle);

    if (!comic) {
      return;
    }
    this.trail.set([
      {
        label: comic!.publisher || 'Unknown',
        routerLink: ['/library/publishers', comic!.publisher]
      },
      {
        label: comic!.series || 'Unknown',
        routerLink: [
          '/library/publishers',
          comic!.publisher!,
          'series',
          comic!.series,
          comic!.volume
        ]
      },
      {
        label: `V${comic!.volume || '????'}`
      },
      {
        label: `#${comic!.issueNumber || '??'}`
      }
    ]);
  }
}
