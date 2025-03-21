/*
 * ComiXed - A digital comic book library management application.
 * Copyright (C) 2018, The ComiXed Project
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

package org.comixedproject.auth;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertSame;
import static org.comixedproject.auth.ComiXedAuthenticationFilter.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import org.comixedproject.adaptors.GenericUtilitiesAdaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ComiXedAuthenticationFilterTest {
  private static final String TEST_EMAIL = "reader@comixedproject.org";
  private static final String TEST_PASSWORD = "test password";
  private static final String TEST_AUTH_TOKEN =
      Base64.getEncoder().encodeToString((TEST_EMAIL + ":" + TEST_PASSWORD).getBytes());
  private static final String TEST_TOKEN_AUTH_TOKEN = TOKEN_PREFIX + TEST_AUTH_TOKEN;
  private static final String TEST_BASIC_AUTH_HEADER = BASIC_PREFIX + TEST_AUTH_TOKEN;

  @InjectMocks private ComiXedAuthenticationFilter authenticationFilter;
  @Mock private ComiXedUserDetailsService userDetailsService;
  @Mock private JwtTokenUtil jwtTokenUtil;
  @Mock private GenericUtilitiesAdaptor genericUtilitiesAdaptor;
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private FilterChain filterChain;
  @Mock private UserDetails userDetails;
  @Mock private SecurityContext securityContext;

  @Captor
  private ArgumentCaptor<UsernamePasswordAuthenticationToken> authenticationTokenArgumentCaptor;

  @BeforeEach
  void setUp() {
    Mockito.when(userDetailsService.loadUserByUsername(Mockito.anyString()))
        .thenReturn(userDetails);
    Mockito.when(userDetails.getPassword()).thenReturn(TEST_PASSWORD);

    SecurityContextHolder.setContext(securityContext);
  }

  @Test
  void doFilterInternal_basicAuth() throws ServletException, IOException {
    Mockito.when(request.getHeader(HEADER_STRING)).thenReturn(TEST_BASIC_AUTH_HEADER);

    authenticationFilter.doFilterInternal(request, response, filterChain);

    Mockito.verify(filterChain, Mockito.times(1)).doFilter(request, response);
  }

  @Test
  void doFilterInternalExceptionOnEmailFromToken() throws ServletException, IOException {
    Mockito.when(request.getHeader(HEADER_STRING)).thenReturn(TEST_TOKEN_AUTH_TOKEN);
    Mockito.when(jwtTokenUtil.getEmailFromToken(Mockito.anyString()))
        .thenThrow(RuntimeException.class);

    authenticationFilter.doFilterInternal(request, response, filterChain);

    Mockito.verify(jwtTokenUtil, Mockito.times(1)).getEmailFromToken(TEST_AUTH_TOKEN);
  }

  @Test
  void doFilterInternal_tokenReceived() throws ServletException, IOException {
    Mockito.when(request.getHeader(HEADER_STRING)).thenReturn(TEST_TOKEN_AUTH_TOKEN);
    Mockito.when(jwtTokenUtil.getEmailFromToken(Mockito.anyString())).thenReturn(TEST_EMAIL);
    Mockito.when(jwtTokenUtil.validateToken(Mockito.anyString(), Mockito.any(UserDetails.class)))
        .thenReturn(true);
    Mockito.doNothing()
        .when(securityContext)
        .setAuthentication(authenticationTokenArgumentCaptor.capture());

    authenticationFilter.doFilterInternal(request, response, filterChain);

    assertNotNull(authenticationTokenArgumentCaptor.getValue());
    assertSame(userDetails, authenticationTokenArgumentCaptor.getValue().getPrincipal());

    Mockito.verify(jwtTokenUtil, Mockito.times(1)).getEmailFromToken(TEST_AUTH_TOKEN);
    Mockito.verify(jwtTokenUtil, Mockito.times(1)).validateToken(TEST_AUTH_TOKEN, userDetails);
    Mockito.verify(securityContext, Mockito.times(1))
        .setAuthentication(authenticationTokenArgumentCaptor.getValue());
    Mockito.verify(filterChain, Mockito.times(1)).doFilter(request, response);
  }

  @Test
  void doFilterInternal_invalidTokenReceived() throws ServletException, IOException {
    Mockito.when(request.getHeader(HEADER_STRING)).thenReturn(TEST_TOKEN_AUTH_TOKEN);
    Mockito.when(jwtTokenUtil.getEmailFromToken(Mockito.anyString())).thenReturn(TEST_EMAIL);
    Mockito.when(jwtTokenUtil.validateToken(Mockito.anyString(), Mockito.any(UserDetails.class)))
        .thenReturn(false);

    authenticationFilter.doFilterInternal(request, response, filterChain);

    Mockito.verify(jwtTokenUtil, Mockito.times(1)).getEmailFromToken(TEST_AUTH_TOKEN);
    Mockito.verify(jwtTokenUtil, Mockito.times(1)).validateToken(TEST_AUTH_TOKEN, userDetails);
    Mockito.verify(filterChain, Mockito.times(1)).doFilter(request, response);
  }
}
