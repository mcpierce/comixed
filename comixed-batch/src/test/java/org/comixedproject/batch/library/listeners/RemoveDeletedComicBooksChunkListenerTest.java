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

package org.comixedproject.batch.library.listeners;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.comixedproject.batch.library.OrganizeLibraryConfiguration.ORGANIZE_LIBRARY_JOB;
import static org.comixedproject.model.messaging.batch.ProcessComicBooksStatus.REMOVE_DELETED_COMIC_BOOKS_STEP;

import org.comixedproject.messaging.PublishingException;
import org.comixedproject.messaging.batch.PublishBatchProcessDetailUpdateAction;
import org.comixedproject.messaging.comicbooks.PublishProcessComicBooksStatusAction;
import org.comixedproject.model.batch.BatchProcessDetail;
import org.comixedproject.model.messaging.batch.ProcessComicBooksStatus;
import org.comixedproject.service.comicbooks.ComicBookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.batch.core.*;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RemoveDeletedComicBooksChunkListenerTest {
  private static final long TEST_TOTAL_COMICS = 717L;
  private static final long TEST_REMAINING_COMICS = 129L;

  @InjectMocks private RemoveDeletedComicBooksChunkListener listener;
  @Mock private ComicBookService comicBookService;
  @Mock private ChunkContext chunkContext;
  @Mock private StepContext stepContext;
  @Mock private StepExecution stepExecution;
  @Mock private JobInstance jobInstance;
  @Mock private JobExecution jobExecution;
  @Mock private PublishProcessComicBooksStatusAction publishProcessComicBooksStatusAction;
  @Mock private PublishBatchProcessDetailUpdateAction publishBatchProcessDetailUpdateAction;
  @Mock private JobParameters jobParameters;

  @Captor ArgumentCaptor<ProcessComicBooksStatus> processComicStatusArgumentCaptor;
  @Captor ArgumentCaptor<BatchProcessDetail> batchProcessDetailArgumentCaptor;

  @BeforeEach
  public void setUp() throws PublishingException {
    Mockito.when(jobExecution.getJobParameters()).thenReturn(jobParameters);
    Mockito.when(jobInstance.getJobName()).thenReturn(ORGANIZE_LIBRARY_JOB);
    Mockito.when(jobExecution.getJobInstance()).thenReturn(jobInstance);
    Mockito.when(jobExecution.getStatus()).thenReturn(BatchStatus.COMPLETED);
    Mockito.when(jobExecution.getExitStatus()).thenReturn(ExitStatus.COMPLETED);
    Mockito.when(comicBookService.getComicBookCount()).thenReturn(TEST_TOTAL_COMICS);
    Mockito.when(comicBookService.findComicsToPurgeCount()).thenReturn(TEST_REMAINING_COMICS);

    Mockito.when(chunkContext.getStepContext()).thenReturn(stepContext);
    Mockito.when(stepExecution.getJobExecution()).thenReturn(jobExecution);
    Mockito.when(stepContext.getStepExecution()).thenReturn(stepExecution);
    Mockito.doNothing()
        .when(publishProcessComicBooksStatusAction)
        .publish(processComicStatusArgumentCaptor.capture());
    Mockito.doNothing()
        .when(publishBatchProcessDetailUpdateAction)
        .publish(batchProcessDetailArgumentCaptor.capture());
  }

  @Test
  void beforeChunk() throws PublishingException {
    listener.beforeChunk(chunkContext);

    final ProcessComicBooksStatus status = processComicStatusArgumentCaptor.getValue();

    assertNotNull(status);
    assertTrue(status.isActive());
    assertEquals(REMOVE_DELETED_COMIC_BOOKS_STEP, status.getStepName());
    assertEquals(TEST_TOTAL_COMICS, status.getTotal());
    assertEquals(TEST_TOTAL_COMICS - TEST_REMAINING_COMICS, status.getProcessed());

    Mockito.verify(publishProcessComicBooksStatusAction, Mockito.times(1)).publish(status);
  }

  @Test
  void afterChunk() throws PublishingException {
    listener.afterChunk(chunkContext);

    final ProcessComicBooksStatus status = processComicStatusArgumentCaptor.getValue();

    assertNotNull(status);
    assertTrue(status.isActive());
    assertEquals(REMOVE_DELETED_COMIC_BOOKS_STEP, status.getStepName());
    assertEquals(TEST_TOTAL_COMICS, status.getTotal());
    assertEquals(TEST_TOTAL_COMICS - TEST_REMAINING_COMICS, status.getProcessed());

    Mockito.verify(publishProcessComicBooksStatusAction, Mockito.times(1)).publish(status);
  }

  @Test
  void afterChunkError() throws PublishingException {
    listener.afterChunkError(chunkContext);

    final ProcessComicBooksStatus status = processComicStatusArgumentCaptor.getValue();

    assertNotNull(status);
    assertTrue(status.isActive());
    assertEquals(REMOVE_DELETED_COMIC_BOOKS_STEP, status.getStepName());
    assertEquals(TEST_TOTAL_COMICS, status.getTotal());
    assertEquals(TEST_TOTAL_COMICS - TEST_REMAINING_COMICS, status.getProcessed());

    Mockito.verify(publishProcessComicBooksStatusAction, Mockito.times(1)).publish(status);
  }

  @Test
  void afterChunk_publishingException() throws PublishingException {
    Mockito.doThrow(PublishingException.class)
        .when(publishBatchProcessDetailUpdateAction)
        .publish(Mockito.any());
    listener.beforeChunk(chunkContext);

    final ProcessComicBooksStatus status = processComicStatusArgumentCaptor.getValue();

    assertNotNull(status);
    assertTrue(status.isActive());
    assertEquals(REMOVE_DELETED_COMIC_BOOKS_STEP, status.getStepName());
    assertEquals(TEST_TOTAL_COMICS, status.getTotal());
    assertEquals(TEST_TOTAL_COMICS - TEST_REMAINING_COMICS, status.getProcessed());

    Mockito.verify(publishProcessComicBooksStatusAction, Mockito.times(1)).publish(status);
  }
}
