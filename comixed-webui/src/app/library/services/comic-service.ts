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

import { inject, Service } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { LoggerService } from '@angular-ru/cdk/logger';
import { Observable } from 'rxjs';
import { LOAD_COMIC_URL } from '@app/library/library-constants';
import { interpolate } from '@app/app-functions';
import { ComicDetailStore } from '@app/library/stores/comic-detail-store';
import { LoadComicResponse } from '@app/library/models/net/load-comic-response';

@Service()
export class ComicService {
  readonly logger = inject(LoggerService);
  readonly client = inject(HttpClient);
  readonly comicDetailStore = inject(ComicDetailStore);

  loadComic(args: { comicId: number }): Observable<void> {
    return new Observable(subscriber => {
      this.client
        .get<LoadComicResponse>(
          interpolate(LOAD_COMIC_URL, { comicId: args.comicId })
        )
        .subscribe({
          next: response => {
            this.logger.debug('Response received:', response);
            this.comicDetailStore.update(
              response.detail,
              response.pages,
              response.tags,
              response.metadata
            );
          },
          error: error => {
            this.logger.error('Failing to load comic', error);
            subscriber.error(error);
          }
        });
    });
  }
}
