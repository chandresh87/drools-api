package uk.gov.hmrc.itmp.service.common.rules.api.engine;

import uk.gov.hmrc.itmp.service.common.rules.api.exception.RulesApiException;
import uk.gov.hmrc.itmp.service.common.rules.api.message.RulesRequest;
import uk.gov.hmrc.itmp.service.common.rules.api.message.RulesResponse;

/**
 * <strong>This interface has method to fire rules by passing the RulesRequest and get the
 * RulesResponse.</strong>
 *
 * <p>It simplifies the communication with the Drools Engine, firing rules and getting response. It
 * handles the loading of rules and firing the rules,leaving the application code to provide the
 * RulesRequest input. Implemented by {@link RulesEngineImpl}
 *
 * <p><strong>Note:</strong> Rules API load the rules from classpath.
 *
 * <pre>
 *  Rule Engine can:
 *
 *  Build a stateful session for a given session name in Kmodule.xml.
 *  Build a stateless session for a given session name in Kmodule.xml.
 *  Build a stateful session for a given Kbase name in Kmodule.xml.
 *  Build a stateless session for a given Kbase name in Kmodule.xml.
 *  Build a default stateful session.
 *  Build a default stateless session.
 * </pre>
 *
 * <p>Rule Engine throws RulesApiException. All the standard Drools exception is wrapped inside
 * RulesApiException.
 *
 * <p>Rule engine throws exception in following case:
 *
 * <pre>
 * RulesRequest is null.
 * Rule engine is not able to initialise session using a given session or Kiebase name.
 * Rule engine is requested for a default session and default is not present in kmodule.
 * </pre>
 *
 * <p>Simple example showing executing rules using Rules Engine API.
 *
 * <pre>
 * {@code @Autowired RulesEngine rulesEngine;
 *
 * Employee employee = new Employee();
 * employee.setEmpID(empId);
 * employee.setEmpName(name);
 * employee.setSalary(salary);
 * List<Object> facts = new ArrayList<>();
 * facts.add(employee);
 *
 * Map<String, Object> globalObject = new HashMap();
 * globalObject.put("employeeConstant", employeeConstant);
 *
 * RulesRequest rulesRequest =
 * new RulesRequest.RulesRequestBuilder()
 * .facts(facts)
 * .buildSessionByKieBase(false)
 * .sessionName("rules.employee.session")
 * .sessionType(SessionType.STATEFUL)
 * .globalElement(globalObject).build();
 *
 * RulesResponse rulesResponse = rulesEngine.fireRules(rulesRequest);
 *
 * }</pre>
 *
 * @see RulesRequest
 * @see RulesResponse
 * @since 1.0.0
 */
public interface RulesEngine {

  /**
   * This method is used to fire the rules and return the RulesResponse.
   *
   * @param rulesRequest All the parameter required to fire the rule.
   * @return RulesResponse Number of rules fired ,name of rules fired and list of facts from
   *     session.
   * @throws RulesApiException In case rulesRequest is null or engine is not able to load given
   *     kiebase or kiession from the container.
   */
  public RulesResponse fireRules(RulesRequest rulesRequest);
}
