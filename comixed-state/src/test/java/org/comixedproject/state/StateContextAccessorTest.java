/*
 * ComiXed - A digital comic book library management application.
 * Copyright (C) 2025, The ComiXed Project
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

package org.comixedproject.state;

import static org.comixedproject.state.comicbooks.ComicStateHandler.HEADER_COMIC;
import static org.comixedproject.state.lists.ReadingListStateHandler.HEADER_READING_LIST;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.comixedproject.model.comicbooks.ComicBook;
import org.comixedproject.model.lists.ReadingList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.MessageHeaders;
import org.springframework.statemachine.StateContext;

@ExtendWith(MockitoExtension.class)
class StateContextAccessorTest {
  @InjectMocks private StateContextAccessor accessor;
  @Mock private MessageHeaders messageHeaders;
  @Mock private StateContext<?, ?> context;
  @Mock private ComicBook comicBook;
  @Mock private ReadingList readingList;

  @BeforeEach
  void setUp() {
    Mockito.when(context.getMessageHeaders()).thenReturn(messageHeaders);
  }

  @Test
  void fetchComic() {
    Mockito.when(messageHeaders.get(HEADER_COMIC, ComicBook.class)).thenReturn(comicBook);

    final ComicBook result = accessor.fetchComic(context);

    assertNotNull(result);
    assertSame(comicBook, result);
  }

  @Test
  void fetchReadingList() {

    Mockito.when(messageHeaders.get(HEADER_READING_LIST, ReadingList.class))
        .thenReturn(readingList);

    final ReadingList result = accessor.fetchReadingList(context);

    assertNotNull(result);
    assertSame(readingList, result);
  }
}
