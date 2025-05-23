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

package org.comixedproject.model.net.collections;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.comixedproject.model.collections.SeriesDetail;

/**
 * <code>LoadPublisherDetailResponse</code> represents the response body when loading a page of
 * series for a publisher.
 *
 * @author Darryl L. Pierce
 */
@AllArgsConstructor
public class LoadPublisherDetailResponse {
  @JsonProperty("totalSeries")
  @Getter
  private long totalSeries;

  @JsonProperty("entries")
  @Getter
  private List<SeriesDetail> entries;
}
