/*
 * ComiXed - A digital comic book library management application.
 * Copyright (C) 2025, The ComiXed Project
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

import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, concatMap } from 'rxjs/operators';
import { Observable, EMPTY, of } from 'rxjs';
import { ScrapeCollectionActions } from '../actions/scrape-story.actions';

@Injectable()
export class ScrapeCollectionEffects {
  loadScrapeCollections$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(ScrapeCollectionActions.loadScrapeCollections),
      concatMap(() =>
        /** An EMPTY observable only emits completion. Replace with your own observable API request */
        EMPTY.pipe(
          map(data =>
            ScrapeCollectionActions.loadScrapeCollectionsSuccess({ data })
          ),
          catchError(error =>
            of(ScrapeCollectionActions.loadScrapeCollectionsFailure({ error }))
          )
        )
      )
    );
  });

  constructor(private actions$: Actions) {}
}
