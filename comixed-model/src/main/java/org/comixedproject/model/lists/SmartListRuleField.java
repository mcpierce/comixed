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

package org.comixedproject.model.lists;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <code>SmartListRuleField</code> determines what field the rule is applied to.
 *
 * @author Darryl L. Pierce
 */
@AllArgsConstructor
public enum SmartListRuleField {
  ARCHIVE_TYPE("archiveType"),
  COMIC_STATE("comicState"),
  IS_UNSCRAPED("isUnscrapped"),
  COMIC_TYPE("comicType"),
  PUBLISHER("publisher"),
  SERIES("series"),
  VOLUME("volume"),
  ISSUE_NUMBER("issueNumber"),
  TITLE("title"),
  COVER_DATE("coverDate"),
  STORE_DATE("storeDate"),
  ADDED_DATE("addedDate");

  @Getter private final String fieldName;
}
