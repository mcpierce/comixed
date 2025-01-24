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
 * A <code>SmartListRuleOperator</code> determines how the left and right sides of a rule are
 * evaluated.
 *
 * @author Darryl L. Pierce
 */
@AllArgsConstructor
public enum SmartListRuleOperator {
  IS_NOT_NULL("!= null"),
  IS_NULL("== null"),
  IS_LESS_THAN("<"),
  IS_LESS_THAN_OR_EQUAL_TO("<="),
  IS_EQUAL_TO("=="),
  IS_GREATER_THAN_OR_EQUAL_TO(">="),
  IS_GREATER_THAN(">");

  @Getter private String operator;
}
