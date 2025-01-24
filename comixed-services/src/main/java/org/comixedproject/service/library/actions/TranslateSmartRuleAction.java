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

import lombok.extern.log4j.Log4j2;
import org.comixedproject.model.lists.SmartListRule;
import org.comixedproject.model.lists.SmartListRuleOperator;
import org.comixedproject.model.lists.SmartListRuleType;
import org.springframework.stereotype.Component;

/**
 * <code>TranslateSmartRuleAction</code> provides an executable type to convert a {@link
 * SmartListRule} into text.
 *
 * @author Darryl L. Pierce
 */
@Component
@Log4j2
public class TranslateSmartRuleAction {
  public String execute(final SmartListRule rule) {
    final StringBuilder ruleBuilder = new StringBuilder();
    final String fieldName = rule.getRuleField().getFieldName();
    final String ruleOperator = rule.getRuleOperator().getOperator();
    String targetValue = rule.getRuleValue();

    // no target value is needed when checking for null
    if (rule.getRuleOperator() == SmartListRuleOperator.IS_NULL
        || rule.getRuleOperator() == SmartListRuleOperator.IS_NOT_NULL) {
      targetValue = "";
    }

    // negating a rule
    String rulePrefix = "";
    String ruleSuffix = "";
    if (rule.getRuleType() == SmartListRuleType.NEGATED) {
      rulePrefix = "(";
      ruleSuffix = ") == false";
    }

    ruleBuilder.append(
        String.format(
            "rule \"rule-%d-%s-%s-%s\"\n",
            rule.getOrder(), rule.getRuleType(), rule.getRuleOperator(), fieldName));
    ruleBuilder.append("salience 1\n");
    ruleBuilder.append("    when\n");
    ruleBuilder.append(
        String.format(
            "      DisplayableComic(%s%s %s %s%s)\n",
            rulePrefix, fieldName, ruleOperator, targetValue, ruleSuffix));
    ruleBuilder.append("    then\n");
    ruleBuilder.append("        System.out.println(\"THIS PASSED!\");\n");
    ruleBuilder.append("end\n\n");

    return ruleBuilder.toString();
  }
}
