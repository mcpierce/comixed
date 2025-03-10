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

package org.comixedproject.batch.comicbooks.readers;

import java.util.List;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.comixedproject.model.comicbooks.ComicBook;
import org.comixedproject.service.comicbooks.ComicBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * <code>ScrapeMetadataReader</code> loads comic books that have been marked for batch scraping.
 *
 * @author Darryl L. Pierce
 */
@Component
@Log4j2
public class ScrapeMetadataReader extends AbstractComicReader {
  @Value("${comixed.batch.scrape-metadata.chunk-size:10}")
  @Getter
  private int chunkSize;

  @Autowired private ComicBookService comicBookService;

  protected List<ComicBook> doLoadComics() {
    return this.comicBookService.findBatchScrapingComics(this.chunkSize);
  }
}
