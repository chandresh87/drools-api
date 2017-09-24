/** */
package rules.api.engine;

import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import rules.api.enums.SessionType;
import rules.api.exception.RulesApiException;
import rules.api.message.RulesRequest;

/**
 * Test class for the RuleEngine
 *
 * @author chandresh.mishra
 */
// @RunWith(SpringJUnit4ClassRunner.class)
// @ContextConfiguration(classes = RulesConfig.class)

public class RulesEngineTest {

  // @Autowired
  @InjectMocks private RulesEngine rulesEngine = new RulesEngineImpl();

  @Mock private RulesEngineHelper rulesEngineHelper;

  /** Setup method - executes before any test in this class. */
  @BeforeMethod
  protected void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testSetUp() {
    assertEquals("class rules.api.engine.RulesEngineImpl", this.rulesEngine.getClass().toString());
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullFactCheck() {
    List<Object> facts = new ArrayList<>();
    RulesRequest rulesRequestParam =
        new RulesRequest.RulesRequestBuilder(facts, false)
            .sessionName("rules.employee.increment.statelesssession")
            .sessionType(SessionType.STATELESS)
            .build();
    // rulesEngine.fireRules(null);
  }

  @Test(expectedExceptions = RulesApiException.class)
  public void testNullRequestParamCheck() {

    rulesEngine.fireRules(null);
  }

  @Test(expectedExceptions = RulesApiException.class)
  public void testDefaultSessionCheck() {
    List<Object> facts = new ArrayList<>();
    facts.add(new Object());
    RulesRequest rulesRequestParam =
        new RulesRequest.RulesRequestBuilder(facts, false).sessionName("ABC").build();
    when(this.rulesEngineHelper.getStatefulKieSession(rulesRequestParam)).thenReturn(null);
    rulesEngine.fireRules(rulesRequestParam);
  }
}
