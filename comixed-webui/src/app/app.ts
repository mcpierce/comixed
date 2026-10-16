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
import { LoggerService } from '@angular-ru/cdk/logger';
import { AccountService } from '@app/account/services/account-service';
import { CurrentUserStore } from '@app/account/stores/current-user-store';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';
import { LibraryService } from '@app/library/services/library-service';
import { WebSocketService } from '@app/messaging/services/web-socket-service';
import { Router, RouterOutlet } from '@angular/router';
import { Topbar } from '@app/layout/components/topbar/topbar';
import { InitialAdminStore } from '@app/account/stores/initial-admin-store';

@Component({
  imports: [Topbar, RouterOutlet],
  selector: 'app-root',
  styleUrl: './app.scss',
  templateUrl: './app.html'
})
export class App {
  readonly logger = inject(LoggerService);
  readonly initialAdminStore = inject(InitialAdminStore);
  readonly currentUserStore = inject(CurrentUserStore);

  readonly router = inject(Router);
  readonly accountService = inject(AccountService);
  readonly libraryService = inject(LibraryService);
  readonly webSocketService = inject(WebSocketService);

  constructor() {
    this.accountService.checkForAdminAccount();
    toObservable<boolean>(this.initialAdminStore.checked)
      .pipe(takeUntilDestroyed())
      .subscribe({
        next: checked => {
          if (!checked) {
            this.logger.trace('Checking for initial admin account');
            this.accountService.checkForAdminAccount();
          }
        }
      });
    toObservable<boolean>(this.initialAdminStore.exists)
      .pipe(takeUntilDestroyed())
      .subscribe({
        next: exists => {
          if (this.initialAdminStore.checked()) {
            if (!exists) {
              this.logger.trace('Redirecting to account creation page');
              this.router.navigateByUrl('/accounts/initial');
            } else {
              this.accountService.loadCurrentUser();
            }
          }
        }
      });
    toObservable<boolean>(this.currentUserStore.isAuthenticated)
      .pipe(takeUntilDestroyed())
      .subscribe({
        next: value => {
          if (value) {
            this.webSocketService.connect().subscribe();
            this.logger.trace('Loading the remote library state');
            this.libraryService.loadLibraryState();
          } else {
            this.webSocketService.disconnect();
          }
        }
      });
  }
}
