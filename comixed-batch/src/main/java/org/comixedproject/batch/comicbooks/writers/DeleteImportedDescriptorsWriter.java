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

package org.comixedproject.batch.comicbooks.writers;

import lombok.extern.log4j.Log4j2;
import org.comixedproject.model.comicfiles.ComicFileDescriptor;
import org.comixedproject.service.comicfiles.ComicFileService;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <code>DeleteImportedDescriptorsWriter</code> actively deletes the comic file descriptor.
 *
 * @author Darryl L. Pierce
 */
@Component
@StepScope
@Log4j2
public class DeleteImportedDescriptorsWriter implements ItemWriter<ComicFileDescriptor> {
  @Autowired private ComicFileService comicFileService;

  @Override
  public void write(final Chunk<? extends ComicFileDescriptor> comicFileDescriptors) {
    comicFileDescriptors.forEach(
        comicFileDescriptor -> {
          log.debug("Deleting comic file descriptor for {}", comicFileDescriptor.getFilename());
          this.comicFileService.delete(comicFileDescriptor);
        });
  }
}