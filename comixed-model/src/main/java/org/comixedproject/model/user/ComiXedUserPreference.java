/*
 * ComiXed - A digital comic book library management application.
 * Copyright (C) 2017, The ComiXed Project
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

package org.comixedproject.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.*;
import org.comixedproject.views.View.UserList;

/**
 * <code>ComiXedUserPreference</code> represents a single preference name and value for a single
 * user.
 *
 * @author Darryl L. Pierce
 */
@Entity
@Table(name = "comixed_user_preferences")
@NoArgsConstructor
@RequiredArgsConstructor
public class ComiXedUserPreference {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonProperty("comixedUserPreferenceId")
  @Column(name = "comixed_user_preference_id")
  @Getter
  private Long comixedUserPreferenceId;

  @ManyToOne
  @JoinColumn(name = "comixed_user_id", updatable = false, nullable = false)
  @JsonIgnore
  @Getter
  @Setter
  private ComiXedUser user;

  @Column(name = "preference_name", nullable = false, updatable = false, length = 128)
  @JsonView(UserList.class)
  @Getter
  @NonNull
  private String name;

  @Column(name = "preference_value", updatable = true, nullable = false, length = 256)
  @JsonView(UserList.class)
  @Getter
  @Setter
  private String value;
}
