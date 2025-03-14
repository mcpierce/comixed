/*
 * ComiXed - A digital comic book library management application.
 * Copyright (C) 2019, The ComiXed Project
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

package org.comixedproject.service.library;

import java.io.IOException;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.comixedproject.adaptors.file.FileAdaptor;
import org.comixedproject.model.archives.ArchiveType;
import org.comixedproject.model.batch.PurgeLibraryEvent;
import org.comixedproject.model.batch.UpdateMetadataEvent;
import org.comixedproject.service.comicbooks.ComicBookService;
import org.comixedproject.service.comicpages.PageCacheService;
import org.comixedproject.state.comicbooks.ComicStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <code>LibraryService</code> provides methods for working with the library of comics.
 *
 * @author Darryl L. Pierce
 */
@Service
@Log4j2
public class LibraryService {
  @Autowired private ComicBookService comicBookService;
  @Autowired private FileAdaptor fileAdaptor;
  @Autowired private PageCacheService pageCacheService;
  @Autowired private ComicStateHandler comicStateHandler;
  @Autowired private ApplicationEventPublisher applicationEventPublisher;

  /**
   * Removes all files in the image cache directory.
   *
   * @throws LibraryException if an error occurs
   */
  public void clearImageCache() throws LibraryException {
    String directory = this.pageCacheService.getRootDirectory();
    log.debug("Clearing the image cache: {}", directory);
    try {
      this.fileAdaptor.deleteDirectoryContents(directory);
    } catch (IOException error) {
      throw new LibraryException("failed to clean image cache directory", error);
    }
  }

  /**
   * Fires an event to start the metadata update process for the specified comics.
   *
   * @param ids the comics
   */
  @Transactional
  public void updateMetadata(final List<Long> ids) {
    log.debug("Preparing {} comic book(s) for metadata update", ids.size());
    this.comicBookService.prepareForMetadataUpdate(ids);
    log.debug("Initiating update batch process");
    this.applicationEventPublisher.publishEvent(UpdateMetadataEvent.instance);
  }

  /**
   * Updates all comics in preparation for library organization.
   *
   * @param ids the comic book ids
   */
  public void prepareForOrganization(final List<Long> ids) {
    log.trace("Marking comics to be organized");
    this.comicBookService.prepareForOrganization(ids);
  }

  /** Marks the entire library for organization. */
  /** Updates the entire library for organization. */
  public void prepareAllForOrganization() {
    this.comicBookService.prepareAllForOrganization();
  }

  /**
   * Prepares comics to have their files recreated.
   *
   * @param ids the comic ids
   * @param archiveType the target archive type
   * @param renamePages the rename pages flag
   * @param deletePages the delete pages flag
   */
  public void prepareToRecreate(
      final List<Long> ids,
      final ArchiveType archiveType,
      final boolean renamePages,
      final boolean deletePages) {
    final long started = System.currentTimeMillis();
    log.debug("Preparing to recreate {} comic book file(s)", ids.size());
    this.comicBookService.prepareForRecreation(ids, archiveType, renamePages, deletePages);
    log.debug(
        "Comic book files prepared for recreation: {}ms", System.currentTimeMillis() - started);
  }

  /** Begins the process of purging comics from the library. */
  @Transactional
  public void prepareForPurging() {
    this.comicBookService.prepareComicBooksForDeleting();
    this.applicationEventPublisher.publishEvent(PurgeLibraryEvent.instance);
  }
}
