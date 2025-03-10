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

package org.comixedproject.batch.library.processors;

import static junit.framework.TestCase.*;
import static org.comixedproject.batch.library.OrganizeLibraryConfiguration.ORGANIZE_LIBRARY_JOB_RENAMING_RULE;
import static org.comixedproject.batch.library.OrganizeLibraryConfiguration.ORGANIZE_LIBRARY_JOB_TARGET_DIRECTORY;
import static org.comixedproject.service.admin.ConfigurationService.CFG_DONT_MOVE_UNSCRAPED_COMICS;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.comixedproject.adaptors.comicbooks.ComicBookAdaptor;
import org.comixedproject.adaptors.comicbooks.ComicFileAdaptor;
import org.comixedproject.adaptors.file.FileAdaptor;
import org.comixedproject.model.archives.ArchiveType;
import org.comixedproject.model.library.OrganizingComic;
import org.comixedproject.model.library.PublicationDetail;
import org.comixedproject.service.admin.ConfigurationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MoveComicFilesProcessorTest {
  private static final String TEST_TARGET_DIRECTORY = "the target directory";
  private static final String TEST_RENAMING_RULE = "the renaming rule";
  private static final ArchiveType TEST_ARCHIVE_TYPE = ArchiveType.CBZ;
  private static final String TEST_REBUILT_FILENAME =
      new File("target/test-classes/rebuilt-example.cbz").getAbsolutePath();
  private static final String TEST_REBUILT_FILENAME_FROM_RULE =
      new File(TEST_REBUILT_FILENAME).getName();
  private static final String TEST_SOURCE_FILENAME =
      new File("target/test-classes/example.cbz").getAbsolutePath();
  private static final String TEST_SOURCE_METADATA_FILENAME =
      new File("target/test-classes/example-metadata.xml").getAbsolutePath();
  private static final String TEST_SOURCE_METADATA_FILENAME_NOT_EXISTS =
      new File("target/test-classes/example-metadata-farkle.xml").getAbsolutePath();
  private static final String TEST_TARGET_METADATA_FILENAME =
      new File("target/test-classes/example-metadata.xml-out").getAbsolutePath();

  @InjectMocks private MoveComicFilesProcessor processor;
  @Mock private ConfigurationService configurationService;
  @Mock private JobParameters jobParameters;
  @Mock private JobExecution jobExecution;
  @Mock private StepExecution stepExecution;
  @Mock private FileAdaptor fileAdaptor;
  @Mock private ComicFileAdaptor comicFileAdaptor;
  @Mock private ComicBookAdaptor comicBookAdaptor;
  @Mock private OrganizingComic organizingComic;
  @Mock private File comicDetailFile;

  @Captor private ArgumentCaptor<File> moveFileSourceArgumentCaptor;
  @Captor private ArgumentCaptor<File> moveFileTargetArgumentCaptor;

  @BeforeEach
  public void setUp() throws IOException {
    Mockito.when(configurationService.isFeatureEnabled(CFG_DONT_MOVE_UNSCRAPED_COMICS))
        .thenReturn(false);
    Mockito.when(jobParameters.getString(ORGANIZE_LIBRARY_JOB_TARGET_DIRECTORY))
        .thenReturn(TEST_TARGET_DIRECTORY);
    Mockito.when(jobParameters.getString(ORGANIZE_LIBRARY_JOB_RENAMING_RULE))
        .thenReturn(TEST_RENAMING_RULE);
    Mockito.when(organizingComic.getArchiveType()).thenReturn(TEST_ARCHIVE_TYPE);
    Mockito.when(comicDetailFile.exists()).thenReturn(true);
    Mockito.when(organizingComic.getFile()).thenReturn(comicDetailFile);
    Mockito.when(organizingComic.getFilename()).thenReturn(TEST_SOURCE_FILENAME);
    Mockito.when(organizingComic.getFilename()).thenReturn(TEST_SOURCE_FILENAME);
    Mockito.when(
            comicFileAdaptor.findAvailableFilename(
                Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyString()))
        .thenReturn(TEST_REBUILT_FILENAME);
    Mockito.when(comicFileAdaptor.standardizeFilename(Mockito.anyString()))
        .thenReturn(TEST_REBUILT_FILENAME_FROM_RULE);
    Mockito.when(
            comicFileAdaptor.createFilenameFromRule(
                Mockito.any(PublicationDetail.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString()))
        .thenReturn(TEST_REBUILT_FILENAME_FROM_RULE);
    Mockito.when(comicBookAdaptor.getMetadataFilename(Mockito.anyString()))
        .thenReturn(TEST_SOURCE_METADATA_FILENAME_NOT_EXISTS);
    Mockito.when(fileAdaptor.sameFile(Mockito.any(File.class), Mockito.any(File.class)))
        .thenReturn(false, false);

    Mockito.doNothing()
        .when(fileAdaptor)
        .moveFile(moveFileSourceArgumentCaptor.capture(), moveFileTargetArgumentCaptor.capture());
  }

  @Test
  void process_comicbookNotFound() {
    Mockito.when(comicDetailFile.exists()).thenReturn(false);

    final OrganizingComic result = processor.process(organizingComic);

    assertNotNull(result);
    assertSame(organizingComic, result);

    Mockito.verify(organizingComic, Mockito.never()).setUpdatedFilename(TEST_REBUILT_FILENAME);
  }

  @Test
  void process_comicbookNotScrapedFeatureOn() throws IOException {
    Mockito.when(configurationService.isFeatureEnabled(CFG_DONT_MOVE_UNSCRAPED_COMICS))
        .thenReturn(true);
    Mockito.when(organizingComic.isScraped()).thenReturn(true);

    final OrganizingComic result = processor.process(organizingComic);

    assertNotNull(result);
    assertSame(organizingComic, result);

    List<File> moveFileSources = moveFileSourceArgumentCaptor.getAllValues();
    List<File> moveFileTargets = moveFileTargetArgumentCaptor.getAllValues();

    File comicDetailSourceFile = moveFileSources.get(0);
    assertNotNull(comicDetailSourceFile);
    assertSame(comicDetailFile, comicDetailSourceFile);

    File rebuiltComicBookFile = moveFileTargets.get(0);
    assertNotNull(rebuiltComicBookFile);
    assertEquals(TEST_REBUILT_FILENAME, rebuiltComicBookFile.getAbsolutePath());

    Mockito.verify(fileAdaptor, Mockito.times(1)).moveFile(comicDetailFile, rebuiltComicBookFile);
    Mockito.verify(comicFileAdaptor, Mockito.times(1)).standardizeFilename(TEST_REBUILT_FILENAME);
  }

  @Test
  void process_comicbookNotScrapedFeatureOnAndNoMetadata() {
    Mockito.when(configurationService.isFeatureEnabled(CFG_DONT_MOVE_UNSCRAPED_COMICS))
        .thenReturn(true);
    Mockito.when(organizingComic.isScraped()).thenReturn(false);

    final OrganizingComic result = processor.process(organizingComic);

    assertNotNull(result);
    assertSame(organizingComic, result);

    Mockito.verify(organizingComic, Mockito.never()).setUpdatedFilename(TEST_REBUILT_FILENAME);
  }

  @Test
  void process() throws IOException {
    processor.process(organizingComic);

    List<File> moveFileSources = moveFileSourceArgumentCaptor.getAllValues();
    List<File> moveFileTargets = moveFileTargetArgumentCaptor.getAllValues();

    File comicDetailSourceFile = moveFileSources.get(0);
    assertNotNull(comicDetailSourceFile);
    assertSame(comicDetailFile, comicDetailSourceFile);

    File rebuiltComicBookFile = moveFileTargets.get(0);
    assertNotNull(rebuiltComicBookFile);
    assertEquals(TEST_REBUILT_FILENAME, rebuiltComicBookFile.getAbsolutePath());

    Mockito.verify(fileAdaptor, Mockito.times(1)).moveFile(comicDetailFile, rebuiltComicBookFile);
    Mockito.verify(comicFileAdaptor, Mockito.times(1)).standardizeFilename(TEST_REBUILT_FILENAME);
  }

  @Test
  void process_sourceAndComicFileAreTheSame() throws IOException {
    Mockito.when(fileAdaptor.sameFile(Mockito.any(File.class), Mockito.any(File.class)))
        .thenReturn(true);

    processor.process(organizingComic);

    Mockito.verify(fileAdaptor, Mockito.never())
        .moveFile(Mockito.any(File.class), Mockito.any(File.class));
  }

  @Test
  void process_movingFileThrowsException() throws IOException {
    Mockito.doThrow(IOException.class)
        .when(fileAdaptor)
        .moveFile(moveFileSourceArgumentCaptor.capture(), moveFileTargetArgumentCaptor.capture());

    processor.process(organizingComic);

    List<File> moveFileSources = moveFileSourceArgumentCaptor.getAllValues();
    List<File> moveFileTargets = moveFileTargetArgumentCaptor.getAllValues();

    assertEquals(moveFileTargets.size(), 1);
    assertEquals(moveFileTargets.size(), 1);

    File metadataFileTarget = moveFileTargets.get(0);
    assertEquals(TEST_REBUILT_FILENAME, metadataFileTarget.getAbsolutePath());

    File metadataFileSource = moveFileSources.get(0);
    assertNull(metadataFileSource.getAbsolutePath());

    Mockito.verify(fileAdaptor, Mockito.times(1)).moveFile(metadataFileSource, metadataFileTarget);
    Mockito.verify(comicFileAdaptor, Mockito.never()).standardizeFilename(Mockito.any());
    Mockito.verify(organizingComic, Mockito.never()).setUpdatedFilename(TEST_REBUILT_FILENAME);
  }

  @Test
  void process_metadataFileExists() throws IOException {
    Mockito.when(comicBookAdaptor.getMetadataFilename(Mockito.anyString()))
        .thenReturn(TEST_SOURCE_METADATA_FILENAME, TEST_TARGET_METADATA_FILENAME);
    Mockito.when(fileAdaptor.sameFile(Mockito.any(File.class), Mockito.any(File.class)))
        .thenReturn(true, false);
    Mockito.when(organizingComic.getUpdatedFilename()).thenReturn(TEST_REBUILT_FILENAME);

    processor.process(organizingComic);

    List<File> moveFileSources = moveFileSourceArgumentCaptor.getAllValues();
    List<File> moveFileTargets = moveFileTargetArgumentCaptor.getAllValues();

    assertEquals(moveFileTargets.size(), 1);
    assertEquals(moveFileTargets.size(), 1);

    File metadataFileSource = moveFileSources.get(0);
    assertEquals(TEST_SOURCE_METADATA_FILENAME, metadataFileSource.getAbsolutePath());

    File metadataFileTarget = moveFileTargets.get(0);
    assertEquals(TEST_TARGET_METADATA_FILENAME, metadataFileTarget.getAbsolutePath());

    Mockito.verify(fileAdaptor, Mockito.times(1)).moveFile(metadataFileSource, metadataFileTarget);
    Mockito.verify(comicFileAdaptor, Mockito.never()).standardizeFilename(Mockito.any());
  }

  @Test
  void process_metadataFileExistsAndIsAlreadyInPlace() throws IOException {
    Mockito.when(fileAdaptor.sameFile(Mockito.any(File.class), Mockito.any(File.class)))
        .thenReturn(true, true);

    processor.process(organizingComic);

    Mockito.verify(fileAdaptor, Mockito.never())
        .moveFile(Mockito.any(File.class), Mockito.any(File.class));
  }

  @Test
  void beforeStep() {
    Mockito.when(jobExecution.getJobParameters()).thenReturn(jobParameters);
    Mockito.when(stepExecution.getJobExecution()).thenReturn(jobExecution);

    processor.beforeStep(stepExecution);

    assertNotNull(processor.jobParameters);
    assertSame(jobParameters, processor.jobParameters);
  }

  @Test
  void afterStep() {
    assertNull(processor.afterStep(stepExecution));
  }
}
