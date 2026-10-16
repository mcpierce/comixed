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

import { CanActivateFn } from '@angular/router';
import { inject } from '@angular/core';
import { CurrentUserStore } from '@app/account/stores/current-user-store';
import { LoggerService } from '@angular-ru/cdk/logger';

export const readerGuard: CanActivateFn = (route, state) => {
  const logger = inject(LoggerService);
  const currentUserStore = inject(CurrentUserStore);

  if (currentUserStore.isAuthenticated()) {
    logger.debug('Is user a reader?', currentUserStore.isReader());
    return currentUserStore.isReader();
  } else {
    logger.debug('User is not logged in');
    return false;
  }
};
