/*
 * ComiXed - A digital comicBook book library management application.
 * Copyright (C) 2024, The ComiXed Project
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

package org.comixedproject.adaptors.content;

import static junit.framework.TestCase.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ComicInfoXmlFileNameContentAdaptorProviderTest {
  @InjectMocks private ComicInfoXmlFileNameContentAdaptorProvider provider;

  @Test
  public void testCreate() {
    final FileNameContentAdaptor result = provider.create();

    assertNotNull(result);
  }

  @Test
  public void testGetName() {
    final String result = provider.getName();

    assertNotNull(result);
    assertFalse(result.isEmpty());
  }

  @Test
  public void testSupportedFilename() {
    assertTrue(provider.supportedFilename("ComicInfo.xml"));
  }

  @Test
  public void testSupportedFilenameMixedCase() {
    assertTrue(provider.supportedFilename("comicInfo.xml"));
  }

  @Test
  public void testSupportedFilenameInSubdirectory() {
    assertTrue(provider.supportedFilename("subdirectory/comicInfo.xml"));
  }

  @Test
  public void testSupportedFilenameInvalidName() {
    assertFalse(provider.supportedFilename("comicInfos.xml"));
  }
}
