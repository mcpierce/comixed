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

import { TestBed } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { Observable, of, throwError } from 'rxjs';
import { AuthenticationEffects } from './authentication.effects';
import { AuthenticationService } from '@app/user/services/authentication-service';
import {
  authenticateUser,
  authenticateUserFailure,
  authenticateUserSuccess,
  loadCurrentUser
} from '@app/user/actions/authentication.actions';
import { USER_READER } from '@app/user/user-fixtures';
import { hot } from 'vitest-marbles';
import { LoggerLevel, provideLogger } from '@angular-ru/cdk/logger';
import { LoginResponse } from '@app/user/models/net/login-response';
import { TokenService } from '@app/user/services/token-service';
import { HttpErrorResponse } from '@angular/common/http';

describe('AuthenticationEffects', () => {
  const TEST_USER = USER_READER;
  const TEST_EMAIL = TEST_USER.email;
  const TEST_PASSWORD = 'the3!p455w0Rd';
  const TEST_TOKEN = 'the!returned!token';

  let actions$: Observable<any>;
  let effects: AuthenticationEffects;
  let authenticationService: AuthenticationService;
  let tokenService: TokenService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideLogger({ minLevel: LoggerLevel.OFF }),
        AuthenticationEffects,
        provideMockActions(() => actions$),
        AuthenticationService,
        TokenService
      ]
    });

    effects = TestBed.inject(AuthenticationEffects);
    authenticationService = vi.mocked(TestBed.inject(AuthenticationService));
    tokenService = vi.mocked(TestBed.inject(TokenService));
  });

  it('should be created', () => {
    expect(effects).toBeTruthy();
  });

  describe('authenticating a user', () => {
    it('fires an action on success', () => {
      const serviceResponse = {
        email: TEST_EMAIL,
        token: TEST_TOKEN
      } as LoginResponse;
      const action = authenticateUser({
        email: TEST_EMAIL,
        password: TEST_PASSWORD
      });
      const outcome1 = authenticateUserSuccess();
      const outcome2 = loadCurrentUser();

      actions$ = hot('-a', { a: action });
      const loginSpy = vi
        .spyOn(authenticationService, 'login')
        .mockReturnValue(of(serviceResponse));
      const setAuthTokenSpy = vi.spyOn(tokenService, 'setAuthToken');

      const expected = hot('-(op)', { o: outcome1, p: outcome2 });
      expect(effects.authenticateUser$).toBeObservable(expected);
      expect(loginSpy).toHaveBeenCalledWith({
        email: TEST_EMAIL,
        password: TEST_PASSWORD
      });
      expect(setAuthTokenSpy).toHaveBeenCalledWith(TEST_TOKEN);
    });

    it('fires an action on server error', () => {
      const serviceResponse = new HttpErrorResponse({});
      const action = authenticateUser({
        email: TEST_EMAIL,
        password: TEST_PASSWORD
      });
      const outcome = authenticateUserFailure();

      actions$ = hot('-a', { a: action });
      const loginSpy = vi
        .spyOn(authenticationService, 'login')
        .mockReturnValueOnce(throwError(() => serviceResponse));
      const setAuthTokenSpy = vi.spyOn(tokenService, 'setAuthToken');

      const expected = hot('-o', { o: outcome });
      expect(effects.authenticateUser$).toBeObservable(expected);
      expect(loginSpy).toHaveBeenCalledWith({
        email: TEST_EMAIL,
        password: TEST_PASSWORD
      });
      expect(setAuthTokenSpy).not.toHaveBeenCalled();
    });

    it('fires an action on general error', () => {
      const action = authenticateUser({
        email: TEST_EMAIL,
        password: TEST_PASSWORD
      });
      const outcome = authenticateUserFailure();

      actions$ = hot('-a', { a: action });
      const loginSpy = vi
        .spyOn(authenticationService, 'login')
        .mockThrow(new Error('general failure'));
      const setAuthTokenSpy = vi.spyOn(tokenService, 'setAuthToken');

      const expected = hot('-(o|)', { o: outcome });
      expect(effects.authenticateUser$).toBeObservable(expected);
      expect(loginSpy).toHaveBeenCalledWith({
        email: TEST_EMAIL,
        password: TEST_PASSWORD
      });
      expect(setAuthTokenSpy).not.toHaveBeenCalled();
    });
  });
});
