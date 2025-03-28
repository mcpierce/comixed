/*
 * ComiXed - A digital comic book library management application.
 * Copyright (C) 2022, The ComiXed Project
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

package org.comixedproject.opds.rest;

import static org.junit.Assert.*;

import java.security.Principal;
import org.apache.commons.lang.math.RandomUtils;
import org.comixedproject.opds.OPDSUtils;
import org.comixedproject.opds.model.OPDSAcquisitionFeed;
import org.comixedproject.opds.model.OPDSNavigationFeed;
import org.comixedproject.opds.service.OPDSAcquisitionService;
import org.comixedproject.opds.service.OPDSNavigationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OPDSSeriesControllerTest {
  private static final boolean TEST_UNREAD = RandomUtils.nextBoolean();
  private static final String TEST_SERIES_NAME_ENCODED = "The encoded series";
  private static final String TEST_SERIES_NAME_DECODED = "The decoded series";
  private static final String TEST_EMAIL = "reader@comixedproject.org";

  @InjectMocks private OPDSSeriesController controller;
  @Mock private OPDSNavigationService opdsNavigationService;
  @Mock private OPDSAcquisitionService opdsAcquisitionService;
  @Mock private OPDSUtils opdsUtils;
  @Mock private OPDSNavigationFeed opdsNavigationFeed;
  @Mock private OPDSAcquisitionFeed opdsAcquisitionFeed;
  @Mock private Principal principal;

  @BeforeEach
  public void setUp() {
    Mockito.when(principal.getName()).thenReturn(TEST_EMAIL);
  }

  @Test
  void getRootFeedForSeries() {
    Mockito.when(
            opdsNavigationService.getRootFeedForSeries(Mockito.anyString(), Mockito.anyBoolean()))
        .thenReturn(opdsNavigationFeed);

    final OPDSNavigationFeed result = controller.getRootFeedForSeries(principal, TEST_UNREAD);

    assertNotNull(result);
    assertSame(opdsNavigationFeed, result);

    Mockito.verify(opdsNavigationService, Mockito.times(1))
        .getRootFeedForSeries(TEST_EMAIL, TEST_UNREAD);
  }

  @Test
  void getVolumeFeedForSeries() {
    Mockito.when(opdsUtils.urlDecodeString(Mockito.anyString()))
        .thenReturn(TEST_SERIES_NAME_DECODED);
    Mockito.when(
            opdsNavigationService.getPublishersFeedForSeries(
                Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean()))
        .thenReturn(opdsNavigationFeed);

    final OPDSNavigationFeed result =
        controller.getPublishersFeedForSeries(principal, TEST_SERIES_NAME_ENCODED, TEST_UNREAD);

    assertNotNull(result);
    assertSame(opdsNavigationFeed, result);

    Mockito.verify(opdsUtils, Mockito.times(1)).urlDecodeString(TEST_SERIES_NAME_ENCODED);
    Mockito.verify(opdsNavigationService, Mockito.times(1))
        .getPublishersFeedForSeries(TEST_SERIES_NAME_DECODED, TEST_EMAIL, TEST_UNREAD);
  }
}
