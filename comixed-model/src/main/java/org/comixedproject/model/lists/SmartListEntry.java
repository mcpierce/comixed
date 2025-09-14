/*
 * ComiXed - A digital comic book library management application.
 * Copyright (C) 2025, The ComiXed Project.
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

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

/**
 * <code>SmartListEntry</code> holds a single rule for a smart list.
 *
 * @author Darryl L. Pierce
 */
@Entity
@Table(name = "smart_list_entries")
@NoArgsConstructor
@RequiredArgsConstructor
public class SmartListEntry {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "smart_list_entry_id")
  @Getter
  private Long smartListEntryId;

  @Column(name = "field_name", nullable = false, length = 128)
  @JsonProperty("fieldName")
  @Getter
  @NonNull
  private String fieldName;

  @Column(name = "negated", nullable = false)
  @JsonProperty("negated")
  @Getter
  private boolean negated = false;

  @Column(name = "rule_type", nullable = false, length = 32)
  @Enumerated(EnumType.STRING)
  @JsonProperty("ruleType")
  @NonNull
  @Getter
  private SmartRuleType ruleType;

  @Column(name = "target_expression", nullable = false, length = 128)
  @JsonProperty("targetExpression")
  @NonNull
  @Getter
  private String targetExpression;
}
