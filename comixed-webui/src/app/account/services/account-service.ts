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

import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { LoggerService } from '@angular-ru/cdk/logger';
import { interpolate } from '@app/app-functions';
import {
  CHECK_FOR_ADMIN_ACCOUNT_URL,
  CREATE_ADMIN_ACCOUNT_URL,
  LOAD_CURRENT_USER_URL,
  LOGIN_URL
} from '@app/account/account-constants';
import { CurrentUserStore } from '@app/account/stores/current-user-store';
import { User } from '@app/account/models/user';
import { LoginResponse } from '@app/account/models/net/login-response';
import { TokenService } from '@app/account/services/token-service';
import { InitialAdminStore } from '@app/account/stores/initial-admin-store';
import { CheckForAdminResponse } from '@app/account/models/net/check-for-admin-response';
import { CreateAdminAccountRequest } from '@app/account/models/net/create-admin-account-request';

@Service()
export class AccountService {
  readonly logger = inject(LoggerService);
  readonly client = inject(HttpClient);
  readonly tokenService = inject(TokenService);
  readonly initialAccountStore = inject(InitialAdminStore);
  readonly currentUserStore = inject(CurrentUserStore);

  checkForAdminAccount(): void {
    this.logger.trace('Checking for existing admin account');
    this.client
      .get<CheckForAdminResponse>(interpolate(CHECK_FOR_ADMIN_ACCOUNT_URL))
      .subscribe({
        next: response => {
          this.logger.debug('Found existing account?', response.hasExisting);
          this.initialAccountStore.update(response.hasExisting);
        },
        error: error =>
          this.logger.error(
            'Failed to check for existing admin account:',
            error
          )
      });
  }

  createAdminAccount(args: { email: string; password: string }): void {
    this.initialAccountStore.update(false);
    this.logger.trace('Creating admin account');
    this.client
      .post(interpolate(CREATE_ADMIN_ACCOUNT_URL), {
        email: args.email,
        password: args.password
      } as CreateAdminAccountRequest)
      .subscribe({
        complete: () => {
          this.logger.debug(
            'Successfully created initial admin account:',
            args.email
          );
          this.initialAccountStore.update(true);
        },
        error: error =>
          this.logger.error('Failed to create initial admin account:', error)
      });
  }

  loadCurrentUser(): void {
    this.logger.trace('Loading current user');
    this.currentUserStore.loading();
    this.client.get<User>(interpolate(LOAD_CURRENT_USER_URL)).subscribe({
      next: value => {
        this.logger.debug('User loaded:', value);
        this.currentUserStore.loaded(value);
      },
      error: error => {
        this.logger.error('Failed to load user:', error);
        this.currentUserStore.loaded(null);
      }
    });
  }

  login(args: { email: string; password: string }) {
    this.logger.trace('Sending login credentials');
    this.currentUserStore.login();
    this.client
      .post<LoginResponse>(
        interpolate(LOGIN_URL),
        new HttpParams().set('email', args.email).set('password', args.password)
      )
      .subscribe({
        next: value => {
          this.logger.debug('Login successful:', value);
          this.tokenService.setAuthToken(value.token);
          this.currentUserStore.loginSuccess();
        },
        error: error => {
          this.logger.error('Failed to login user:', error);
          this.currentUserStore.loginFailure();
        }
      });
  }
}
