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

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.util.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.comixedproject.model.user.ComiXedUser;

/**
 * <code>SmartList</code> represents a smart list for a user.
 *
 * @author Darryl L. Pierce
 */
@Entity
@Table(name = "smart_lists")
@NoArgsConstructor
public class SmartList {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonProperty("id")
  @Getter
  private Long id;

  @Column(name = "list_name", nullable = false, updatable = true, length = 128)
  @Getter
  @Setter
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_id")
  @Getter
  @Setter
  private ComiXedUser owner;

  @OneToMany(mappedBy = "smartList", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @OrderColumn(name = "rule_index")
  @Getter
  private List<SmartListRule> rules = new ArrayList<>();

  @Override
  public boolean equals(final Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    final SmartList smartList = (SmartList) o;
    return Objects.equals(getName(), smartList.getName())
        && Objects.equals(getOwner(), smartList.getOwner());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getName(), getOwner());
  }

  @Override
  public String toString() {
    return "SmartList{" + "id=" + id + ", name='" + name + '\'' + ", owner=" + owner + '}';
  }
}
