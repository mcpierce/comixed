/*
 * ComiXed - A digital comic book library management application.
 * Copyright (C) 2017, The ComiXed Project
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

package org.comixedproject.task;

import static junit.framework.TestCase.assertFalse;

import java.io.File;
import org.comixedproject.adaptors.AdaptorException;
import org.comixedproject.adaptors.FilenameScraperAdaptor;
import org.comixedproject.handlers.ComicFileHandler;
import org.comixedproject.handlers.ComicFileHandlerException;
import org.comixedproject.messaging.PublishingException;
import org.comixedproject.messaging.comic.PublishComicUpdateAction;
import org.comixedproject.model.comic.Comic;
import org.comixedproject.model.comic.ComicState;
import org.comixedproject.model.tasks.PersistedTask;
import org.comixedproject.service.comic.ComicService;
import org.comixedproject.service.task.TaskService;
import org.comixedproject.task.encoders.ProcessComicTaskEncoder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
@SpringBootTest
public class AddComicTaskTest {
  private static final String TEST_CBZ_FILE = "src/test/resources/example.cbz";

  @InjectMocks private AddComicTask task;
  @Mock private ComicFileHandler comicFileHandler;
  @Mock private ComicService comicService;
  @Mock private ObjectFactory<Comic> comicFactory;
  @Mock private Comic comic;
  @Mock private FilenameScraperAdaptor filenameScraperAdaptor;
  @Mock private ObjectFactory<ProcessComicTaskEncoder> processComicTaskEncoderObjectFactory;
  @Mock private ProcessComicTaskEncoder processComicTaskEncoder;
  @Mock private PersistedTask workerPersistedTask;
  @Mock private TaskService taskService;
  @Mock private PublishComicUpdateAction publishComicUpdateAction;

  @Test
  public void testCreateDescription() {
    assertFalse(task.createDescription().isEmpty());
  }

  @Test
  public void testStartTaskAlreadyImported() throws TaskException {
    Mockito.when(comicService.findByFilename(Mockito.anyString())).thenReturn(comic);

    task.setFilename(TEST_CBZ_FILE);

    task.startTask();

    Mockito.verify(comicService, Mockito.times(1))
        .findByFilename(new File(TEST_CBZ_FILE).getAbsolutePath());
    Mockito.verify(comicFactory, Mockito.never()).getObject();
  }

  @Test(expected = TaskException.class)
  public void testStartTaskLoadException() throws TaskException, ComicFileHandlerException {
    Mockito.when(comicService.findByFilename(Mockito.anyString())).thenReturn(null);
    Mockito.when(comicFactory.getObject()).thenReturn(comic);
    Mockito.doThrow(ComicFileHandlerException.class)
        .when(comicFileHandler)
        .loadComicArchiveType(Mockito.any(Comic.class));

    task.setFilename(TEST_CBZ_FILE);

    try {
      task.startTask();
    } finally {
      Mockito.verify(comicService, Mockito.times(1))
          .findByFilename(new File(TEST_CBZ_FILE).getAbsolutePath());
      Mockito.verify(comicFactory, Mockito.times(1)).getObject();
      Mockito.verify(comicFileHandler, Mockito.times(1)).loadComicArchiveType(comic);
    }
  }

  @Test
  public void testStartTask()
      throws TaskException, ComicFileHandlerException, AdaptorException, PublishingException {
    Mockito.when(comicService.findByFilename(Mockito.anyString())).thenReturn(null);
    Mockito.when(comicFactory.getObject()).thenReturn(comic);
    Mockito.when(comicService.save(Mockito.any(Comic.class))).thenReturn(comic);
    Mockito.when(processComicTaskEncoderObjectFactory.getObject())
        .thenReturn(processComicTaskEncoder);
    Mockito.when(processComicTaskEncoder.encode()).thenReturn(workerPersistedTask);

    task.setDeleteBlockedPages(false);
    task.setIgnoreMetadata(false);

    task.setFilename(TEST_CBZ_FILE);
    task.startTask();

    Mockito.verify(comicService, Mockito.times(1))
        .findByFilename(new File(TEST_CBZ_FILE).getAbsolutePath());
    Mockito.verify(comicFactory, Mockito.times(1)).getObject();
    Mockito.verify(comic, Mockito.times(1)).setFilename(new File(TEST_CBZ_FILE).getAbsolutePath());
    Mockito.verify(filenameScraperAdaptor, Mockito.times(1)).execute(comic);
    Mockito.verify(comicFileHandler, Mockito.times(1)).loadComicArchiveType(comic);
    Mockito.verify(comic, Mockito.times(1)).setComicState(ComicState.ADDED);
    Mockito.verify(comicService, Mockito.times(1)).save(comic);
    Mockito.verify(processComicTaskEncoder, Mockito.times(1)).setComic(comic);
    Mockito.verify(processComicTaskEncoder, Mockito.times(1)).setDeleteBlockedPages(false);
    Mockito.verify(processComicTaskEncoder, Mockito.times(1)).setIgnoreMetadata(false);
    Mockito.verify(taskService, Mockito.times(1)).save(workerPersistedTask);
    Mockito.verify(publishComicUpdateAction, Mockito.times(1)).publish(comic);
  }

  @Test
  public void testStartTaskIgnoreMetadata()
      throws TaskException, ComicFileHandlerException, AdaptorException, PublishingException {
    Mockito.when(comicService.findByFilename(Mockito.anyString())).thenReturn(null);
    Mockito.when(comicFactory.getObject()).thenReturn(comic);
    Mockito.when(comicService.save(Mockito.any(Comic.class))).thenReturn(comic);
    Mockito.when(processComicTaskEncoderObjectFactory.getObject())
        .thenReturn(processComicTaskEncoder);
    Mockito.when(processComicTaskEncoder.encode()).thenReturn(workerPersistedTask);

    task.setDeleteBlockedPages(false);
    task.setIgnoreMetadata(true);

    task.setFilename(TEST_CBZ_FILE);
    task.startTask();

    Mockito.verify(comicService, Mockito.times(1))
        .findByFilename(new File(TEST_CBZ_FILE).getAbsolutePath());
    Mockito.verify(comicFactory, Mockito.times(1)).getObject();
    Mockito.verify(comic, Mockito.times(1)).setFilename(new File(TEST_CBZ_FILE).getAbsolutePath());
    Mockito.verify(filenameScraperAdaptor, Mockito.times(1)).execute(comic);
    Mockito.verify(comic, Mockito.times(1)).setComicState(ComicState.ADDED);
    Mockito.verify(comicFileHandler, Mockito.times(1)).loadComicArchiveType(comic);
    Mockito.verify(comicService, Mockito.times(1)).save(comic);
    Mockito.verify(processComicTaskEncoder, Mockito.times(1)).setComic(comic);
    Mockito.verify(processComicTaskEncoder, Mockito.times(1)).setDeleteBlockedPages(false);
    Mockito.verify(processComicTaskEncoder, Mockito.times(1)).setIgnoreMetadata(true);
    Mockito.verify(taskService, Mockito.times(1)).save(workerPersistedTask);
    Mockito.verify(publishComicUpdateAction, Mockito.times(1)).publish(comic);
  }

  @Test
  public void testStartTaskDeleteBlockedPages()
      throws TaskException, ComicFileHandlerException, AdaptorException, PublishingException {
    Mockito.when(comicService.findByFilename(Mockito.anyString())).thenReturn(null);
    Mockito.when(comicFactory.getObject()).thenReturn(comic);
    Mockito.when(comicService.save(Mockito.any(Comic.class))).thenReturn(comic);
    Mockito.when(processComicTaskEncoderObjectFactory.getObject())
        .thenReturn(processComicTaskEncoder);
    Mockito.when(processComicTaskEncoder.encode()).thenReturn(workerPersistedTask);

    task.setDeleteBlockedPages(true);
    task.setIgnoreMetadata(false);

    task.setFilename(TEST_CBZ_FILE);
    task.startTask();

    Mockito.verify(comicService, Mockito.times(1))
        .findByFilename(new File(TEST_CBZ_FILE).getAbsolutePath());
    Mockito.verify(comicFactory, Mockito.times(1)).getObject();
    Mockito.verify(comic, Mockito.times(1)).setFilename(new File(TEST_CBZ_FILE).getAbsolutePath());
    Mockito.verify(filenameScraperAdaptor, Mockito.times(1)).execute(comic);
    Mockito.verify(comicFileHandler, Mockito.times(1)).loadComicArchiveType(comic);
    Mockito.verify(comic, Mockito.times(1)).setComicState(ComicState.ADDED);
    Mockito.verify(comicService, Mockito.times(1)).save(comic);
    Mockito.verify(processComicTaskEncoder, Mockito.times(1)).setComic(comic);
    Mockito.verify(processComicTaskEncoder, Mockito.times(1)).setDeleteBlockedPages(true);
    Mockito.verify(processComicTaskEncoder, Mockito.times(1)).setIgnoreMetadata(false);
    Mockito.verify(taskService, Mockito.times(1)).save(workerPersistedTask);
    Mockito.verify(publishComicUpdateAction, Mockito.times(1)).publish(comic);
  }

  @Test
  public void testStartTaskDeleteBlockedPagesIgnoreMetadata()
      throws TaskException, ComicFileHandlerException, AdaptorException, PublishingException {
    Mockito.when(comicService.findByFilename(Mockito.anyString())).thenReturn(null);
    Mockito.when(comicFactory.getObject()).thenReturn(comic);
    Mockito.when(comicService.save(Mockito.any(Comic.class))).thenReturn(comic);
    Mockito.when(processComicTaskEncoderObjectFactory.getObject())
        .thenReturn(processComicTaskEncoder);
    Mockito.when(processComicTaskEncoder.encode()).thenReturn(workerPersistedTask);

    task.setDeleteBlockedPages(true);
    task.setIgnoreMetadata(true);

    task.setFilename(TEST_CBZ_FILE);
    task.startTask();

    Mockito.verify(comicService, Mockito.times(1))
        .findByFilename(new File(TEST_CBZ_FILE).getAbsolutePath());
    Mockito.verify(comicFactory, Mockito.times(1)).getObject();
    Mockito.verify(comic, Mockito.times(1)).setFilename(new File(TEST_CBZ_FILE).getAbsolutePath());
    Mockito.verify(filenameScraperAdaptor, Mockito.times(1)).execute(comic);
    Mockito.verify(comicFileHandler, Mockito.times(1)).loadComicArchiveType(comic);
    Mockito.verify(comic, Mockito.times(1)).setComicState(ComicState.ADDED);
    Mockito.verify(comicService, Mockito.times(1)).save(comic);
    Mockito.verify(processComicTaskEncoder, Mockito.times(1)).setComic(comic);
    Mockito.verify(processComicTaskEncoder, Mockito.times(1)).setDeleteBlockedPages(true);
    Mockito.verify(processComicTaskEncoder, Mockito.times(1)).setIgnoreMetadata(true);
    Mockito.verify(taskService, Mockito.times(1)).save(workerPersistedTask);
    Mockito.verify(publishComicUpdateAction, Mockito.times(1)).publish(comic);
  }
}
