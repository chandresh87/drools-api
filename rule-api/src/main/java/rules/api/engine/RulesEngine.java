package rules.api.engine;

import java.util.List;
import rules.api.message.RulesRequest;
import rules.api.message.RulesResponse;

/**
 * This interface has method to fire rules by passing the RulesRequest and get the RulesResponse.
 *
 * @author chandresh.mishra
 */
public interface RulesEngine {

  /**
   * This method is used to fire the rules and return number of rules fired.
   *
   * @param rulesRequestParams - All the parameter required to fire the rule
   * @param returnedFactsClass - List of class for facts returned from session.
   * @return integer - Number of rules fired
   */
  public RulesResponse fireRules(RulesRequest rulesRequestParams);

  /**
   * This method is used to fire the rules and return the response containing number of rules fired
   * and requested facts from the session.
   *
   * @param rulesRequestParams - All the parameter required to fire the rule
   * @param returnedFactsClass - List of class for facts returned from session.
   * @return RulesResponse
   */
  public RulesResponse fireRules(
      RulesRequest rulesRequestParams, List<Class<?>> returnedFactsClass);
}
