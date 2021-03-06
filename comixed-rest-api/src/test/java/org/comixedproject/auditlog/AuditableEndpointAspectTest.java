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

package org.comixedproject.auditlog;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertSame;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.comixedproject.model.auditlog.WebAuditLogEntry;
import org.comixedproject.service.auditlog.WebAuditLogService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

@RunWith(MockitoJUnitRunner.class)
public class AuditableEndpointAspectTest {
  private static final byte[] TEST_RESPONSE_OBJECT = "JSON content".getBytes();

  @InjectMocks private AuditableEndpointAspect auditableEndpointAspect;
  @Mock private WebAuditLogService webAuditLogService;
  @Mock private ProceedingJoinPoint proceedingJoinPoint;
  @Mock private Object response;
  @Captor private ArgumentCaptor<WebAuditLogEntry> restAuditLogEntryArgumentCaptor;
  @Mock private WebAuditLogEntry savedEntry;
  @Mock private ObjectMapper objectMapper;
  @Mock private ContentCachingRequestWrapper requestWrapper;

  @Before
  public void setUp() {
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(requestWrapper));
    Mockito.when(requestWrapper.getContentAsByteArray()).thenReturn(TEST_RESPONSE_OBJECT);
  }

  @Test
  public void testAround() throws Throwable {
    Mockito.when(webAuditLogService.save(restAuditLogEntryArgumentCaptor.capture()))
        .thenReturn(savedEntry);
    Mockito.when(proceedingJoinPoint.proceed()).thenReturn(response);

    final Object result = auditableEndpointAspect.around(proceedingJoinPoint);

    assertNotNull(result);
    assertSame(response, result);

    Mockito.verify(webAuditLogService, Mockito.times(1))
        .save(restAuditLogEntryArgumentCaptor.getValue());
    Mockito.verify(proceedingJoinPoint, Mockito.times(1)).proceed();
  }
}
