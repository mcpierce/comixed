/*
 * ComiXed - A digital comic book library management application.
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import org.comixedproject.model.comicbooks.ComicBook;
import org.comixedproject.model.comicpages.ComicPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ImageFileTypeContentAdaptorTest extends BaseContentAdaptorTest {
  private static final String TEST_JPEG_FILENAME = "src/test/resources/example.jpg";
  private static final String TEST_WEBP_FILENAME = "src/test/resources/example.webp";
  private static final String TEST_GIF_FILENAME = "src/test/resources/example.gif";

  @InjectMocks private ImageFileTypeContentAdaptor adaptor;

  private ComicBook comicBook = new ComicBook();
  private ContentAdaptorRules contentAdaptorRules = new ContentAdaptorRules();

  @Test
  void loadContent_fileAlreadyExists() throws IOException {
    comicBook.getPages().add(new ComicPage());
    comicBook.getPages().get(0).setFilename(TEST_JPEG_FILENAME);

    byte[] content = loadFile(TEST_JPEG_FILENAME);

    adaptor.loadContent(comicBook, TEST_JPEG_FILENAME, content, contentAdaptorRules);

    assertEquals(1, comicBook.getPageCount());
    assertNotNull(comicBook.getPages().get(0));
  }

  @Test
  void loadContent_jpg() throws IOException {
    byte[] content = loadFile(TEST_JPEG_FILENAME);

    adaptor.loadContent(comicBook, TEST_JPEG_FILENAME, content, contentAdaptorRules);

    assertEquals(1, comicBook.getPageCount());
    assertNotNull(comicBook.getPages().get(0));
  }

  @Test
  void loadContent_webp() throws IOException {
    byte[] content = loadFile(TEST_WEBP_FILENAME);

    adaptor.loadContent(comicBook, TEST_WEBP_FILENAME, content, contentAdaptorRules);

    assertEquals(1, comicBook.getPageCount());
    assertNotNull(comicBook.getPages().get(0));
  }

  @Test
  void loadContent_gif() throws IOException {
    byte[] content = loadFile(TEST_GIF_FILENAME);

    adaptor.loadContent(comicBook, TEST_GIF_FILENAME, content, contentAdaptorRules);

    assertEquals(1, comicBook.getPageCount());
    assertNotNull(comicBook.getPages().get(0));
  }
}
