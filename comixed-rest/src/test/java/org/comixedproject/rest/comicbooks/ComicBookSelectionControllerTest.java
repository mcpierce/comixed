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

package org.comixedproject.rest.comicbooks;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.comixedproject.rest.comicbooks.ComicBookSelectionController.LIBRARY_SELECTIONS;

import jakarta.servlet.http.HttpSession;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import org.comixedproject.model.comicbooks.ComicTagType;
import org.comixedproject.model.net.comicbooks.*;
import org.comixedproject.opds.OPDSUtils;
import org.comixedproject.service.comicbooks.ComicBookSelectionException;
import org.comixedproject.service.comicbooks.ComicSelectionService;
import org.comixedproject.service.library.DisplayableComicService;
import org.comixedproject.service.user.ComiXedUserException;
import org.comixedproject.service.user.UserService;
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
class ComicBookSelectionControllerTest {
  private static final String TEST_ENCODED_SELECTIONS = "This is the encoded selection set";
  private static final String TEST_REENCODED_SELECTIONS = "This is the selection set re-encoded";
  private static final Long TEST_COMIC_BOOK_ID = 17L;
  private static final ComicTagType TEST_TAG_TYPE = ComicTagType.CHARACTER;
  private static final String TEST_TAG_VALUE = "The Batman";
  private static final String TEST_PUBLISHER = "The Publisher";
  private static final String TEST_SERIES = "The Series";
  private static final String TEST_VOLUME = "2024";
  private static final String TEST_EMAIL = "reader@comixedproject.org";

  @InjectMocks private ComicBookSelectionController controller;
  @Mock private ComicSelectionService comicSelectionService;
  @Mock private DisplayableComicService displayableComicService;
  @Mock private UserService userService;
  @Mock private OPDSUtils opdsUtils;
  @Mock private HttpSession httpSession;
  @Mock private List<Long> selectedIds;
  @Mock private Principal principal;
  @Mock private List<Long> comicDetailIdList;

  @BeforeEach
  void setUp() throws ComicBookSelectionException, ComiXedUserException {
    Mockito.when(httpSession.getAttribute(LIBRARY_SELECTIONS)).thenReturn(TEST_ENCODED_SELECTIONS);
    Mockito.when(comicSelectionService.decodeSelections(TEST_ENCODED_SELECTIONS))
        .thenReturn(selectedIds);
    Mockito.when(comicSelectionService.encodeSelections(Mockito.anyList()))
        .thenReturn(TEST_REENCODED_SELECTIONS);
    Mockito.when(principal.getName()).thenReturn(TEST_EMAIL);
    Mockito.when(userService.getComicBookIdsForUser(Mockito.anyString(), Mockito.anyBoolean()))
        .thenReturn(comicDetailIdList);
  }

  @Test
  void getAllSelections() throws ComicBookSelectionException {
    final List result = controller.getAllSelections(httpSession);

    assertNotNull(result);
    assertEquals(selectedIds, result);

    Mockito.verify(httpSession, Mockito.times(1)).getAttribute(LIBRARY_SELECTIONS);
  }

  @Test
  void addSingleSelection_selecting() throws ComicBookSelectionException {
    controller.addSingleSelection(httpSession, principal, TEST_COMIC_BOOK_ID);

    Mockito.verify(comicSelectionService, Mockito.times(1))
        .addComicSelectionForUser(TEST_EMAIL, selectedIds, TEST_COMIC_BOOK_ID);
    Mockito.verify(comicSelectionService, Mockito.times(1)).encodeSelections(selectedIds);
    Mockito.verify(httpSession, Mockito.times(1))
        .setAttribute(LIBRARY_SELECTIONS, TEST_REENCODED_SELECTIONS);
  }

  @Test
  void addSingleSelection_deselecting() throws ComicBookSelectionException {
    controller.deleteSingleSelection(httpSession, principal, TEST_COMIC_BOOK_ID);

    Mockito.verify(httpSession, Mockito.times(1)).getAttribute(LIBRARY_SELECTIONS);
    Mockito.verify(comicSelectionService, Mockito.times(1))
        .removeComicSelectionFromUser(TEST_EMAIL, selectedIds, TEST_COMIC_BOOK_ID);
    Mockito.verify(comicSelectionService, Mockito.times(1)).encodeSelections(selectedIds);
    Mockito.verify(httpSession, Mockito.times(1))
        .setAttribute(LIBRARY_SELECTIONS, TEST_REENCODED_SELECTIONS);
  }

  @Test
  void selectComicBooksByFilter_selecting() throws ComicBookSelectionException {
    controller.selectComicBooksByFilter(
        httpSession,
        principal,
        new MultipleComicBooksSelectionRequest(
            null, null, null, null, null, false, null, null, true));

    Mockito.verify(comicSelectionService, Mockito.times(1))
        .selectByFilter(
            TEST_EMAIL, selectedIds, null, null, null, null, null, false, null, null, true);
    Mockito.verify(comicSelectionService, Mockito.times(1)).encodeSelections(selectedIds);
    Mockito.verify(httpSession, Mockito.times(1))
        .setAttribute(LIBRARY_SELECTIONS, TEST_REENCODED_SELECTIONS);
  }

  @Test
  void selectComicBooksByFilter_deselecting() throws ComicBookSelectionException {
    controller.selectComicBooksByFilter(
        httpSession,
        principal,
        new MultipleComicBooksSelectionRequest(
            null, null, null, null, null, false, null, null, false));

    Mockito.verify(comicSelectionService, Mockito.times(1))
        .selectByFilter(
            TEST_EMAIL, selectedIds, null, null, null, null, null, false, null, null, false);
    Mockito.verify(comicSelectionService, Mockito.times(1)).encodeSelections(selectedIds);
    Mockito.verify(httpSession, Mockito.times(1))
        .setAttribute(LIBRARY_SELECTIONS, TEST_REENCODED_SELECTIONS);
  }

  @Test
  void addComicBooksByTagTypeAndValue() throws ComicBookSelectionException {
    Mockito.when(opdsUtils.urlDecodeString(TEST_TAG_VALUE)).thenReturn(TEST_TAG_VALUE);

    controller.addComicBooksByTagTypeAndValue(
        httpSession, principal, TEST_TAG_TYPE, TEST_TAG_VALUE);

    Mockito.verify(httpSession, Mockito.times(1)).getAttribute(LIBRARY_SELECTIONS);
    Mockito.verify(comicSelectionService, Mockito.times(1))
        .addByTagTypeAndValue(selectedIds, TEST_TAG_TYPE, TEST_TAG_VALUE);
    Mockito.verify(comicSelectionService, Mockito.times(1)).encodeSelections(selectedIds);
    Mockito.verify(httpSession, Mockito.times(1))
        .setAttribute(LIBRARY_SELECTIONS, TEST_REENCODED_SELECTIONS);
  }

  @Test
  void removeComicBooksByTagTypeAndValue() throws ComicBookSelectionException {
    Mockito.when(opdsUtils.urlDecodeString(TEST_TAG_VALUE)).thenReturn(TEST_TAG_VALUE);

    controller.removeComicBooksByTagTypeAndValue(
        httpSession, principal, TEST_TAG_TYPE, TEST_TAG_VALUE);

    Mockito.verify(httpSession, Mockito.times(1)).getAttribute(LIBRARY_SELECTIONS);
    Mockito.verify(comicSelectionService, Mockito.times(1))
        .removeByTagTypeAndValue(selectedIds, TEST_TAG_TYPE, TEST_TAG_VALUE);
    Mockito.verify(comicSelectionService, Mockito.times(1)).encodeSelections(selectedIds);
    Mockito.verify(httpSession, Mockito.times(1))
        .setAttribute(LIBRARY_SELECTIONS, TEST_REENCODED_SELECTIONS);
  }

  @Test
  void addSelectionsById_selecting() throws ComicBookSelectionException {
    final List<Long> comicBookIdList = new ArrayList<Long>();
    for (long index = 1000L; index < 2000L; index++) comicBookIdList.add(index);

    controller.addComicBookSelectionsById(
        httpSession, principal, new AddComicBookSelectionsByIdRequest(comicBookIdList, true));

    Mockito.verify(httpSession, Mockito.times(1)).getAttribute(LIBRARY_SELECTIONS);
    Mockito.verify(selectedIds, Mockito.times(1)).addAll(comicBookIdList);
    Mockito.verify(comicSelectionService, Mockito.times(1)).encodeSelections(selectedIds);
    Mockito.verify(httpSession, Mockito.times(1))
        .setAttribute(LIBRARY_SELECTIONS, TEST_REENCODED_SELECTIONS);
  }

  @Test
  void addSelectionsById_deselecting() throws ComicBookSelectionException {
    final List<Long> comicBookIdList = new ArrayList<Long>();
    for (long index = 1000L; index < 2000L; index++) comicBookIdList.add(index);

    controller.addComicBookSelectionsById(
        httpSession, principal, new AddComicBookSelectionsByIdRequest(comicBookIdList, false));

    Mockito.verify(httpSession, Mockito.times(1)).getAttribute(LIBRARY_SELECTIONS);
    Mockito.verify(selectedIds, Mockito.times(1)).removeAll(comicBookIdList);
    Mockito.verify(comicSelectionService, Mockito.times(1)).encodeSelections(selectedIds);
    Mockito.verify(httpSession, Mockito.times(1))
        .setAttribute(LIBRARY_SELECTIONS, TEST_REENCODED_SELECTIONS);
  }

  @Test
  void addSelectionsByPublisherForAddition_selecting() throws ComicBookSelectionException {
    Mockito.when(displayableComicService.getIdsByPublisher(Mockito.anyString()))
        .thenReturn(selectedIds);

    controller.addComicBookSelectionsByPublisher(
        httpSession, principal, new AddComicBookSelectionsByPublisherRequest(TEST_PUBLISHER, true));

    Mockito.verify(httpSession, Mockito.times(1)).getAttribute(LIBRARY_SELECTIONS);
    Mockito.verify(displayableComicService, Mockito.times(1)).getIdsByPublisher(TEST_PUBLISHER);
    Mockito.verify(selectedIds, Mockito.times(1)).addAll(selectedIds);
    Mockito.verify(comicSelectionService, Mockito.times(1)).encodeSelections(selectedIds);
    Mockito.verify(httpSession, Mockito.times(1))
        .setAttribute(LIBRARY_SELECTIONS, TEST_REENCODED_SELECTIONS);
  }

  @Test
  void addSelectionsByPublisherForAddition_deselecting() throws ComicBookSelectionException {
    Mockito.when(displayableComicService.getIdsByPublisher(Mockito.anyString()))
        .thenReturn(selectedIds);

    controller.addComicBookSelectionsByPublisher(
        httpSession,
        principal,
        new AddComicBookSelectionsByPublisherRequest(TEST_PUBLISHER, false));

    Mockito.verify(httpSession, Mockito.times(1)).getAttribute(LIBRARY_SELECTIONS);
    Mockito.verify(displayableComicService, Mockito.times(1)).getIdsByPublisher(TEST_PUBLISHER);
    Mockito.verify(selectedIds, Mockito.times(1)).removeAll(selectedIds);
    Mockito.verify(comicSelectionService, Mockito.times(1)).encodeSelections(selectedIds);
    Mockito.verify(httpSession, Mockito.times(1))
        .setAttribute(LIBRARY_SELECTIONS, TEST_REENCODED_SELECTIONS);
  }

  @Test
  void addSelectionsByPublisherSeriesVolume_selecting() throws ComicBookSelectionException {
    Mockito.when(
            this.displayableComicService.getIdsByPublisherSeriesAndVolume(
                Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
        .thenReturn(selectedIds);

    controller.addComicBookSelectionsByPublisherSeriesVolume(
        httpSession,
        principal,
        new AddComicBookSelectionsByPublisherSeriesVolumeRequest(
            TEST_PUBLISHER, TEST_SERIES, TEST_VOLUME, true));

    Mockito.verify(httpSession, Mockito.times(1)).getAttribute(LIBRARY_SELECTIONS);
    Mockito.verify(displayableComicService, Mockito.times(1))
        .getIdsByPublisherSeriesAndVolume(TEST_PUBLISHER, TEST_SERIES, TEST_VOLUME);
    Mockito.verify(selectedIds, Mockito.times(1)).addAll(selectedIds);
    Mockito.verify(comicSelectionService, Mockito.times(1)).encodeSelections(selectedIds);
    Mockito.verify(httpSession, Mockito.times(1))
        .setAttribute(LIBRARY_SELECTIONS, TEST_REENCODED_SELECTIONS);
  }

  @Test
  void addSelectionsByPublisherSeriesVolume_deselecting() throws ComicBookSelectionException {
    Mockito.when(
            displayableComicService.getIdsByPublisherSeriesAndVolume(
                Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
        .thenReturn(selectedIds);

    controller.addComicBookSelectionsByPublisherSeriesVolume(
        httpSession,
        principal,
        new AddComicBookSelectionsByPublisherSeriesVolumeRequest(
            TEST_PUBLISHER, TEST_SERIES, TEST_VOLUME, false));

    Mockito.verify(httpSession, Mockito.times(1)).getAttribute(LIBRARY_SELECTIONS);
    Mockito.verify(displayableComicService, Mockito.times(1))
        .getIdsByPublisherSeriesAndVolume(TEST_PUBLISHER, TEST_SERIES, TEST_VOLUME);
    Mockito.verify(selectedIds, Mockito.times(1)).removeAll(selectedIds);
    Mockito.verify(comicSelectionService, Mockito.times(1)).encodeSelections(selectedIds);
    Mockito.verify(httpSession, Mockito.times(1))
        .setAttribute(LIBRARY_SELECTIONS, TEST_REENCODED_SELECTIONS);
  }

  @Test
  void addDuplicateComicBooksToSelections() throws ComicBookSelectionException {
    Mockito.when(displayableComicService.getDuplicateComicIds()).thenReturn(selectedIds);

    controller.addDuplicateComicBooksSelection(
        httpSession, principal, new DuplicateComicBooksSelectionRequest(true));

    Mockito.verify(httpSession, Mockito.times(1)).getAttribute(LIBRARY_SELECTIONS);
    Mockito.verify(displayableComicService, Mockito.times(1)).getDuplicateComicIds();
    Mockito.verify(selectedIds, Mockito.times(1)).addAll(selectedIds);
    Mockito.verify(comicSelectionService, Mockito.times(1)).encodeSelections(selectedIds);
    Mockito.verify(httpSession, Mockito.times(1))
        .setAttribute(LIBRARY_SELECTIONS, TEST_REENCODED_SELECTIONS);
  }

  @Test
  void removeDuplicateComicBooksToSelections() throws ComicBookSelectionException {
    Mockito.when(displayableComicService.getDuplicateComicIds()).thenReturn(selectedIds);

    controller.addDuplicateComicBooksSelection(
        httpSession, principal, new DuplicateComicBooksSelectionRequest(false));

    Mockito.verify(httpSession, Mockito.times(1)).getAttribute(LIBRARY_SELECTIONS);
    Mockito.verify(displayableComicService, Mockito.times(1)).getDuplicateComicIds();
    Mockito.verify(selectedIds, Mockito.times(1)).removeAll(selectedIds);
    Mockito.verify(comicSelectionService, Mockito.times(1)).encodeSelections(selectedIds);
    Mockito.verify(httpSession, Mockito.times(1))
        .setAttribute(LIBRARY_SELECTIONS, TEST_REENCODED_SELECTIONS);
  }

  @Test
  void addUnreadComicBooksSelection_selectingRead()
      throws ComicBookSelectionException, ComiXedUserException {
    controller.addUnreadComicBooksSelection(
        httpSession, principal, new UnreadComicBooksSelectionRequest(true, false));

    Mockito.verify(httpSession, Mockito.times(1)).getAttribute(LIBRARY_SELECTIONS);
    Mockito.verify(userService, Mockito.times(1)).getComicBookIdsForUser(TEST_EMAIL, false);
    Mockito.verify(selectedIds, Mockito.times(1)).addAll(comicDetailIdList);
    Mockito.verify(comicSelectionService, Mockito.times(1)).encodeSelections(selectedIds);
    Mockito.verify(httpSession, Mockito.times(1))
        .setAttribute(LIBRARY_SELECTIONS, TEST_REENCODED_SELECTIONS);
  }

  @Test
  void addUnreadComicBooksSelection_deselectingRead()
      throws ComicBookSelectionException, ComiXedUserException {
    controller.addUnreadComicBooksSelection(
        httpSession, principal, new UnreadComicBooksSelectionRequest(false, false));

    Mockito.verify(httpSession, Mockito.times(1)).getAttribute(LIBRARY_SELECTIONS);
    Mockito.verify(userService, Mockito.times(1)).getComicBookIdsForUser(TEST_EMAIL, false);
    Mockito.verify(selectedIds, Mockito.times(1)).removeAll(comicDetailIdList);
    Mockito.verify(comicSelectionService, Mockito.times(1)).encodeSelections(selectedIds);
    Mockito.verify(httpSession, Mockito.times(1))
        .setAttribute(LIBRARY_SELECTIONS, TEST_REENCODED_SELECTIONS);
  }

  @Test
  void addUnreadComicBooksSelection_selectingUnread()
      throws ComicBookSelectionException, ComiXedUserException {
    controller.addUnreadComicBooksSelection(
        httpSession, principal, new UnreadComicBooksSelectionRequest(true, true));

    Mockito.verify(httpSession, Mockito.times(1)).getAttribute(LIBRARY_SELECTIONS);
    Mockito.verify(userService, Mockito.times(1)).getComicBookIdsForUser(TEST_EMAIL, true);
    Mockito.verify(selectedIds, Mockito.times(1)).addAll(comicDetailIdList);
    Mockito.verify(comicSelectionService, Mockito.times(1)).encodeSelections(selectedIds);
    Mockito.verify(httpSession, Mockito.times(1))
        .setAttribute(LIBRARY_SELECTIONS, TEST_REENCODED_SELECTIONS);
  }

  @Test
  void addUnreadComicBooksSelection_deselectingUnread()
      throws ComicBookSelectionException, ComiXedUserException {
    controller.addUnreadComicBooksSelection(
        httpSession, principal, new UnreadComicBooksSelectionRequest(false, true));

    Mockito.verify(httpSession, Mockito.times(1)).getAttribute(LIBRARY_SELECTIONS);
    Mockito.verify(userService, Mockito.times(1)).getComicBookIdsForUser(TEST_EMAIL, true);
    Mockito.verify(selectedIds, Mockito.times(1)).removeAll(comicDetailIdList);
    Mockito.verify(comicSelectionService, Mockito.times(1)).encodeSelections(selectedIds);
    Mockito.verify(httpSession, Mockito.times(1))
        .setAttribute(LIBRARY_SELECTIONS, TEST_REENCODED_SELECTIONS);
  }

  @Test
  void clearSelections() throws ComicBookSelectionException {
    controller.clearSelections(httpSession, principal);

    Mockito.verify(httpSession, Mockito.times(1)).getAttribute(LIBRARY_SELECTIONS);
    Mockito.verify(comicSelectionService, Mockito.times(1)).encodeSelections(selectedIds);
    Mockito.verify(httpSession, Mockito.times(1))
        .setAttribute(LIBRARY_SELECTIONS, TEST_REENCODED_SELECTIONS);
  }
}
