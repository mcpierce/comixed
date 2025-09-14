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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import lombok.*;
import org.comixedproject.model.user.ComiXedUser;
import org.hibernate.annotations.ColumnTransformer;

@Entity
@Table(name = "smart_lists")
@NoArgsConstructor
@RequiredArgsConstructor
public class SmartList {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "smart_list_id")
  @JsonProperty("smartListId")
  @Getter
  private Long smartListId;

  @ManyToOne
  @JoinColumn(name = "owner_id", nullable = false, updatable = false)
  @JsonProperty("owner")
  @Getter
  @NonNull
  private ComiXedUser owner;

  @Column(name = "name", nullable = false, updatable = true)
  @JsonProperty("name")
  @Getter
  private String name;

  @Column(name = "name_key", nullable = false, updatable = true)
  @ColumnTransformer(write = "(UPPER(?))")
  @JsonIgnore
  @Getter
  private String nameKey;

  @ElementCollection
  @CollectionTable(name = "smart_list_entries", joinColumns = @JoinColumn(name = "smart_list_id"))
  @Column(name = "smart_list_entry_id")
  @Getter
  private Set<SmartListEntry> entries = new HashSet<>();

  @Column(name = "created_on", nullable = false, insertable = true, updatable = true)
  @JsonProperty("createdOn")
  @Getter
  @Setter
  private Date createdOn = new Date();

  @Column(name = "last_modified_on", nullable = false, insertable = true, updatable = true)
  @JsonProperty("lastModifiedOn")
  @Getter
  @Setter
  private Date lastModifiedOn = new Date();

  public void setName(final String name) {
    this.name = name;
    this.nameKey = name;
  }
}
