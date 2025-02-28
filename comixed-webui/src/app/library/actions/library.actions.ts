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

import { createAction, props } from '@ngrx/store';
import { EditMultipleComics } from '@app/library/models/ui/edit-multiple-comics';
import { RemoteLibraryState } from '@app/library/models/net/remote-library-state';

export const editMultipleComics = createAction(
  '[Library] Edit multiple comics',
  props<{ ids: number[]; details: EditMultipleComics }>()
);

export const multipleComicsEdited = createAction(
  '[Library] Multiple comics edited'
);

export const editMultipleComicsFailed = createAction(
  '[Library] Failed to edit multiple comics'
);

export const loadLibraryState = createAction(
  '[Library] Load the current state of the library'
);

export const libraryStateLoaded = createAction(
  '[Library] The current state of the library is loaded',
  props<{ state: RemoteLibraryState }>()
);

export const loadLibraryStateFailed = createAction(
  '[Library] Failed to load the current state of the library'
);
