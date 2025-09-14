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

package org.comixedproject.rest.lists;

import com.fasterxml.jackson.annotation.JsonView;
import io.micrometer.core.annotation.Timed;
import java.security.Principal;
import lombok.extern.log4j.Log4j2;
import org.comixedproject.model.lists.SmartList;
import org.comixedproject.service.lists.SmartListException;
import org.comixedproject.service.lists.SmartListService;
import org.comixedproject.views.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * <code>SmartListController</code> provides end points for loading the entries for a {@link
 * SmartList}.
 *
 * @author Darryl L. Pierce
 */
@RestController
@Log4j2
public class SmartListController {
  @Autowired private SmartListService smartListService;

  /**
   * Returns a smart list.
   *
   * @param principal the user principal
   * @param listId the list id
   * @return the smart list
   */
  @GetMapping(value = "/api/lists/smart/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @JsonView(View.ReadingLists.class)
  @PreAuthorize("hasRole('READER')")
  @Timed(value = "comixed.smart-list.test")
  public SmartList loadSmartList(final Principal principal, @PathVariable("id") final long listId)
      throws SmartListException {
    final String email = principal.getName();
    log.info("Loading smart list for {}: id={}", email, listId);
    return this.smartListService.loadSmartList(email, listId);
  }
}
