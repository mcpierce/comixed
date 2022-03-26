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

package org.comixedproject.service.metadata;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.comixedproject.model.metadata.MetadataCache;
import org.comixedproject.model.metadata.MetadataCacheEntry;
import org.comixedproject.repositories.metadata.MetadataCacheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <code>MetadataCacheService</code> manages storing and retrieving cached comic metadata.
 *
 * @author Darryl L. Pierce
 */
@Service
@Log4j2
public class MetadataCacheService {
  private static final long SEVEN_DAYS = 7L * 24L * 60L * 60L * 1000L;
  @Autowired private MetadataCacheRepository metadataCacheRepository;

  /**
   * Stores data in the cache.
   *
   * @param source the data source
   * @param key the entry key
   * @param values the values to cache
   */
  @Transactional
  public void saveToCache(final String source, final String key, final List<String> values) {
    log.debug("Saving data to cache: source={} key={} record count={}", source, key, values.size());

    final MetadataCache existing = this.metadataCacheRepository.getFromCache(source, key);

    if (existing != null) {
      log.debug("Deleting existing scaping cache");
      this.metadataCacheRepository.delete(existing);
      this.metadataCacheRepository.flush();
    } else {
      log.debug("No existing cache found");
    }

    log.debug("Creating new cache entry");
    final MetadataCache entry = new MetadataCache();
    entry.setSource(source);
    entry.setCacheKey(key);
    for (int index = 0; index < values.size(); index++) {
      final String value = values.get(index);
      final MetadataCacheEntry cachedValue = new MetadataCacheEntry();
      cachedValue.setMetadataCache(entry);
      cachedValue.setEntryNumber(index);
      cachedValue.setEntryValue(value);
      entry.getEntries().add(index, cachedValue);
    }

    log.debug("Saving cache entry: key={}", entry.getCacheKey());
    this.metadataCacheRepository.save(entry);
  }

  /**
   * Returns data from the cache.
   *
   * @param source the data source
   * @param key the entry key
   * @return the values in the cache, or null if none were found or were expired
   */
  @Transactional
  public List<String> getFromCache(final String source, final String key) {
    log.debug("Loading scraping cache entry: source={} key={}", source, key);
    final MetadataCache cacheEntry = this.metadataCacheRepository.getFromCache(source, key);

    if (cacheEntry == null) {
      log.debug("No cached entry found");
      return null;
    }

    final long expireThreshold = System.currentTimeMillis() - SEVEN_DAYS;
    if (cacheEntry.getCreatedOn().getTime() <= expireThreshold) {
      log.debug("Entry is expired");
      this.metadataCacheRepository.delete(cacheEntry);
      return null;
    }

    log.debug("Extracting cached values");
    final List<String> result = new ArrayList<>();
    for (int index = 0; index < cacheEntry.getEntries().size(); index++)
      result.add(cacheEntry.getEntries().get(index).getEntryValue());

    return result;
  }
}