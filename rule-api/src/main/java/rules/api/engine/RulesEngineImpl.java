package rules.api.engine;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rules.api.enums.SessionType;
import rules.api.exception.RulesApiException;
import rules.api.message.RulesRequest;
import rules.api.message.RulesResponse;

/**
 * This class provides the core methods to run the rule engine
 *
 * @author chandresh.mishra
 */
@Component
public class RulesEngineImpl implements RulesEngine {

  @Autowired private RulesEngineHelper rulesEngineHelper;

  private Logger logger = LogManager.getLogger(this);

  /**
   * This method is used to fire the rules and RulesResponse
   *
   * @param rulesRequest - All the parameter required to fire the rule
   * @return RulesResponse - Number of rules fired ,name of rules fired and facts from session
   */
  @Override
  public RulesResponse fireRules(RulesRequest rulesRequest) {

    logger.traceEntry("START - method - [fireRules(RulesRequest)]");
    logger.traceExit("END - method - [fireRules(RulesRequest)]");
    return this.fireRules(rulesRequest, null);
  }

  /**
   * This method is used to fire the rules and return the response containing number of rules fired
   * and requested facts from the session.
   *
   * @param rulesRequestParams - All the parameter required to fire the rule
   * @param returnedFactsClass - List of class for facts returned from session.
   * @return RulesResponse
   */
  private RulesResponse fireRules(
      RulesRequest rulesRequestParams, List<Class<?>> returnedFactsClass) {

    logger.traceEntry("START - method - [fireRules(RulesRequest,List<Class>)]");

    KieSession kSession = null;
    RulesResponse rulesResponse = null;

    logger.info(rulesRequestParams);

    if (null == rulesRequestParams) {

      logger.error("RulesRequest is mandatory");

      throw new RulesApiException("RulesRequest is mandatory");
    }

    try {
      // Using a default stateful session in case session type is not given OR session type is stateful
      if (null == rulesRequestParams.getSessionType()
          || rulesRequestParams.getSessionType() == SessionType.STATEFUL) {

        kSession = rulesEngineHelper.getStatefulKieSession(rulesRequestParams);

        if (null == kSession) {
          logger.error("Can not instantiate KieSession.Please check configuration");
          throw new RulesApiException("Can not instantiate KieSession.Please check configuration");
        }

        rulesResponse =
            rulesEngineHelper.fireStatefulRules(kSession, rulesRequestParams, returnedFactsClass);

      }

      // If session type passed is state less
      else if (rulesRequestParams.getSessionType() == SessionType.STATELESS) {

        StatelessKieSession statelessKieSession =
            rulesEngineHelper.getStatelessKieSession(rulesRequestParams);

        if (null == statelessKieSession) {
          logger.error("Can not instantiate stateless KieSession.Please check configuration");
          throw new RulesApiException(
              "Can not instantiate stateless KieSession.Please check configuration");
        }

        rulesResponse =
            rulesEngineHelper.fireRuleStateless(
                statelessKieSession, rulesRequestParams, returnedFactsClass);
      }
      logger.info(rulesResponse);
      logger.traceExit("END - method - [fireRules(RulesRequest,List<Class>)]");

    } catch (Exception ex) {
      if (ex instanceof RulesApiException) {

        throw ex;
      } else {
        throw new RulesApiException(ex.getMessage(), ex);
      }
    }
    return rulesResponse;
  }

  @Override
  public List<Object> filterFacts(List<Object> objectList, List<Class<?>> returnedFactsClass) {

    return objectList
        .stream()
        .filter(element -> returnedFactsClass.contains(element.getClass()))
        .collect(Collectors.toList());
  }
}
