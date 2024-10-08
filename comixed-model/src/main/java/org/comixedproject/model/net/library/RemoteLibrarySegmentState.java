/*
 * ComiXed - A digital comic book library management application.
 * Copyright (C) 2022, The ComiXed Project
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

package org.comixedproject.model.net.library;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.comixedproject.views.View;

/**
 * <code>RemoteLibrarySegmentState</code> contains the state for a single segment of the library.
 *
 * @author Darryl L. Pierce
 */
@AllArgsConstructor
public class RemoteLibrarySegmentState {
  @JsonProperty("name")
  @JsonView(View.RemoteLibraryState.class)
  @Getter
  @NonNull
  private String name;

  @JsonProperty("count")
  @JsonView(View.RemoteLibraryState.class)
  @Getter
  private long count;
}
