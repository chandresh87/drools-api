
package uk.gov.hmrc.itmp.service.common.rules.api.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kie.api.KieServices;
import org.kie.api.command.Command;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import uk.gov.hmrc.itmp.service.common.rules.api.channels.SendData;
import uk.gov.hmrc.itmp.service.common.rules.api.exception.RulesApiException;
import uk.gov.hmrc.itmp.service.common.rules.api.listener.RuleAgendaListener;
import uk.gov.hmrc.itmp.service.common.rules.api.message.RulesRequest;
import uk.gov.hmrc.itmp.service.common.rules.api.message.RulesResponse;

/**
 * This class contains helper method used by the rule engine internally.It has default visibility.
 */
class RulesEngineHelper {

  private static Logger logger = LogManager.getLogger(RulesEngineHelper.class);

  /** Private constructor to defend any internal or external instantiation. */
  private RulesEngineHelper() {
    throw new AssertionError();
  }
  /**
   * This method returns the stateful kieSession from the container.
   *
   * <p>It will build the stateful kieSession by using RulesRequest parameters-
   * buildSessionByKieBase,kieBasename and sessionName. If buildSessionByKieBase is true then it
   * will build session using given kiebase name or default kiebase from container if kiebase name
   * is not given. If buildSessionByKieBase is false then it will build the session using given
   * session name or default session from container if session name is not given.
   *
   * @param rulesRequest
   * @param kContainer
   * @return KieSession
   */
  static KieSession getStatefulKieSession(RulesRequest rulesRequest, KieContainer kContainer) {

    logger.traceEntry("START - method - [getStatefulKieSession(RulesRequest)]");

    if (null == kContainer) {
      logger.error("Can not initialise container");

      throw new RulesApiException("can not initialise container");
    }

    KieSession kSession = null;

    // Getting a default kiesession from kieBase if kiebase name is provided
    if (rulesRequest.isBuildSessionByKieBase()
        && StringUtils.isNotEmpty(rulesRequest.getKieBasename())) {

      logger.debug("Initialise kieBase with kieBase name {}" + rulesRequest.getKieBasename());
      kSession = kContainer.getKieBase(rulesRequest.getKieBasename()).newKieSession();

    }

    // Getting a default kiesession from default kieBase if kiebase name is not provided
    else if (rulesRequest.isBuildSessionByKieBase()
        && StringUtils.isEmpty(rulesRequest.getKieBasename())) {

      logger.debug("Initialise default kieBase");
      kSession = kContainer.getKieBase().newKieSession();
    }

    // If session type passed is state full and session name is given
    else if (!rulesRequest.isBuildSessionByKieBase()
        && StringUtils.isNotEmpty(rulesRequest.getSessionName())) {

      logger.debug("Initialise session with session name {}" + rulesRequest.getSessionName());
      // Initialise session with session name
      kSession = kContainer.newKieSession(rulesRequest.getSessionName());

    } else {

      logger.debug("Getting a default kiesession from Container");
      // Getting a default kiesession from Container
      kSession = kContainer.newKieSession();
    }

    logger.traceExit("END - method - [getStatefulKieSession(RulesRequest)]");
    return kSession;
  }

  /**
   * This method returns the stateless kieSession from the container.
   *
   * <p>It will build the stateless kieSession by using RulesRequest parameters-
   * buildSessionByKieBase,kieBasename and sessionName. If buildSessionByKieBase is true then it
   * will build stateless session using given kiebase name or default kiebase from container if
   * kiebase name is not given. If buildSessionByKieBase is false then it will build the stateless
   * session using given session name or default session from container if session name is not
   * given.
   *
   * @param rulesRequest
   * @param kContainer
   * @return stateless session
   */
  static StatelessKieSession getStatelessKieSession(
      RulesRequest rulesRequest, KieContainer kContainer) {

    logger.traceEntry("START - method - [getStatelessKieSession(RulesRequest)]");

    if (null == kContainer) {
      logger.error("Can not initialise container");

      throw new RulesApiException("an not initialise container");
    }

    StatelessKieSession statelessKieSession = null;

    // Getting a default stateless session from kieBase if kiebase name is provided
    if (rulesRequest.isBuildSessionByKieBase()
        && StringUtils.isNotEmpty(rulesRequest.getKieBasename())) {

      statelessKieSession =
          kContainer.getKieBase(rulesRequest.getKieBasename()).newStatelessKieSession();

    }

    // Getting a default stateless session from default kieBase if kiebase name is not provided
    else if (rulesRequest.isBuildSessionByKieBase()
        && StringUtils.isEmpty(rulesRequest.getKieBasename())) {

      statelessKieSession = kContainer.getKieBase().newStatelessKieSession();

    }

    // If session type passed is state less and session name is given
    else if (!rulesRequest.isBuildSessionByKieBase()
        && StringUtils.isNotEmpty(rulesRequest.getSessionName())) {

      logger.debug(
          "Initialise state less session with session name {}" + rulesRequest.getSessionName());
      // Initialise session with session name
      statelessKieSession = kContainer.newStatelessKieSession(rulesRequest.getSessionName());

    } else {
      logger.debug("Getting a default StatelessKieSession from Container");
      // Getting a default StatelessKieSession from Container
      statelessKieSession = kContainer.newStatelessKieSession();
    }

    logger.traceExit("END - method - [getStatelessKieSession(RulesRequest)]");
    return statelessKieSession;
  }

  /**
   * This method fire the rule using stateful session and returns the rulesResponse
   *
   * <p>It fire the rule after populating session with agenda listener,channel, global element and
   * facts.
   *
   * @param kSession -stateful session
   * @param rulesRequest - Request parameters
   * @param returnedFactsClass
   * @return
   */
  static RulesResponse fireStatefulRules(
      KieSession kSession, RulesRequest rulesRequest, List<Class<?>> returnedFactsClass) {

    logger.traceEntry("START - method - [fireStatefulRules(KieSession,RulesRequest,List<Class>)]");

    if (null != kSession && null != rulesRequest) {

      int numberOfFiredRules = 0;
      List<Object> factsFromSession = null;
      List<String> rulesFired = null;

      RuleAgendaListener ruleAgendaListner = new RuleAgendaListener();

      SendData sendDataChannel = new SendData();

      //Adding the listener to session
      kSession.addEventListener(ruleAgendaListner);

      //Adding the channel to session
      kSession.registerChannel("send-channel", sendDataChannel);

      // Setting global variables and Services
      setGlobalElement(kSession, rulesRequest.getGlobalElement());

      // firing rules by passing the facts
      numberOfFiredRules = fireRulesWithFact(kSession, rulesRequest.getFacts());

      if (!CollectionUtils.isEmpty(returnedFactsClass)) {
        // filter the facts that has been returned from session
        factsFromSession = filterFacts(sendDataChannel, returnedFactsClass);
      } else factsFromSession = sendDataChannel.getNewObjectInsterted();

      rulesFired = ruleAgendaListner.getRulesFired();
      // disposing the session
      kSession.dispose();

      logger.traceEntry("END - method - [fireStatefulRules(KieSession,RulesRequest,List<Class>)]");

      return new RulesResponse(numberOfFiredRules, factsFromSession, rulesFired);
    } else {
      logger.error("KieSession and RulesRequest are mandatory feilds");
      throw new RulesApiException("KieSession and RulesRequest are mandatory feilds");
    }
  }

  /**
   * This method fires rules using stateless session.
   *
   * <p>It fire the rule after populating session with agenda listener,channel, global element and
   * facts.
   *
   * @param statelessKieSession
   * @param rulesRequest
   * @param returnedFactsClass
   * @param kieService
   * @return RulesResponse
   */
  @SuppressWarnings("rawtypes")
  static RulesResponse fireRuleStateless(
      StatelessKieSession statelessKieSession,
      RulesRequest rulesRequest,
      List<Class<?>> returnedFactsClass,
      KieServices kieService) {

    logger.traceEntry("START - method - [fireRuleStateless(KieSession,RulesRequest,List<Class>)]");

    RuleAgendaListener ruleAgendaListner = new RuleAgendaListener();

    SendData sendDataChannel = new SendData();

    if (null != statelessKieSession && null != rulesRequest) {

      int numberOfFiredRules = 0;
      List<Object> factsFromSession = null;
      List<String> rulesFired = null;

      // Setting global variables and Services
      setGlobalElement(statelessKieSession, rulesRequest.getGlobalElement());

      // Adding the listener for the session
      statelessKieSession.addEventListener(ruleAgendaListner);

      //Register Channel
      statelessKieSession.registerChannel("send-channel", sendDataChannel);

      List<Command> commandList = new ArrayList<>();

      if (!CollectionUtils.isEmpty(rulesRequest.getFacts())) { //Inserting the facts in the session
        Command newInsertOrder =
            kieService.getCommands().newInsertElements(rulesRequest.getFacts());
        commandList.add(newInsertOrder);
      }
      Command newFireAllRules = kieService.getCommands().newFireAllRules("outFired");

      commandList.add(newFireAllRules);

      //Executing the command as a batch process
      ExecutionResults execResults =
          statelessKieSession.execute(kieService.getCommands().newBatchExecution(commandList));

      numberOfFiredRules = (int) execResults.getValue("outFired");

      //Filtering the facts that would be returned as a part of response
      if (!CollectionUtils.isEmpty(returnedFactsClass)) {
        factsFromSession = filterFacts(sendDataChannel, returnedFactsClass);
      } else {
        factsFromSession = sendDataChannel.getNewObjectInsterted();
      }
      rulesFired = ruleAgendaListner.getRulesFired();
      logger.traceEntry(
          "START - method - [fireRuleStateless(KieSession,RulesRequest,List<Class>)]");
      return new RulesResponse(numberOfFiredRules, factsFromSession, rulesFired);
    } else {
      logger.error("statelessKieSession and RulesRequest are mandatory feilds");
      throw new RulesApiException("statelessKieSession and RulesRequest are mandatory feilds");
    }
  }

  /**
   * This method will insert facts in stateful session and fire all the rules.
   *
   * @param facts - Facts passed as a part of request
   * @param kSession - session object
   * @return The number of rules fired
   */
  private static int fireRulesWithFact(KieSession kSession, List<Object> facts) {

    if (CollectionUtils.isNotEmpty(facts)) { //Adding all the facts to the session
      facts.forEach(kSession::insert);
    }
    // firing the rules
    return kSession.fireAllRules();
  }

  /**
   * This method will filter the facts that need to be returned in response
   *
   * @param returned Facts Class
   * @return list of objects
   */
  private static List<Object> filterFacts(
      SendData sendDataChannel, List<Class<?>> returnedFactsClass) {

    return sendDataChannel
        .getNewObjectInsterted()
        .stream()
        .filter(element -> returnedFactsClass.contains(element.getClass()))
        .collect(Collectors.toList());
  }

  /**
   * This method set global service and variables to stateful session
   *
   * @param session
   * @param globalElement
   */
  private static void setGlobalElement(KieSession session, Map<String, Object> globalElement) {

    if (null != globalElement && globalElement.size() > 0 && session != null) {

      globalElement.forEach(session::setGlobal);
    }
  }

  /**
   * This method set global service and variables to stateless session
   *
   * @param session
   * @param globalElement
   */
  private static void setGlobalElement(
      StatelessKieSession session, Map<String, Object> globalElement) {

    if (globalElement != null && globalElement.size() > 0 && session != null) {

      globalElement.forEach(session::setGlobal);
    }
  }
}
