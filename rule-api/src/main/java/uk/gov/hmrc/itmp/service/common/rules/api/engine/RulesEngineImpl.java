package uk.gov.hmrc.itmp.service.common.rules.api.engine;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmrc.itmp.service.common.rules.api.enums.SessionType;
import uk.gov.hmrc.itmp.service.common.rules.api.exception.RulesApiException;
import uk.gov.hmrc.itmp.service.common.rules.api.message.RulesRequest;
import uk.gov.hmrc.itmp.service.common.rules.api.message.RulesResponse;

/**
 * <strong>This class provides the implementation for core methods in rule engine
 * interface.</strong>
 *
 * <p>This class is a spring bean.So application is not needed to initialise this class. It will be
 * automatically injected by spring.
 *
 * <pre>
 * Example:
 * {@code @Autowired RulesEngine rulesEngine;}
 * </pre>
 *
 * @see RulesEngine
 * @since 1.0.0
 */
@Component
public class RulesEngineImpl implements RulesEngine {

  @Autowired private KieContainer kContainer;

  private KieServices kieService = KieServices.Factory.get();

  private Logger logger = LogManager.getLogger(this);

  /**
   * This method is used to fire the rules and return the RulesResponse.
   *
   * @param rulesRequest All the parameter required to fire the rule.
   * @return RulesResponse Number of rules fired ,name of rules fired and facts from session.
   * @throws RulesApiException In case rulesRequest is null or engine not able to load given kiebase
   *     or kiession from the container.
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

        kSession = RulesEngineHelper.getStatefulKieSession(rulesRequestParams, kContainer);

        if (null == kSession) {
          logger.error("Cannot instantiate KieSession. Please check configuration");
          throw new RulesApiException("Cannot instantiate KieSession.Please check configuration");
        }

        rulesResponse =
            RulesEngineHelper.fireStatefulRules(kSession, rulesRequestParams, returnedFactsClass);

      }

      // If session type passed is state less
      else if (rulesRequestParams.getSessionType() == SessionType.STATELESS) {

        StatelessKieSession statelessKieSession =
            RulesEngineHelper.getStatelessKieSession(rulesRequestParams, kContainer);

        if (null == statelessKieSession) {
          logger.error("Cannot instantiate stateless KieSession.Please check configuration");
          throw new RulesApiException(
              "Cannot instantiate stateless KieSession.Please check configuration");
        }

        rulesResponse =
            RulesEngineHelper.fireRuleStateless(
                statelessKieSession, rulesRequestParams, returnedFactsClass, kieService);
      }
      logger.info(rulesResponse);
      logger.traceExit("END - method - [fireRules(RulesRequest,List<Class>)]");

    } catch (RulesApiException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new RulesApiException(ex.getMessage(), ex);
    }

    return rulesResponse;
  }
}
