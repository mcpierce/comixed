package org.comixedproject.model.lists;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.util.Objects;
import lombok.*;

/**
 * <code>SmartListRule</code> represents a single rule in a smart list.
 *
 * @author Darryl L. Pierce
 */
@Entity
@Table(name = "smart_list_rules")
@NoArgsConstructor
@RequiredArgsConstructor
public class SmartListRule {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonProperty("id")
  @Getter
  private Long id;

  @ManyToOne
  @JoinColumn(name = "smart_list_id")
  @Getter
  @NonNull
  private SmartList smartList;

  @Column(name = "rule_order", nullable = false, updatable = true)
  @Getter
  @Setter
  @NonNull
  private Integer order;

  @Column(name = "rule_field", nullable = false, updatable = true)
  @Enumerated(EnumType.STRING)
  @Getter
  @Setter
  @NonNull
  private SmartListRuleField ruleField;

  @Column(name = "rule_value", nullable = false, updatable = true, length = 256)
  @Getter
  @Setter
  @NonNull
  private String ruleValue;

  @Column(name = "rule_type", nullable = false, updatable = true)
  @Enumerated(EnumType.STRING)
  @Getter
  @Setter
  private SmartListRuleType ruleType;

  @Column(name = "rule_operator", nullable = false, updatable = true)
  @Enumerated(EnumType.STRING)
  @Getter
  @Setter
  private SmartListRuleOperator ruleOperator;

  @Override
  public boolean equals(final Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    final SmartListRule that = (SmartListRule) o;
    return Objects.equals(getSmartList(), that.getSmartList())
        && Objects.equals(getOrder(), that.getOrder())
        && getRuleField() == that.getRuleField()
        && Objects.equals(getRuleValue(), that.getRuleValue())
        && getRuleType() == that.getRuleType()
        && getRuleOperator() == that.getRuleOperator();
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        getSmartList(),
        getOrder(),
        getRuleField(),
        getRuleValue(),
        getRuleType(),
        getRuleOperator());
  }

  @Override
  public String toString() {
    return "SmartListRule{"
        + "id="
        + id
        + ", smartList="
        + smartList
        + ", order="
        + order
        + ", ruleField="
        + ruleField
        + ", ruleValue='"
        + ruleValue
        + '\''
        + ", ruleType="
        + ruleType
        + ", ruleOperator="
        + ruleOperator
        + '}';
  }
}
