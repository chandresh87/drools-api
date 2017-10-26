/** */
package rules.test.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.drools.core.event.DefaultAgendaEventListener;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.command.Command;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;

/**
 * This is a base test class for testing rules. It provide convenience method for creating
 * container,session and firing rules.
 *
 * @author chandresh.mishra
 */
public class RulesBaseTest {

  private KieServices kService;
  private KieContainer kContainer;

  private Logger logger = LogManager.getLogger(this);

  /**
   * This method provides the default stateful kiesession
   *
   * @return KieSession
   */
  protected KieSession createDefaultSession() {

    KieSession ksession = this.createContainer().newKieSession();
    return ksession;
  }

  /**
   * This method provides the default StatelessKieSession
   *
   * @return statelessKieSession
   */
  protected StatelessKieSession createDefaultStatelessSession() {

    StatelessKieSession statelessKieSession = this.createContainer().newStatelessKieSession();
    return statelessKieSession;
  }

  /**
   * This method gives the kiebase from the container
   *
   * @param kieBaseName -Name of kieBase
   * @return KieBase - Returns the kieBase
   */
  protected KieBase createKnowledgeBase(String kieBaseName) {
    KieContainer kContainer = this.createContainer();
    KieBase kbase = kContainer.getKieBase(kieBaseName);

    if (kbase == null) {
      throw new IllegalArgumentException("Unknown Kie Base with name '" + kieBaseName + "'");
    }

    return kbase;
  }

  /**
   * This method returns a given session from container
   *
   * @param sessionName -Name of session
   * @return KieSession - Returns the KieSession
   */
  protected KieSession createSession(String sessionName) {

    KieContainer kContainer = this.createContainer();
    KieSession ksession = kContainer.newKieSession(sessionName);

    if (ksession == null) {
      throw new IllegalArgumentException("Unknown Session with name '" + sessionName + "'");
    }

    return ksession;
  }

  /**
   * This method gives a state less kiesession from container
   *
   * @param name - Name of stateless kieSession
   * @return StatelessKieSession
   */
  protected StatelessKieSession createStatelessSession(String name) {

    KieContainer kContainer = this.createContainer();
    StatelessKieSession ksession = kContainer.newStatelessKieSession(name);

    if (ksession == null) {
      throw new IllegalArgumentException("Unknown Session with name '" + name + "'");
    }

    return ksession;
  }

  /**
   * Set agenda Listener to StatelessKieSession
   *
   * @param ksession
   */
  /* private void setAgendaListner(StatelessKieSession statelessKieSession) {
    statelessKieSession.addEventListener(
        new DefaultAgendaEventListener() {
          @Override
          public void afterMatchFired(AfterMatchFiredEvent event) {
            logger.debug("Rules getting fired" + event.getMatch().getRule().getName());
          }
        });
  }*/

  /**
   * This method is used to test a particular drl file. It build a session using a given drl file.
   *
   * @param drlFile -Name of drl file present at classpath
   * @return KieSession
   */
  protected KieSession createKieSessionFromDRL(String drlFile) {
    KieHelper kieHelper = new KieHelper();
    kieHelper.addResource(ResourceFactory.newClassPathResource(drlFile), ResourceType.DRL);

    Results results = kieHelper.verify();

    if (results.hasMessages(Message.Level.WARNING, Message.Level.ERROR)) {
      List<Message> messages = results.getMessages(Message.Level.WARNING, Message.Level.ERROR);
      for (Message message : messages) {
        logger.debug("Error: " + message.getText());
      }

      throw new IllegalStateException("Compilation errors were found. Check the logs.");
    }
    KieSession ksession = kieHelper.build().newKieSession();

    return ksession;
  }

  /**
   * It fires a rule using state full session and returns number of rules fired
   *
   * @param kSession - stateful KieSession object
   * @param facts - Facts passed to a engine
   * @param globalElement - Global elements passed to engine
   * @return Number of rules fired
   */
  protected RuleTestResponse fireRule(
      KieSession kSession, List<Object> facts, Map<String, Object> globalElement) {

    RuleTestResponse ruleTestResponse = null;
    if (null != kSession) {

      Helper helper = new Helper();
      kSession.registerChannel("send-channel", helper);
      kSession.addEventListener(helper.getAgendaListener());

      if (globalElement != null && globalElement.size() > 0) {
        globalElement.forEach(
            (key, value) -> {
              kSession.setGlobal(key, value);
            });
      }

      if (CollectionUtils.isNotEmpty(facts)) {
        facts.forEach(fact -> kSession.insert(fact));
      }

      int numberOfRulesFired = kSession.fireAllRules();

      ruleTestResponse =
          new RuleTestResponse(
              numberOfRulesFired, helper.getNewObjectInsterted(), helper.getRulesFired());
      destroy(kSession);
    } else throw new IllegalArgumentException("Session is mandatory");

    return ruleTestResponse;
  }

  /**
   * It fires a rule using state less session and returns number of rules fired
   *
   * @param statelessKieSession - statelessKieSession object
   * @param facts - Facts passed to a engine
   * @param globalElement - Global elements passed to engine
   * @return int - Number of rules fired
   */
  @SuppressWarnings("rawtypes")
  protected RuleTestResponse fireRule(
      StatelessKieSession statelessKieSession,
      List<Object> facts,
      Map<String, Object> globalElement) {
    ExecutionResults execResults;

    RuleTestResponse ruleTestResponse = null;

    if (null != statelessKieSession) {

      Helper helper = new Helper();
      statelessKieSession.registerChannel("send-channel", helper);
      statelessKieSession.addEventListener(helper.getAgendaListener());

      if (globalElement != null && globalElement.size() > 0) {
        globalElement.forEach(
            (key, value) -> {
              statelessKieSession.setGlobal(key, value);
            });
      }

      Command newInsertOrder = getKieServices().getCommands().newInsertElements(facts);
      Command newFireAllRules = getKieServices().getCommands().newFireAllRules("outFired");
      List<Command> commandList = new ArrayList<>();
      commandList.add(newInsertOrder);
      commandList.add(newFireAllRules);

      execResults =
          statelessKieSession.execute(
              getKieServices().getCommands().newBatchExecution(commandList));

      int numberOfRulesFired = (int) execResults.getValue("outFired");

      ruleTestResponse =
          new RuleTestResponse(
              numberOfRulesFired, helper.getNewObjectInsterted(), helper.getRulesFired());
    } else throw new IllegalArgumentException("Session is mandatory");

    return ruleTestResponse;
  }

  /**
   * It is used to filter facts from session.
   *
   * @param ksession - stateful KieSession object
   * @param classType - Class class object
   * @param <T> - Generic type for class class
   * @return collection of facts
   */
  protected <T> Collection<? extends Object> getFactsFromKieSession(
      KieSession ksession, Class<T> classType) {
    return ksession.getObjects(new ClassObjectFilter(classType));
  }

  /**
   * Destroy the session and free all the resources.
   *
   * @param kSession - stateful KieSession object
   */
  protected void destroy(KieSession kSession) {
    kSession.dispose();
  }

  /**
   * It builds the container using class path resources.
   *
   * @return KieContainer
   */
  private KieContainer createContainer() {

    if (kContainer != null) {
      return kContainer;
    }

    KieContainer Container = getKieServices().getKieClasspathContainer();

    Results results = Container.verify();

    if (results.hasMessages(Message.Level.WARNING, Message.Level.ERROR)) {

      List<Message> messages = results.getMessages(Message.Level.WARNING, Message.Level.ERROR);

      for (Message message : messages) {

        logger.error(
            "Compilation errors in rules: {} in file {} at line number  {} and coloumn {}.Error message is: {}",
            message.getLevel(),
            message.getPath(),
            message.getLine(),
            message.getColumn(),
            message.getText());
      }

      throw new IllegalStateException("Compilation errors were found. Check the logs.");
    }

    kContainer = Container;
    return kContainer;
  }

  /**
   * It gives a kieservice
   *
   * @return KieServices
   */
  private KieServices getKieServices() {
    if (null == kService) kService = KieServices.Factory.get();

    return kService;
  }

  /**
   * This method sets the default agenda listener to a session
   *
   * @param kSession
   */
  /* private void setDefaultListner(KieSession kSession) {
    kSession.addEventListener(
        new DefaultAgendaEventListener() {
          @Override
          public void afterMatchFired(AfterMatchFiredEvent event) {
            logger.debug("Rules getting fired " + event.getMatch().getRule().getName());
          }
        });
  }*/

  private class Helper implements Channel {
    //List of new facts inserted
    private ArrayList<Object> newObjectInsterted = new ArrayList<>();

    //List of name of rules fired
    private List<String> rulesFired = new ArrayList<>();

    @Override
    public void send(Object object) {
      newObjectInsterted.add(object);
      logger.debug("inserted new fact in channels" + object.toString());
    }

    public DefaultAgendaEventListener getAgendaListener() {
      return new DefaultAgendaEventListener() {

        @Override
        public void afterMatchFired(AfterMatchFiredEvent event) {
          logger.debug("Rules getting fired " + event.getMatch().getRule().getName());
          rulesFired.add(event.getMatch().getRule().getName());
        }
      };
    }

    /** @return the newObjectInsterted */
    public ArrayList<Object> getNewObjectInsterted() {
      return newObjectInsterted;
    }

    /** @return the rulesFired */
    public List<String> getRulesFired() {
      return rulesFired;
    }
  }
}
