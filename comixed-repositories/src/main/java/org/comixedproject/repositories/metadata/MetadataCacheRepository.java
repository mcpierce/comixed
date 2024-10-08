/*
 * ComiXed - A digital comic book library management application.
 * Copyright (C) 2020, The ComiXed Project.
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

package org.comixedproject.repositories.metadata;

import org.comixedproject.model.metadata.MetadataCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * <code>MetadataCacheRepository</code> handls storing and fetching instances of {@link
 * MetadataCache}.
 *
 * @author Darryl L. Pierce
 */
@Repository
public interface MetadataCacheRepository extends JpaRepository<MetadataCache, Long> {
  /**
   * Retrieves the cache entry for the given source with the given key.
   *
   * @param source the source
   * @param key the key
   * @return the entry, or null
   */
  @Query("SELECT c FROM MetadataCache c WHERE c.source = :source AND c.cacheKey = :key")
  MetadataCache getFromCache(@Param("source") String source, @Param("key") String key);
}
