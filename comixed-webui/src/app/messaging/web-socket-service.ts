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

import { inject, Service } from '@angular/core';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { AuthTokenService } from '@app/account/auth-token-service';
import { WS_ROOT_URL } from '@app/messaging/messaging-constants';

@Service()
export class WebSocketService {
  stompClient: Client | null = null;
  private authTokenService = inject(AuthTokenService);

  connect() {
    this.stompClient = new Client({
      webSocketFactory: () => new SockJS(WS_ROOT_URL),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      beforeConnect: async () => {
        const token = this.authTokenService.getAuthToken();
        this.stompClient!.connectHeaders = token
          ? {
              Authorization: `Bearer ${token}`,
              authorization: `Bearer ${token}`,
            }
          : {};
      },
    });
  }
}
