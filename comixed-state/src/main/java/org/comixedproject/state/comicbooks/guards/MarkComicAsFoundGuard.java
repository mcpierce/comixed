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

package org.comixedproject.state.comicbooks.guards;

import java.io.File;
import lombok.extern.log4j.Log4j2;
import org.comixedproject.model.comicbooks.ComicBook;
import org.comixedproject.model.comicbooks.ComicState;
import org.comixedproject.state.comicbooks.ComicEvent;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

/**
 * <code>MarkComicAsFoundGuard</code> provides a guard for marking comics a found.
 *
 * @author Darryl L. Pierce
 */
@Component
@Log4j2
public class MarkComicAsFoundGuard extends AbstractComicBookGuard {
  @Override
  public boolean evaluate(final StateContext<ComicState, ComicEvent> stateContext) {
    final ComicBook comicBook = this.fetchComic(stateContext);
    if (!comicBook.getComicDetail().isMissing()) {
      log.trace("Comic book not marked as missing: id={}", comicBook.getComicBookId());
      return false;
    }
    final File file = comicBook.getComicDetail().getFile();
    if (!file.exists()) {
      log.trace("Physical file not found: {}", file.getAbsolutePath());
      return false;
    }

    log.debug("Comic book can be marked as missing: id={}", comicBook.getComicBookId());
    return true;
  }
}
