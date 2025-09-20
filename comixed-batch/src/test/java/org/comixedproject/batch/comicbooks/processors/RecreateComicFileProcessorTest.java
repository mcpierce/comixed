/*
 * ComiXed - A digital comicBook book library management application.
 * Copyright (C) 2021, The ComiXed Project
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

package org.comixedproject.batch.comicbooks.processors;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertSame;

import java.io.File;
import org.comixedproject.adaptors.AdaptorException;
import org.comixedproject.adaptors.comicbooks.ComicBookAdaptor;
import org.comixedproject.batch.LendingLibraryAction;
import org.comixedproject.batch.LendingLibraryManager;
import org.comixedproject.model.archives.ArchiveType;
import org.comixedproject.model.comicbooks.ComicBook;
import org.comixedproject.model.comicbooks.ComicDetail;
import org.comixedproject.service.admin.ConfigurationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RecreateComicFileProcessorTest {
  private static final ArchiveType TEST_TARGET_ARCHIVE = ArchiveType.CBZ;
  private static final String TEST_PAGE_RENAMING_RULE = "The page renaming rule";
  private static final long TEST_COMIC_BOOK_ID = 717L;

  @InjectMocks private RecreateComicFileProcessor processor;
  @Mock private LendingLibraryManager lendingLibraryManager;
  @Mock private ConfigurationService configurationService;
  @Mock private ComicBookAdaptor comicBookAdaptor;
  @Mock private File comicFile;
  @Mock private ComicDetail comicDetail;
  @Mock private ComicBook comicBook;
  @Mock private ComicBook processedComicBook;

  @Captor private ArgumentCaptor<LendingLibraryAction> lendingLibraryActionArgumentCaptor;

  @BeforeEach
  public void setUp() {
    Mockito.when(comicFile.exists()).thenReturn(true);
    Mockito.when(comicFile.isFile()).thenReturn(true);
    Mockito.when(comicDetail.getFile()).thenReturn(comicFile);
    Mockito.when(comicBook.getComicBookId()).thenReturn(TEST_COMIC_BOOK_ID);
    Mockito.when(comicBook.getComicDetail()).thenReturn(comicDetail);
    Mockito.when(comicBook.getTargetArchiveType()).thenReturn(TEST_TARGET_ARCHIVE);
    Mockito.when(comicBook.isDeletePages()).thenReturn(false);
    Mockito.when(
            configurationService.getOptionValue(
                ConfigurationService.CFG_LIBRARY_PAGE_RENAMING_RULE, ""))
        .thenReturn(TEST_PAGE_RENAMING_RULE);
  }

  @Test
  void process() {
    Mockito.when(
            lendingLibraryManager.executeAction(
                Mockito.any(ComicBook.class),
                Mockito.anyLong(),
                lendingLibraryActionArgumentCaptor.capture()))
        .thenReturn(processedComicBook);

    final ComicBook result = processor.process(comicBook);

    assertNotNull(result);
    assertSame(processedComicBook, result);

    final LendingLibraryAction action = lendingLibraryActionArgumentCaptor.getValue();
    assertSame(comicBook, action.execute(comicBook));

    Mockito.verify(lendingLibraryManager, Mockito.times(1))
        .executeAction(comicBook, TEST_COMIC_BOOK_ID, action);
  }

  @Test
  void doProcessing_deleteMarkedPages() throws Exception {
    Mockito.when(comicBook.isDeletePages()).thenReturn(true);

    final ComicBook result = processor.doProcessing(comicBook);

    assertNotNull(result);
    assertSame(comicBook, result);

    Mockito.verify(comicBookAdaptor, Mockito.times(1))
        .save(comicBook, TEST_TARGET_ARCHIVE, true, TEST_PAGE_RENAMING_RULE);
  }

  @Test
  void doProcessing_adaptorExceptionOnSave() throws Exception {
    Mockito.doThrow(AdaptorException.class)
        .when(comicBookAdaptor)
        .save(
            Mockito.any(ComicBook.class),
            Mockito.any(ArchiveType.class),
            Mockito.anyBoolean(),
            Mockito.anyString());

    final ComicBook result = processor.doProcessing(comicBook);

    assertNotNull(result);
    assertSame(comicBook, result);

    Mockito.verify(comicBook, Mockito.never()).removeDeletedPages();
    Mockito.verify(comicBookAdaptor, Mockito.times(1))
        .save(comicBook, TEST_TARGET_ARCHIVE, false, TEST_PAGE_RENAMING_RULE);
  }

  @Test
  void doProcessing_sourceNotFound() throws Exception {
    Mockito.when(comicFile.exists()).thenReturn(false);

    final ComicBook result = processor.doProcessing(comicBook);

    assertNotNull(result);
    assertSame(comicBook, result);

    Mockito.verify(comicBook, Mockito.never()).removeDeletedPages();
    Mockito.verify(comicBookAdaptor, Mockito.never())
        .save(Mockito.any(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyString());
  }

  @Test
  void doProcessing_sourceNotFile() throws Exception {
    Mockito.when(comicFile.isFile()).thenReturn(false);

    final ComicBook result = processor.doProcessing(comicBook);

    assertNotNull(result);
    assertSame(comicBook, result);

    Mockito.verify(comicBook, Mockito.never()).removeDeletedPages();
    Mockito.verify(comicBookAdaptor, Mockito.never())
        .save(Mockito.any(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyString());
  }

  @Test
  void doProcessing() throws Exception {
    final ComicBook result = processor.doProcessing(comicBook);

    assertNotNull(result);
    assertSame(comicBook, result);

    Mockito.verify(comicBook, Mockito.never()).removeDeletedPages();
    Mockito.verify(comicBookAdaptor, Mockito.times(1))
        .save(comicBook, TEST_TARGET_ARCHIVE, false, TEST_PAGE_RENAMING_RULE);
  }
}
