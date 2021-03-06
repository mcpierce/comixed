/*
 * ComiXed - A digital comic book library management application.
 * Copyright (C) 2020, The ComiXed Project
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

import { createReducer, on } from '@ngrx/store';
import {
  pageSizeSet,
  paginationSet,
  resetDisplayOptions,
  setPageSize,
  setPagination
} from '../actions/display.actions';
import {
  PAGE_SIZE_DEFAULT,
  PAGE_SIZE_PREFERENCE,
  PAGINATION_DEFAULT
} from '@app/library/library.constants';
import { getUserPreference } from '@app/user';

export const DISPLAY_FEATURE_KEY = 'display_state';

export interface DisplayState {
  pageSize: number;
  pagination: number;
}

export const initialState: DisplayState = {
  pageSize: PAGE_SIZE_DEFAULT,
  pagination: PAGINATION_DEFAULT
};

export const reducer = createReducer(
  initialState,

  on(resetDisplayOptions, (state, action) => {
    if (!!action.user) {
      return {
        ...state,
        pageSize: parseInt(
          getUserPreference(
            action.user.preferences,
            PAGE_SIZE_PREFERENCE,
            `${PAGE_SIZE_DEFAULT}`
          ),
          10
        )
      };
    } else {
      return { ...state, pageSize: PAGE_SIZE_DEFAULT };
    }
  }),
  on(setPageSize, (state, action) => ({ ...state, pageSize: action.size })),
  on(pageSizeSet, state => state),
  on(setPagination, (state, action) => ({
    ...state,
    pagination: action.pagination
  })),
  on(paginationSet, state => state)
);
