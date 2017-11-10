package uk.gov.hmrc.itmp.service.common.rules.test.base.message;

import java.util.Collections;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;

/**
 * <strong>This class holds the response from rules engine.</strong>
 *
 * <pre>
 * RulesResponse contains
 *
 * Number of rules fired.
 * Immutable List of names of rules fired.
 * Immutable List of objects passed to channel.
 * </pre>
 *
 * @since 1.0.0
 */
public class RuleTestResponse {

  // Number of rules fired
  private int numberOfRulesFired;

  // List of objects from session
  private List<Object> factsFromSession;

  // List of rules fired
  private List<String> nameOfRulesFired;

  /**
   * @param numberOfRulesFired - Number of rules fired
   * @param factsFromSession - Facts returned from the session
   * @param rulesFired - Name of rules fired
   */
  public RuleTestResponse(
      int numberOfRulesFired, List<Object> factsFromSession, List<String> rulesFired) {

    this.numberOfRulesFired = numberOfRulesFired;
    this.factsFromSession = factsFromSession;
    this.nameOfRulesFired = rulesFired;
  }

  /** @return the factsFromSession */
  public List<Object> getFactsFromSession() {
    if (CollectionUtils.isNotEmpty(factsFromSession)) {
      return Collections.unmodifiableList(factsFromSession);
    }
    return factsFromSession;
  }

  /** @return the numberOfRulesFired */
  public int getNumberOfRulesFired() {
    return numberOfRulesFired;
  }

  /** @return the nameOfRulesFired */
  public List<String> getNameOfRulesFired() {
    if (CollectionUtils.isNotEmpty(nameOfRulesFired)) {
      return Collections.unmodifiableList(nameOfRulesFired);
    }
    return nameOfRulesFired;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder
        .append("RulesResponse [numberOfRulesFired=")
        .append(numberOfRulesFired)
        .append(", factsFromSession=")
        .append(factsFromSession)
        .append(", rulesFired=")
        .append(nameOfRulesFired)
        .append("]");
    return builder.toString();
  }
}
