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

package org.comixedproject.service.library.actions;

import static org.comixedproject.model.lists.SmartListRuleOperator.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import org.comixedproject.model.archives.ArchiveType;
import org.comixedproject.model.comicbooks.ComicState;
import org.comixedproject.model.comicbooks.ComicType;
import org.comixedproject.model.lists.SmartListRule;
import org.comixedproject.model.lists.SmartListRuleField;
import org.comixedproject.model.lists.SmartListRuleOperator;
import org.comixedproject.model.lists.SmartListRuleType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TranslateSmartRuleActionTest {
  private static final Integer TEST_RULE_ORDER = 22;
  public static final String TEST_ARCHIVE_TYPE_VALUE = ArchiveType.CB7.name();
  public static final String TEST_COMIC_STATE_VALUE = ComicState.STABLE.name();
  public static final String TEST_UNSCRAPED_VALUE = Boolean.FALSE.toString();
  public static final String TEST_COMIC_TYPE_VALUE = ComicType.ISSUE.name();
  private static final String TEST_PUBLISHER = "Super Publisher";
  private static final String TEST_SERIES = "Great Series";
  private static final String TEST_VOLUME = "2025";
  private static final String TEST_ISSUE_NUMBER = "64.EXTRA";
  private static final String TEST_TITLE = "This is the title of this comic";
  private static final String TEST_COVER_DATE = "2025-01-26";
  private static final String TEST_STORE_DATE = "2024-12-23";
  private static final String TEST_ADDED_DATE = "2024-12-20";

  @InjectMocks private TranslateSmartRuleAction action;
  @Mock private SmartListRule rule;

  private Map<String, String> fieldToValueMap = new HashMap<>();

  @Before
  public void setUp() {
    Mockito.when(rule.getOrder()).thenReturn(TEST_RULE_ORDER);

    fieldToValueMap.put(SmartListRuleField.ARCHIVE_TYPE.getFieldName(), TEST_ARCHIVE_TYPE_VALUE);
    fieldToValueMap.put(SmartListRuleField.COMIC_STATE.getFieldName(), TEST_COMIC_STATE_VALUE);
    fieldToValueMap.put(SmartListRuleField.IS_UNSCRAPED.getFieldName(), TEST_UNSCRAPED_VALUE);
    fieldToValueMap.put(SmartListRuleField.COMIC_TYPE.getFieldName(), TEST_COMIC_TYPE_VALUE);
    fieldToValueMap.put(SmartListRuleField.PUBLISHER.getFieldName(), TEST_PUBLISHER);
    fieldToValueMap.put(SmartListRuleField.SERIES.getFieldName(), TEST_SERIES);
    fieldToValueMap.put(SmartListRuleField.VOLUME.getFieldName(), TEST_VOLUME);
    fieldToValueMap.put(SmartListRuleField.ISSUE_NUMBER.getFieldName(), TEST_ISSUE_NUMBER);
    fieldToValueMap.put(SmartListRuleField.TITLE.getFieldName(), TEST_TITLE);
    fieldToValueMap.put(SmartListRuleField.COVER_DATE.getFieldName(), TEST_COVER_DATE);
    fieldToValueMap.put(SmartListRuleField.STORE_DATE.getFieldName(), TEST_STORE_DATE);
    fieldToValueMap.put(SmartListRuleField.ADDED_DATE.getFieldName(), TEST_ADDED_DATE);
  }

  @Test
  public void testExecute_applied_isNotNull() {
    for (var index = 0; index < SmartListRuleField.values().length; index++) {
      final SmartListRuleField field = SmartListRuleField.values()[index];

      unaryOperatorTest(field, IS_NOT_NULL);
    }
  }

  @Test
  public void testExecute_applied_isNull() {
    for (var index = 0; index < SmartListRuleField.values().length; index++) {
      final SmartListRuleField field = SmartListRuleField.values()[index];

      unaryOperatorTest(field, IS_NULL);
    }
  }

  private void unaryOperatorTest(
      final SmartListRuleField field, final SmartListRuleOperator operator) {
    // applied rule
    Mockito.when(rule.getRuleType()).thenReturn(SmartListRuleType.APPLIED);
    Mockito.when(rule.getRuleField()).thenReturn(field);
    Mockito.when(rule.getRuleOperator()).thenReturn(operator);

    String result = action.execute(rule);

    assertNotNull(result);
    assertTrue(
        result.contains(String.format("%s %s", field.getFieldName(), operator.getOperator())));

    // negated rule
    Mockito.when(rule.getRuleType()).thenReturn(SmartListRuleType.NEGATED);
    Mockito.when(rule.getRuleField()).thenReturn(field);
    Mockito.when(rule.getRuleOperator()).thenReturn(operator);

    result = action.execute(rule);

    assertNotNull(result);
    assertTrue(
        result.contains(
            String.format("(%s %s ) == false", field.getFieldName(), operator.getOperator())));
  }

  @Test
  public void testExecute_applied_isLessThan() {
    for (var index = 0; index < SmartListRuleField.values().length; index++) {
      final SmartListRuleField field = SmartListRuleField.values()[index];
      binaryOperatorTest(field, IS_LESS_THAN);
    }
  }

  private void binaryOperatorTest(
      final SmartListRuleField field, final SmartListRuleOperator operator) {
    final String fieldValue = this.fieldToValueMap.get(field);

    Mockito.when(rule.getRuleField()).thenReturn(field);
    Mockito.when(rule.getRuleType()).thenReturn(SmartListRuleType.APPLIED);
    Mockito.when(rule.getRuleOperator()).thenReturn(operator);
    Mockito.when(rule.getRuleValue()).thenReturn(fieldValue);

    final String result = action.execute(rule);

    assertNotNull(result);
    assertTrue(
        result.contains(
            String.format("%s %s %s", field.getFieldName(), operator.getOperator(), fieldValue)));
  }

  @Test
  public void testExecute_applied_isLessThanOrEqualTo() {
    for (var index = 0; index < SmartListRuleField.values().length; index++) {
      final SmartListRuleField field = SmartListRuleField.values()[index];
      binaryOperatorTest(field, IS_LESS_THAN_OR_EQUAL_TO);
    }
  }

  @Test
  public void testExecute_applied_isEqualTo() {
    for (var index = 0; index < SmartListRuleField.values().length; index++) {
      final SmartListRuleField field = SmartListRuleField.values()[index];
      binaryOperatorTest(field, IS_EQUAL_TO);
    }
  }

  @Test
  public void testExecute_applied_isGreaterThanOrEqualTo() {
    for (var index = 0; index < SmartListRuleField.values().length; index++) {
      final SmartListRuleField field = SmartListRuleField.values()[index];
      binaryOperatorTest(field, IS_GREATER_THAN_OR_EQUAL_TO);
    }
  }

  @Test
  public void testExecute_applied_isGreaterThan() {
    for (var index = 0; index < SmartListRuleField.values().length; index++) {
      final SmartListRuleField field = SmartListRuleField.values()[index];
      binaryOperatorTest(field, IS_GREATER_THAN);
    }
  }
}
