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

package org.comixedproject.rest.user;

import static org.comixedproject.rest.comicbooks.ComicSelectionController.LIBRARY_SELECTIONS;
import static org.junit.Assert.assertThrows;

import jakarta.servlet.http.HttpSession;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import org.comixedproject.service.comicbooks.ComicSelectionException;
import org.comixedproject.service.comicbooks.ComicSelectionService;
import org.comixedproject.service.user.ReadComicsException;
import org.comixedproject.service.user.ReadComicsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ReadComicsControllerTest {
  private static final String TEST_EMAIL = "reader@comixedproject.org";
  private static final String TEST_ENCODED_IDS = "The encoded selected ids";
  private static final String TEST_REENCODED_IDS = "The re-encoded selected ids";
  private static final long TEST_COMIC_DETAIL_ID = 129L;

  @InjectMocks private ReadComicsController controller;
  @Mock private ReadComicsService readComicsService;
  @Mock private ComicSelectionService comicSelectionService;
  @Mock private HttpSession httpSession;
  @Mock private Principal principal;

  private List selectedIds = new ArrayList();

  @BeforeEach
  void setUp() throws ComicSelectionException {
    Mockito.when(principal.getName()).thenReturn(TEST_EMAIL);
    Mockito.when(httpSession.getAttribute(LIBRARY_SELECTIONS)).thenReturn(TEST_ENCODED_IDS);
    Mockito.when(comicSelectionService.decodeSelections(TEST_ENCODED_IDS)).thenReturn(selectedIds);
    Mockito.when(comicSelectionService.encodeSelections(Mockito.anyList()))
        .thenReturn(TEST_REENCODED_IDS);
  }

  @Test
  void markComicBookAsRead_serviceThrowsException() throws ReadComicsException {
    Mockito.doThrow(ReadComicsException.class)
        .when(readComicsService)
        .markComicBookAsRead(Mockito.anyString(), Mockito.anyLong());

    assertThrows(
        ReadComicsException.class,
        () -> controller.markSingleComicBookRead(principal, TEST_COMIC_DETAIL_ID));
  }

  @Test
  void markComicBookAsRead() throws ReadComicsException {
    controller.markSingleComicBookRead(principal, TEST_COMIC_DETAIL_ID);

    Mockito.verify(readComicsService, Mockito.times(1))
        .markComicBookAsRead(TEST_EMAIL, TEST_COMIC_DETAIL_ID);
  }

  @Test
  void umnarkComicBookAsRead_serviceThrowsException() throws ReadComicsException {
    Mockito.doThrow(ReadComicsException.class)
        .when(readComicsService)
        .unmarkComicBookAsRead(Mockito.anyString(), Mockito.anyLong());

    assertThrows(
        ReadComicsException.class,
        () -> controller.unmarkSingleComicBookRead(principal, TEST_COMIC_DETAIL_ID));
  }

  @Test
  void unmarkComicBookAsRead() throws ReadComicsException {
    controller.unmarkSingleComicBookRead(principal, TEST_COMIC_DETAIL_ID);

    Mockito.verify(readComicsService, Mockito.times(1))
        .unmarkComicBookAsRead(TEST_EMAIL, TEST_COMIC_DETAIL_ID);
  }

  @Test
  void markSelectedAsRead_serviceThrowsException() throws ReadComicsException {
    Mockito.doThrow(ReadComicsException.class)
        .when(readComicsService)
        .markSelectionsAsRead(Mockito.anyString(), Mockito.anyList());

    assertThrows(
        ReadComicsException.class,
        () -> controller.markSelectedComicBooksRead(principal, httpSession));
  }

  @Test
  void markSelectedAsRead_selectionExceptionDuringDecode() throws ReadComicsException {
    Mockito.doThrow(ReadComicsException.class)
        .when(readComicsService)
        .markSelectionsAsRead(Mockito.anyString(), Mockito.anyList());

    assertThrows(
        ReadComicsException.class,
        () -> controller.markSelectedComicBooksRead(principal, httpSession));
  }

  @Test
  void markSelectedAsRead_selectionExceptionDuringEncode() throws ComicSelectionException {
    Mockito.doThrow(ComicSelectionException.class)
        .when(comicSelectionService)
        .encodeSelections(Mockito.anyList());

    assertThrows(
        ComicSelectionException.class,
        () -> controller.markSelectedComicBooksRead(principal, httpSession));
  }

  @Test
  void markSelectedAsRead() throws ReadComicsException, ComicSelectionException {
    controller.markSelectedComicBooksRead(principal, httpSession);

    Mockito.verify(readComicsService, Mockito.times(1))
        .markSelectionsAsRead(TEST_EMAIL, selectedIds);
    Mockito.verify(comicSelectionService, Mockito.times(1))
        .clearSelectedComicBooks(TEST_EMAIL, selectedIds);
  }

  @Test
  void unmarkSelectedAsRead_serviceThrowsException() throws ReadComicsException {
    Mockito.doThrow(ReadComicsException.class)
        .when(readComicsService)
        .unmarkSelectionsAsRead(Mockito.anyString(), Mockito.anyList());

    assertThrows(
        ReadComicsException.class,
        () -> controller.unmarkSelectedComicBooksRead(principal, httpSession));
  }

  @Test
  void unmarkSelectedAsRead_selectionExceptionDuringDecode() throws ReadComicsException {
    Mockito.doThrow(ReadComicsException.class)
        .when(readComicsService)
        .unmarkSelectionsAsRead(Mockito.anyString(), Mockito.anyList());

    assertThrows(
        ReadComicsException.class,
        () -> controller.unmarkSelectedComicBooksRead(principal, httpSession));
  }

  @Test
  void unmarkSelectedAsRead_selectionExceptionDuringEncode() throws ComicSelectionException {
    Mockito.doThrow(ComicSelectionException.class)
        .when(comicSelectionService)
        .encodeSelections(Mockito.anyList());

    assertThrows(
        ComicSelectionException.class,
        () -> controller.unmarkSelectedComicBooksRead(principal, httpSession));
  }

  @Test
  void unmarkSelectedAsRead() throws ReadComicsException, ComicSelectionException {
    controller.unmarkSelectedComicBooksRead(principal, httpSession);

    Mockito.verify(readComicsService, Mockito.times(1))
        .unmarkSelectionsAsRead(TEST_EMAIL, selectedIds);
    Mockito.verify(comicSelectionService, Mockito.times(1))
        .clearSelectedComicBooks(TEST_EMAIL, selectedIds);
  }
}
