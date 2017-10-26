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

  // List of objects returned from the from rule engine
  private List<Object> factsFromSession;

  // List of rules name fired
  private List<String> nameOfRulesFired;

  /**
   * @param numberOfRulesFired - Number of rules fired
   * @param factsFromSession - Facts returned from the engine
   * @param rulesFired - Name of rules fired
   */
  public RulesResponse(
      int numberOfRulesFired, List<Object> factsFromSession, List<String> rulesFired) {

    this.numberOfRulesFired = numberOfRulesFired;
    this.factsFromSession = factsFromSession;
    this.nameOfRulesFired = rulesFired;
  }

  /** @return the factsFromSession */
  public List<Object> getFactsFromSession() {
    return factsFromSession;
  }

  /** @return the numberOfRulesFired */
  public int getNumberOfRulesFired() {
    return numberOfRulesFired;
  }

  /** @return the nameOfRulesFired */
  public List<String> getNameOfRulesFired() {
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
