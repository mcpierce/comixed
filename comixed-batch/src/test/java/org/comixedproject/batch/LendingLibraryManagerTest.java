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

package org.comixedproject.batch;

import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.concurrent.TimeoutException;
import lombok.NonNull;
import net.jodah.concurrentunit.Waiter;
import org.comixedproject.model.comicbooks.ComicBook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LendingLibraryManagerTest {
  private static final Long TEST_COMIC_BOOK_ID = 717L;

  @InjectMocks private LendingLibraryManager manager;
  @Mock private @NonNull ComicBook comicBook;
  @Mock private ComicBook updatedComicBook;

  @Test
  void executeAction() throws InterruptedException, TimeoutException {
    final Waiter waiter = new Waiter();

    new Thread(
            () -> {
              final ComicBook result =
                  manager.executeAction(
                      comicBook,
                      TEST_COMIC_BOOK_ID,
                      (input) -> {
                        waiter.assertTrue(true);
                        return updatedComicBook;
                      });
              assertSame(updatedComicBook, result);
              waiter.resume();
            })
        .start();

    waiter.await(1000L);
  }

  @Test
  void executeAction_alreadyInUse() throws InterruptedException, TimeoutException {
    final Waiter waiter = new Waiter();

    this.doRun(waiter);
    this.doRun(waiter);

    waiter.await(1000L);
  }

  private void doRun(final Waiter waiter) {
    new Thread(
            () -> {
              final ComicBook result =
                  manager.executeAction(
                      comicBook,
                      TEST_COMIC_BOOK_ID,
                      (input) -> {
                        waiter.assertTrue(manager.catalog.contains(TEST_COMIC_BOOK_ID));
                        waiter.assertTrue(true);
                        return updatedComicBook;
                      });
              assertSame(updatedComicBook, result);
              waiter.resume();
            })
        .start();
  }
}
