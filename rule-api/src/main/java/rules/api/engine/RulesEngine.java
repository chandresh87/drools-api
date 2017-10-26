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
   * This method is used to fire the rules and returns the RulesResponse.
   *
   * @param rulesRequest - All the parameter required to fire the rule
   * @return RulesResponse
   */
  public RulesResponse fireRules(RulesRequest rulesRequest);

  /**
   * This method is used to filter the facts using the type of object
   *
   * @param objectList - List of object passed for filtration
   * @param returnedFactsClass - List of object class that need to be filtered
   * @return List
   */
  public List<Object> filterFacts(List<Object> objectList, List<Class<?>> returnedFactsClass);
}
