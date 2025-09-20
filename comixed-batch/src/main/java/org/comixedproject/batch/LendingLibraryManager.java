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

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * <code>LendingLibraryManager</code> provides a system for marking comics as checked out for
 * processing. This is provided so that batch processes can verify that a comic is available for
 * processing, to prevent batch processes from clobbering each other.
 *
 * @author Darryl L. Pierce
 */
@Service
@Log4j2
public class LendingLibraryManager {
  private static final Object mutex = new Object();

  Set<Long> catalog = new ConcurrentSkipListSet<>();

  /**
   * Checks out a comic before performing an action. It then checks the comic back in.
   *
   * @param item the loaned item
   * @param comicBookId the comic book id
   * @param action the action to perform
   * @return the updated comic book
   */
  public <T> T executeAction(
      @NonNull final T item,
      final long comicBookId,
      @NonNull final LendingLibraryAction<T> action) {
    log.info("Lending out comic book: id={}", comicBookId);
    T result = null;
    synchronized (mutex) {
      boolean done = false;
      while (!done) {
        if (catalog.contains(comicBookId)) {
          log.info("Waiting for comic book to be returned: id={}", comicBookId);
          try {
            mutex.wait();
          } catch (InterruptedException error) {
            throw new RuntimeException("Error waiting for comic to be released", error);
          }
        } else {
          log.info("Checking out comic book: id={}", comicBookId);
          this.catalog.add(comicBookId);
          log.info("Running action on comic book: id={}", comicBookId);
          result = action.execute(item);
          log.info("Checking in comic book: id={}", comicBookId);
          this.catalog.remove(comicBookId);
          mutex.notifyAll();
          done = true;
        }
      }
    }
    return result;
  }
}
