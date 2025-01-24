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

package org.comixedproject.service.lists;

import java.util.List;
import java.util.Objects;
import lombok.extern.log4j.Log4j2;
import org.comixedproject.model.library.DisplayableComic;
import org.comixedproject.model.lists.SmartList;
import org.comixedproject.model.lists.SmartListRule;
import org.comixedproject.repositories.lists.SmartListRepository;
import org.comixedproject.service.library.DisplayableComicService;
import org.comixedproject.service.library.actions.TranslateSmartRuleAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <code>SmartListService</code> provides a central service for applying rules to a list of comics.
 *
 * @author Darryl L. Pierce
 */
@Service
@Log4j2
public class SmartListService {
  @Autowired private SmartListRepository smartListRepository;
  @Autowired private DisplayableComicService displayableComicService;
  @Autowired private TranslateSmartRuleAction translateSmartRuleAction;

  /**
   * Applies the specified rule to all comics in the library.
   *
   * @param smartListId the list id
   * @param pageNumber the page number
   * @param pageSize the page size
   * @param sortField the sort field
   * @param sortDirection the sort order
   * @return the matching comics
   * @throws SmartListException if an error occurs
   */
  @Transactional(readOnly = true)
  public List<DisplayableComic> findSmartListEntries(
      final Long smartListId,
      final int pageNumber,
      final int pageSize,
      final String sortField,
      final String sortDirection)
      throws SmartListException {
    final SmartList smartList = this.doGetSmartList(smartListId);
    final String template = this.doCreateRules(smartList.getRules());
    final int startingIndex = pageNumber * pageSize;
    return this.displayableComicService.loadComics(sortField, sortDirection).stream()
        .filter(DisplayableComic::isRuleMatch)
        .toList()
        .subList(startingIndex, startingIndex + pageSize);
  }

  private String doCreateRules(final List<SmartListRule> rules) {
    final StringBuilder ruleBuilder = new StringBuilder();

    ruleBuilder.append("package org.comixedproject;\n\n");
    ruleBuilder.append("import org.comixedproject.model.library.DisplayableComic;\n\n");

    for (int index = 0; index < rules.size(); index++) {
      final SmartListRule rule = rules.get(index);
      ruleBuilder.append(this.translateSmartRuleAction.execute(rule));
    }
    ruleBuilder.append("\n");
    ruleBuilder.append("\n");
    ruleBuilder.append("end template\n");

    return ruleBuilder.toString();
  }

  private SmartList doGetSmartList(final Long id) throws SmartListException {
    log.trace("Loading smart list: id={}", id);
    final SmartList result = this.smartListRepository.getReferenceById(id);
    if (Objects.isNull(result)) throw new SmartListException("No such smart list: id=" + id);
    return result;
  }
}
