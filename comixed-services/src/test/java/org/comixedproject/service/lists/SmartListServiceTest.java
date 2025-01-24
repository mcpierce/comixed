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

import java.util.ArrayList;
import java.util.List;
import org.comixedproject.model.library.DisplayableComic;
import org.comixedproject.model.lists.SmartList;
import org.comixedproject.model.lists.SmartListRule;
import org.comixedproject.model.lists.SmartListRuleField;
import org.comixedproject.repositories.lists.SmartListRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SmartListServiceTest {
  private static final Long TEST_SMART_LIST_ID = 23L;
  private static final int TEST_PAGE_NUMBER = 3;
  private static final int TEST_PAGE_SIZE = 25;
  private static final String TEST_SORT_FIELD = "added-date";
  private static final String TEST_SORT_DIRECTION = "asc";
  private static final SmartListRuleField TEST_RULE_FIELD = SmartListRuleField.PUBLISHER;

  @InjectMocks private SmartListService service;
  @Mock private SmartListRepository smartListRepository;
  @Mock private SmartList smartList;
  @Mock private SmartListRule rule;

  private List<SmartListRule> rulesList = new ArrayList<>();

  @Before
  public void setUp() {
    Mockito.when(rule.getRuleField()).thenReturn(TEST_RULE_FIELD);
    rulesList.add(rule);
    Mockito.when(smartList.getRules()).thenReturn(rulesList);
    Mockito.when(smartListRepository.getReferenceById(Mockito.anyLong())).thenReturn(smartList);
  }

  @Test(expected = SmartListException.class)
  public void testFindSmartListEntries_invalidSmartList() throws SmartListException {
    Mockito.when(smartListRepository.getReferenceById(Mockito.anyLong())).thenReturn(null);

    try {
      service.findSmartListEntries(
          TEST_SMART_LIST_ID,
          TEST_PAGE_NUMBER,
          TEST_PAGE_SIZE,
          TEST_SORT_FIELD,
          TEST_SORT_DIRECTION);
    } finally {
      Mockito.verify(smartListRepository).getReferenceById(TEST_SMART_LIST_ID);
    }
  }

  @Test
  public void testFindSmartListEntries() throws SmartListException {
    final List<DisplayableComic> result =
        service.findSmartListEntries(
            TEST_SMART_LIST_ID,
            TEST_PAGE_NUMBER,
            TEST_PAGE_SIZE,
            TEST_SORT_FIELD,
            TEST_SORT_DIRECTION);
  }
}
