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
import { beforeEach } from 'vitest';
import { AUTH_TOKEN_KEY } from '@app/account/account-constants';
import { AuthTokenService } from '@app/account/auth-token-service';

describe('AuthTokenService', () => {
  const TEST_TOKEN = 'the test auth token';

  let service: AuthTokenService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AuthTokenService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('saving the auth token', () => {
    beforeEach(() => {
      service.saveAuthToken(TEST_TOKEN);
    });

    it('should be stored', () => {
      expect(window.localStorage.getItem(AUTH_TOKEN_KEY)).toEqual(TEST_TOKEN);
    });
  });

  describe('checking for an existing auth token', () => {
    describe('when it exists', () => {
      beforeEach(() => {
        window.localStorage.setItem(AUTH_TOKEN_KEY, TEST_TOKEN);
      });

      it('returns true', () => {
        expect(service.hasAuthToken()).toBeTruthy();
      });
    });

    describe('when it does not exist', () => {
      beforeEach(() => {
        window.localStorage.removeItem(AUTH_TOKEN_KEY);
      });

      it('returns false', () => {
        expect(service.hasAuthToken()).toBeFalsy();
      });
    });
  });

  describe('getting the auth token', () => {
    describe('when it exists', () => {
      beforeEach(() => {
        window.localStorage.setItem(AUTH_TOKEN_KEY, TEST_TOKEN);
      });

      it('should return the token', () => {
        expect(service.getAuthToken()).toEqual(TEST_TOKEN);
      });
    });

    describe('when it does not exist', () => {
      beforeEach(() => {
        window.localStorage.removeItem(AUTH_TOKEN_KEY);
      });

      it('should return a blank string', () => {
        expect(service.getAuthToken()).toEqual('');
      });
    });
  });

  describe('removing the auth tokn', () => {
    beforeEach(() => {
      window.localStorage.setItem(AUTH_TOKEN_KEY, TEST_TOKEN);
      service.removeAuthToken();
    });

    it('should remove the auth token', () => {
      window.localStorage.setItem(AUTH_TOKEN_KEY, TEST_TOKEN);
      service.removeAuthToken();
      expect(window.localStorage.getItem(AUTH_TOKEN_KEY)).toBeNull();
    });
  });
});
