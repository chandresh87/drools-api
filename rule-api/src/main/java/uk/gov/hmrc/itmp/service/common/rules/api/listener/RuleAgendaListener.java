package uk.gov.hmrc.itmp.service.common.rules.api.listener;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;
import uk.gov.hmrc.itmp.service.common.rules.api.message.RulesResponse;

/**
 * <strong>This class implements AgendaEventListener.</strong>
 *
 * <p>It is used for logging and returning all the rules matched and fired encapsulated in {@link
 * RulesResponse#getNameOfRulesFired() }.
 *
 * <p>Rule engine internally use this class to register event listener with stateful and stateless
 * session before firing the rules.
 *
 * @since 1.0.0
 */
public class RuleAgendaListener implements AgendaEventListener {

  private Logger log = LogManager.getLogger(this);

  //List of names of rules fired
  private List<String> rulesFired = new ArrayList<>();

  /* (non-Javadoc)
   * @see org.kie.api.event.rule.AgendaEventListener#afterMatchFired(org.kie.api.event.rule.AfterMatchFiredEvent)
   */
  @Override
  public void afterMatchFired(AfterMatchFiredEvent event) {

    log.info("Rules fired : " + event.getMatch().getRule().getName());
    rulesFired.add(event.getMatch().getRule().getName());
  }

  @Override
  public void matchCreated(MatchCreatedEvent event) {
    // No Implementation
  }

  @Override
  public void matchCancelled(MatchCancelledEvent event) {
    // No Implementation
  }

  @Override
  public void beforeMatchFired(BeforeMatchFiredEvent event) {
    // No Implementation

  }

  @Override
  public void agendaGroupPopped(AgendaGroupPoppedEvent event) {

    // No Implementation
  }

  @Override
  public void agendaGroupPushed(AgendaGroupPushedEvent event) {
    // No Implementation

  }

  @Override
  public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {

    // No Implementation
  }

  @Override
  public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {

    // No Implementation
  }

  @Override
  public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {

    // No Implementation
  }

  @Override
  public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {

    // No Implementation
  }

  /** @return the rulesFired List */
  public List<String> getRulesFired() {
    return rulesFired;
  }
}
