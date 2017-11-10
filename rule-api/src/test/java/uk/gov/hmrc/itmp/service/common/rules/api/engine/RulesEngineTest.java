/** */
package uk.gov.hmrc.itmp.service.common.rules.api.engine;

import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.drools.core.event.AgendaEventSupport;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.gov.hmrc.itmp.service.common.rules.api.enums.SessionType;
import uk.gov.hmrc.itmp.service.common.rules.api.exception.RulesApiException;
import uk.gov.hmrc.itmp.service.common.rules.api.message.RulesRequest;
import uk.gov.hmrc.itmp.service.common.rules.api.message.RulesResponse;

/**
 * Test class for the RuleEngine
 *
 * @author chandresh.mishra
 */
public class RulesEngineTest {

  @InjectMocks private RulesEngine rulesEngine = new RulesEngineImpl();

  @Mock private KieContainer kContainer;
  @Mock private KieServices kieService;
  @Mock private KieSession kSession;
  private StatefulKnowledgeSessionImpl statefulKnowledgeSessionImpl =
      new StatefulKnowledgeSessionImpl();

  /** Setup method - executes before any test in this class. */
  @BeforeMethod
  protected void setUp() {

    statefulKnowledgeSessionImpl.setAgendaEventSupport(new AgendaEventSupport());
    kSession = statefulKnowledgeSessionImpl;
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testSetUp() {
    assertEquals(
        "class uk.gov.hmrc.itmp.service.common.rules.api.engine.RulesEngineImpl",
        this.rulesEngine.getClass().toString());
  }

  @Test(
    expectedExceptions = RulesApiException.class,
    expectedExceptionsMessageRegExp = "Can not instantiate KieSession.Please check configuration"
  )
  public void testNullSessionType() {
    RulesRequest rulesRequest = new RulesRequest.RulesRequestBuilder().build();
    when(this.kContainer.newKieSession()).thenReturn(null);
    rulesEngine.fireRules(rulesRequest);
  }

  @Test(
    expectedExceptions = RulesApiException.class,
    expectedExceptionsMessageRegExp = "Can not instantiate KieSession.Please check configuration"
  )
  public void testStateFulSessionType() {
    RulesRequest rulesRequest =
        new RulesRequest.RulesRequestBuilder().sessionType(SessionType.STATEFUL).build();
    when(this.kContainer.newKieSession()).thenReturn(null);
    rulesEngine.fireRules(rulesRequest);
  }

  @Test()
  public void testStateFul_DefaultSession() {
    List<Object> facts = new ArrayList<>();
    facts.add(new Object());

    RulesRequest rulesRequest =
        new RulesRequest.RulesRequestBuilder()
            .sessionType(SessionType.STATEFUL)
            // .facts(facts)
            .build();
    when(this.kContainer.newKieSession()).thenReturn(kSession);
    //  when(this.kSession.insert(Object.class)).then(null);
    RulesResponse rulesResponse = rulesEngine.fireRules(rulesRequest);

    Assert.assertEquals(0, rulesResponse.getNumberOfRulesFired());
  }

  @Test(expectedExceptions = RulesApiException.class)
  public void testStateFul_DefaultSession_KieBaseName() {
    StatefulKnowledgeSessionImpl statefulKnowledgeSessionImpl = new StatefulKnowledgeSessionImpl();

    List<Object> facts = new ArrayList<>();
    facts.add(new Object());

    statefulKnowledgeSessionImpl.setAgendaEventSupport(new AgendaEventSupport());

    KieSession kSession = statefulKnowledgeSessionImpl;

    RulesRequest rulesRequest =
        new RulesRequest.RulesRequestBuilder()
            .sessionType(SessionType.STATEFUL)
            .buildSessionByKieBase(true)
            // .facts(facts)
            .build();
    // when(this.kContainer.getKieBase().newKieSession()).thenReturn(kSession);

    RulesResponse rulesResponse = rulesEngine.fireRules(rulesRequest);

    Assert.assertEquals(0, rulesResponse.getNumberOfRulesFired());
  }

  @Test(
    expectedExceptions = RulesApiException.class,
    expectedExceptionsMessageRegExp = "RulesRequest is mandatory"
  )
  public void testNullRequest() {

    rulesEngine.fireRules(null);
  }

  @Test(expectedExceptions = RulesApiException.class)
  public void testNullRequestParamCheck() {

    rulesEngine.fireRules(null);
  }
}
