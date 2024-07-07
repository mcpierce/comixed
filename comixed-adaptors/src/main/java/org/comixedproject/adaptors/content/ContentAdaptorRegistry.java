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

package org.comixedproject.adaptors.content;

import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 * <code>ContentAdaptorRegistry</code> provides a registry for retrieving the correct {@link
 * ContentAdaptor} for the provided content file name and/or content.
 *
 * @author Darryl L. Pierce
 */
@Component
@Log4j2
public class ContentAdaptorRegistry {
  /**
   * Returns the content adaptor that can be used to load the given content based on its filename.
   *
   * <p>If none is found, it checks for an adaptor for the given content type.
   *
   * @param filename the entry's filename
   * @return the adaptor, or <code>null</code> if none is found
   */
  public ContentAdaptor getContentAdaptorForFilename(final String filename) {
    log.debug("Searching for content adaptor for filename: {}", filename);
    final List<FileNameContentAdaptorProvider> loaders =
        ServiceLoader.load(FileNameContentAdaptorProvider.class).stream()
            .map(ServiceLoader.Provider::get)
            .toList();

    final Optional<FileNameContentAdaptorProvider> provider =
        loaders.stream().filter(loader -> loader.supportedFilename(filename)).findFirst();

    if (provider.isPresent()) {
      final FileNameContentAdaptorProvider result = provider.get();
      log.debug("Found provider: {}", result.getName());
      return result.create();
    }

    log.debug("No content adaptor found");
    return null;
  }

  public ContentAdaptor getContentAdaptorForContentType(final String contentType) {
    log.debug("Searching for content adaptor for content type: {}", contentType);

    final List<FileTypeContentAdaptorProvider> loaders =
        ServiceLoader.load(FileTypeContentAdaptorProvider.class).stream()
            .map(ServiceLoader.Provider::get)
            .toList();

    final Optional<FileTypeContentAdaptorProvider> provider =
        loaders.stream().filter(loader -> loader.supportedContentType(contentType)).findFirst();

    if (provider.isPresent()) {
      final FileTypeContentAdaptorProvider result = provider.get();
      log.debug("Found provider: {}", result.getName());
      return result.create();
    }

    log.debug(
        "No content adaptor found for filename or content type: content type={}", contentType);
    return null;
  }
}
