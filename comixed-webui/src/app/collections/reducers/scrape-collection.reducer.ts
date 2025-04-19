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

import { createFeature, createReducer, on } from '@ngrx/store';
import { ScrapeCollectionActions } from '../actions/scrape-story.actions';

export const scrapeCollectionFeatureKey = 'scrapeCollection';

export interface State {}

export const initialState: State = {};

export const reducer = createReducer(
  initialState,
  on(ScrapeCollectionActions.loadScrapeCollections, state => state),
  on(
    ScrapeCollectionActions.loadScrapeCollectionsSuccess,
    (state, action) => state
  ),
  on(
    ScrapeCollectionActions.loadScrapeCollectionsFailure,
    (state, action) => state
  )
);

export const scrapeCollectionFeature = createFeature({
  name: scrapeCollectionFeatureKey,
  reducer
});
