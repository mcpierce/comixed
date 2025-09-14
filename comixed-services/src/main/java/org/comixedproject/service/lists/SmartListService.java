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

package org.comixedproject.service.lists;

import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.comixedproject.model.lists.SmartList;
import org.comixedproject.repositories.lists.SmartListRepository;
import org.comixedproject.service.library.DisplayableComicService;
import org.kie.api.runtime.KieContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
public class SmartListService {
  @Autowired private SmartListRepository smartListRepository;
  @Autowired private DisplayableComicService displayableComicService;
  @Autowired private KieContainer kieContainer;

  /**
   * Loads a reading list by id for a given user.
   *
   * @param email the user's email
   * @param id the list id
   * @return the smart list
   * @throws SmartListException if the id is invalid, or the list is not owned by the given user
   */
  @Transactional(readOnly = true)
  public SmartList loadSmartList(final String email, final long id) throws SmartListException {
    final SmartList result = this.doLoadSmartList(id);
    if (result.getOwner().getEmail().equals(email)) {
      return result;
    }

    throw new SmartListException("List not owned by " + email + ": id=" + id);
  }

  private SmartList doLoadSmartList(final long id) throws SmartListException {
    final Optional<SmartList> optionalSmartList = this.smartListRepository.findById(id);
    if (optionalSmartList.isPresent()) {
      return optionalSmartList.get();
    }
    throw new SmartListException("No such smart list: id=" + id);
  }
}
