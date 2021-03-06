/*
 * ComiXed - A digital comic book library management application.
 * Copyright (C) 2020, The ComiXed Project
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

package org.comixedproject.task.encoders;

import static junit.framework.TestCase.*;

import org.comixedproject.model.comic.Comic;
import org.comixedproject.model.tasks.PersistedTask;
import org.comixedproject.service.task.TaskService;
import org.comixedproject.task.MoveComicTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.ObjectFactory;

@RunWith(MockitoJUnitRunner.class)
public class MoveComicTaskEncoderTest {
  private static final String TEST_DIRECTORY = "/Users/comixedreader/Documents/comics";
  private static final String TEST_RENAMING_RULE =
      "$PUBLISHER/$SERIES/$VOLUME/$SERIES v$VOLUME #$ISSUE ($COVERDATE)";

  @InjectMocks private MoveComicTaskEncoder encoder;
  @Mock private TaskService taskService;
  @Mock private ObjectFactory<MoveComicTask> moveComicTaskObjectFactory;
  @Mock private MoveComicTask moveComicTask;
  @Mock Comic comic;

  @Test
  public void testEncode() {
    encoder.setComic(comic);
    encoder.setTargetDirectory(TEST_DIRECTORY);
    encoder.setTargetDirectory(TEST_DIRECTORY);
    encoder.setRenamingRule(TEST_RENAMING_RULE);

    PersistedTask result = encoder.encode();

    assertNotNull(result);
    assertSame(comic, result.getComic());
    assertEquals(TEST_DIRECTORY, result.getProperty(MoveComicTaskEncoder.DIRECTORY));
    assertEquals(TEST_RENAMING_RULE, result.getProperty(MoveComicTaskEncoder.RENAMING_RULE));
  }

  @Test
  public void testDecode() {
    Mockito.when(moveComicTaskObjectFactory.getObject()).thenReturn(moveComicTask);

    PersistedTask persistedTask = new PersistedTask();
    persistedTask.setComic(comic);
    persistedTask.setProperty(MoveComicTaskEncoder.DIRECTORY, TEST_DIRECTORY);
    persistedTask.setProperty(MoveComicTaskEncoder.RENAMING_RULE, TEST_RENAMING_RULE);

    MoveComicTask result = encoder.decode(persistedTask);

    assertNotNull(result);
    assertSame(moveComicTask, result);

    Mockito.verify(taskService, Mockito.times(1)).delete(persistedTask);
    Mockito.verify(moveComicTask, Mockito.times(1)).setComic(comic);
    Mockito.verify(moveComicTask, Mockito.times(1)).setTargetDirectory(TEST_DIRECTORY);
    Mockito.verify(moveComicTask, Mockito.times(1)).setRenamingRule(TEST_RENAMING_RULE);
  }
}
