/** */
package rules.api.message;

import java.util.List;

/**
 * This class is responsible for sending back the response from rules engine
 *
 * @author chandresh.mishra
 */
public class RulesResponse {

  // Number of rules fired
  private int numberOfRulesFired;

  // List of objects from session
  private List<Object> factsFromSession;

  // List of rules fired
  private List<String> rulesFired;

  /**
   * @param numberOfRulesFired
   * @param factsFromSession
   * @param rulesFired
   */
  public RulesResponse(
      int numberOfRulesFired, List<Object> factsFromSession, List<String> rulesFired) {

    this.numberOfRulesFired = numberOfRulesFired;
    this.factsFromSession = factsFromSession;
    this.rulesFired = rulesFired;
  }

  /** @return the factsFromSession */
  public List<Object> getFactsFromSession() {
    return factsFromSession;
  }

  /** @return the numberOfRulesFired */
  public int getNumberOfRulesFired() {
    return numberOfRulesFired;
  }

  /** @return the rulesFired */
  public List<String> getRulesFired() {
    return rulesFired;
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
        .append(rulesFired)
        .append("]");
    return builder.toString();
  }
}
