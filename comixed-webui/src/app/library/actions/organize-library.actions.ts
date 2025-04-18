/*
 * ComiXed - A digital comic book library management application.
 * Copyright (C) 2021, The ComiXed Project
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

import { createAction } from '@ngrx/store';

export const startLibraryOrganization = createAction(
  '[Organize Library] Start the library organization process'
);

export const startLibraryOrganizationSuccess = createAction(
  '[Organize Library] The organization process is started'
);

export const startLibraryOrganizationFailure = createAction(
  '[Organize Library] Failed to start the organization process'
);

export const startEntireLibraryOrganization = createAction(
  '[Organize Library] Organize every comic in the library'
);

export const startEntireLibraryOrganizationSuccess = createAction(
  '[Organize Library] Successfully started organizing every comic in the library'
);

export const startEntireLibraryOrganizationFailure = createAction(
  '[Organize Library] Failed to organize every comic in the library'
);
