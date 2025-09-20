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
import static org.comixedproject.service.admin.ConfigurationService.CFG_LIBRARY_NO_COMICINFO_ENTRY;
import static org.comixedproject.service.admin.ConfigurationService.CFG_LIBRARY_NO_RECREATE_COMICS;
import static org.comixedproject.service.admin.ConfigurationService.CREATE_EXTERNAL_METADATA_FILE;

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
class UpdateMetadataProcessorTest {
  private static final ArchiveType TEST_ARCHIVE_TYPE = ArchiveType.CB7;
  private static final long TEST_COMIC_BOOK_ID = 717L;

  @InjectMocks private UpdateMetadataProcessor processor;
  @Mock private LendingLibraryManager lendingLibraryManager;
  @Mock private ComicBookAdaptor comicBookAdaptor;
  @Mock private ConfigurationService configurationService;
  @Mock private ComicBook comicBook;
  @Mock private ComicBook processedComicBook;
  @Mock private ComicDetail comicDetail;

  @Captor private ArgumentCaptor<LendingLibraryAction> lendingLibraryActionArgumentCaptor;

  @BeforeEach
  public void setUp() {
    Mockito.when(comicBook.getComicDetail()).thenReturn(comicDetail);
    Mockito.when(comicBook.getComicBookId()).thenReturn(TEST_COMIC_BOOK_ID);
    Mockito.when(comicDetail.getArchiveType()).thenReturn(TEST_ARCHIVE_TYPE);
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
  void doProcessing_updateException() throws Exception {
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

    Mockito.verify(comicBookAdaptor, Mockito.times(1))
        .save(comicBook, TEST_ARCHIVE_TYPE, false, "");
  }

  @Test
  void doProcessing_createExternalFileThrowsException() throws Exception {
    Mockito.when(configurationService.isFeatureEnabled(CREATE_EXTERNAL_METADATA_FILE))
        .thenReturn(true);
    Mockito.doThrow(AdaptorException.class)
        .when(comicBookAdaptor)
        .saveMetadataFile(Mockito.any(ComicBook.class));

    final ComicBook result = processor.doProcessing(comicBook);

    assertNotNull(result);
    assertSame(comicBook, result);

    Mockito.verify(configurationService, Mockito.times(1))
        .isFeatureEnabled(CREATE_EXTERNAL_METADATA_FILE);
    Mockito.verify(comicBookAdaptor, Mockito.times(1)).saveMetadataFile(comicBook);
  }

  @Test
  void doProcessing_createExternalFile() throws Exception {
    Mockito.when(configurationService.isFeatureEnabled(CREATE_EXTERNAL_METADATA_FILE))
        .thenReturn(true);

    final ComicBook result = processor.doProcessing(comicBook);

    assertNotNull(result);
    assertSame(comicBook, result);

    Mockito.verify(configurationService, Mockito.times(1))
        .isFeatureEnabled(CREATE_EXTERNAL_METADATA_FILE);
    Mockito.verify(comicBookAdaptor, Mockito.times(1)).saveMetadataFile(comicBook);
  }

  @Test
  void doProcessing_forRarFile() throws Exception {
    Mockito.when(comicDetail.getArchiveType()).thenReturn(ArchiveType.CBR);

    final ComicBook result = processor.doProcessing(comicBook);

    assertNotNull(result);
    assertSame(comicBook, result);

    Mockito.verify(comicBookAdaptor, Mockito.never())
        .save(
            Mockito.any(ComicBook.class),
            Mockito.any(ArchiveType.class),
            Mockito.anyBoolean(),
            Mockito.anyString());
  }

  @Test
  void doProcessing_noComicInfoFileEnabled() throws Exception {
    Mockito.when(configurationService.isFeatureEnabled(CFG_LIBRARY_NO_COMICINFO_ENTRY))
        .thenReturn(true);

    final ComicBook result = processor.doProcessing(comicBook);

    assertNotNull(result);
    assertSame(comicBook, result);

    Mockito.verify(comicBookAdaptor, Mockito.never())
        .save(
            Mockito.any(ComicBook.class),
            Mockito.any(ArchiveType.class),
            Mockito.anyBoolean(),
            Mockito.anyString());
  }

  @Test
  void doProcessing_noRecreateComicFileAlowed() throws Exception {
    Mockito.when(configurationService.isFeatureEnabled(CFG_LIBRARY_NO_RECREATE_COMICS))
        .thenReturn(true);

    final ComicBook result = processor.doProcessing(comicBook);

    assertNotNull(result);
    assertSame(comicBook, result);

    Mockito.verify(comicBookAdaptor, Mockito.never())
        .save(
            Mockito.any(ComicBook.class),
            Mockito.any(ArchiveType.class),
            Mockito.anyBoolean(),
            Mockito.anyString());
  }

  @Test
  void doProcessing() throws Exception {
    Mockito.when(configurationService.isFeatureEnabled(CREATE_EXTERNAL_METADATA_FILE))
        .thenReturn(true);

    final ComicBook result = processor.doProcessing(comicBook);

    assertNotNull(result);
    assertSame(comicBook, result);

    Mockito.verify(comicBookAdaptor, Mockito.times(1))
        .save(comicBook, TEST_ARCHIVE_TYPE, false, "");
    Mockito.verify(configurationService, Mockito.times(1))
        .isFeatureEnabled(CREATE_EXTERNAL_METADATA_FILE);
  }
}
