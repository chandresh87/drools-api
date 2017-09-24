/** */
package com.rules.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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

  protected KieSession createDefaultSession() {

    KieSession ksession = this.createContainer().newKieSession();
    setDefaultListner(ksession);
    return ksession;
  }

  protected KieBase createKnowledgeBase(String name) {
    KieContainer kContainer = this.createContainer();
    KieBase kbase = kContainer.getKieBase(name);

    if (kbase == null) {
      throw new IllegalArgumentException("Unknown Kie Base with name '" + name + "'");
    }

    return kbase;
  }

  protected KieSession createSession(String name) {

    KieContainer kContainer = this.createContainer();
    KieSession ksession = kContainer.newKieSession(name);

    if (ksession == null) {
      throw new IllegalArgumentException("Unknown Session with name '" + name + "'");
    }
    setDefaultListner(ksession);
    return ksession;
  }

  protected StatelessKieSession createStatelessSession(String name) {

    KieContainer kContainer = this.createContainer();
    StatelessKieSession ksession = kContainer.newStatelessKieSession(name);

    if (ksession == null) {
      throw new IllegalArgumentException("Unknown Session with name '" + name + "'");
    }

    ksession.addEventListener(
        new DefaultAgendaEventListener() {
          @Override
          public void afterMatchFired(AfterMatchFiredEvent event) {
            logger.debug("Rules getting fired" + event.getMatch().getRule().getName());
          }
        });
    return ksession;
  }

  protected KieSession createKieSessionFromDRL(String drl) {
    KieHelper kieHelper = new KieHelper();
    kieHelper.addResource(ResourceFactory.newClassPathResource(drl), ResourceType.DRL);

    Results results = kieHelper.verify();

    if (results.hasMessages(Message.Level.WARNING, Message.Level.ERROR)) {
      List<Message> messages = results.getMessages(Message.Level.WARNING, Message.Level.ERROR);
      for (Message message : messages) {
        logger.debug("Error: " + message.getText());
      }

      throw new IllegalStateException("Compilation errors were found. Check the logs.");
    }
    KieSession ksession = kieHelper.build().newKieSession();
    setDefaultListner(ksession);
    return ksession;
  }

  protected int fireRule(
      KieSession kSession, List<Object> facts, Map<String, Object> globalElement) {
    if (null != kSession && null != facts) {
      facts.forEach(fact -> kSession.insert(fact));

      if (globalElement != null && globalElement.size() > 0) {
        globalElement.forEach(
            (key, value) -> {
              kSession.setGlobal(key, value);
            });
      }
    } else throw new IllegalArgumentException("Session and facts are mandatory");

    return kSession.fireAllRules();
  }

  protected int fireRule(
      StatelessKieSession statelessKieSession,
      List<Object> facts,
      Map<String, Object> globalElement) {
    ExecutionResults execResults;
    if (null != statelessKieSession && null != facts) {
      Command newInsertOrder = getKieServices().getCommands().newInsertElements(facts);
      Command newFireAllRules = getKieServices().getCommands().newFireAllRules("outFired");
      List<Command> commandList = new ArrayList<>();
      commandList.add(newInsertOrder);
      commandList.add(newFireAllRules);

      execResults =
          statelessKieSession.execute(
              getKieServices().getCommands().newBatchExecution(commandList));

      if (globalElement != null && globalElement.size() > 0) {
        globalElement.forEach(
            (key, value) -> {
              statelessKieSession.setGlobal(key, value);
            });
      }
    } else throw new IllegalArgumentException("Session and facts are mandatory");

    return (int) execResults.getValue("outFired");
  }

  protected <T> Collection<T> getFactsFromKieSession(KieSession ksession, Class<T> classType) {
    return (Collection<T>) ksession.getObjects(new ClassObjectFilter(classType));
  }

  protected void destroy(KieSession kSession) {
    kSession.dispose();
  }

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

  private KieServices getKieServices() {
    if (null == kService) kService = KieServices.Factory.get();

    return kService;
  }

  private void setDefaultListner(KieSession kSession) {
    kSession.addEventListener(
        new DefaultAgendaEventListener() {
          @Override
          public void afterMatchFired(AfterMatchFiredEvent event) {
            logger.debug("Rules getting fired " + event.getMatch().getRule().getName());
          }
        });
  }
}
