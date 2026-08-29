/*
 * ComiXed - A digital comic book library management application.
 * Copyright (C) 2026, The ComiXed Project
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

package org.comixedproject.model.comicbooks;

import jakarta.persistence.*;
import java.util.Date;
import lombok.*;

/**
 * <code>ComicEvent</code> represents an event to be performed on an instance of {@link
 * ComicDetail}.
 *
 * @author Darryl L. Pierce
 */
@Entity
@Table(name = "comic_events_v4")
@NoArgsConstructor
@RequiredArgsConstructor
public class ComicEvent {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "comic_event_id")
  @Getter
  private Long comicEventId;

  @ManyToOne()
  @JoinColumn(name = "comic_detail_id", insertable = true, nullable = false, updatable = false)
  @Getter
  @NonNull
  private ComicDetail comicDetail;

  @Column(name = "event_type", insertable = true, updatable = false, nullable = false)
  @Getter
  @NonNull
  private ComicEventType eventType;

  @Column(name = "completed_on", insertable = true, updatable = true, nullable = true)
  @Getter
  @Setter
  private Date completedOn;
}
