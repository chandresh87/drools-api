package com.employee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.gov.hmrc.application.rules.domain.Employee;
import uk.gov.hmrc.application.rules.domain.Promotion;
import uk.gov.hmrc.application.rules.enums.SalaryIncrement;
import uk.gov.hmrc.application.rules.service.EmployeeService;
import uk.gov.hmrc.application.rules.service.EmployeeServiceImpl;
import uk.gov.hmrc.itmp.service.common.rules.test.base.RulesBaseTest;
import uk.gov.hmrc.itmp.service.common.rules.test.base.message.RuleTestResponse;

public class EmployeeTest extends RulesBaseTest {

  private KieSession kSession;
  private StatelessKieSession statelessKieSession;

  @BeforeMethod
  public void setup() {
    kSession = null;
    statelessKieSession = null;
  }

  @Test
  public void incrementTest_SeesionFromDrl() {

    Employee employee = new Employee();
    employee.setEmpName("John");
    employee.setRating(1);
    kSession =
        createStatefulKieSessionFromDRL(
            "uk/gov/hmrc/itmp/application/rules/employee/increment/Salary_Increment_Rule.drl");
    List<Object> list = new ArrayList<>();
    list.add(employee);
    RuleTestResponse ruleTestResponse = fireRule(kSession, list, null);

    Assert.assertEquals(4, ruleTestResponse.getNumberOfRulesFired());
    Assert.assertEquals(10, employee.getIncrement());

    Assert.assertTrue(ruleTestResponse.getNameOfRulesFired().contains("Entitle for promotion"));
  }

  @Test
  public void fireDefaultSession() {
    kSession = createDefaultSession();

    Employee employee = new Employee();
    employee.setEmpName("John");
    employee.setRating(1);
    employee.setSalary(5000);
    List<Object> list = new ArrayList<>();
    list.add(employee);

    Map<String, Object> globalService = new HashMap<>();
    EmployeeService EmployeeService = new EmployeeServiceImpl();
    globalService.put("employeeService", EmployeeService);

    RuleTestResponse ruleTestResponse = fireRule(kSession, list, globalService);
    Assert.assertEquals(6, ruleTestResponse.getNumberOfRulesFired());
    Assert.assertEquals(10, employee.getIncrement());
    Assert.assertEquals(0, employee.getTax());
  }

  @Test
  public void fireDefaultStatelessSession() {

    statelessKieSession = createDefaultStatelessSession();

    Employee employee = new Employee();
    employee.setEmpName("John");
    employee.setRating(1);
    employee.setSalary(5000);
    List<Object> list = new ArrayList<>();
    list.add(employee);

    Map<String, Object> globalService = new HashMap<>();
    EmployeeService EmployeeService = new EmployeeServiceImpl();
    globalService.put("employeeService", EmployeeService);

    RuleTestResponse ruleTestResponse = fireRule(statelessKieSession, list, globalService);
    Assert.assertEquals(6, ruleTestResponse.getNumberOfRulesFired());
    Assert.assertEquals(10, employee.getIncrement());
    Assert.assertEquals(0, employee.getTax());
  }

  @Test
  public void fireRulesUsingKiebase() {

    KieBase Kbase = createKnowledgeBase("KBase");

    statelessKieSession = Kbase.newStatelessKieSession();

    Employee employee = new Employee();
    employee.setEmpName("John");
    employee.setRating(1);
    employee.setSalary(5000);
    List<Object> list = new ArrayList<>();
    list.add(employee);

    Map<String, Object> globalService = new HashMap<>();
    EmployeeService EmployeeService = new EmployeeServiceImpl();
    globalService.put("employeeService", EmployeeService);

    RuleTestResponse ruleTestResponse = fireRule(statelessKieSession, list, globalService);
    Assert.assertEquals(6, ruleTestResponse.getNumberOfRulesFired());
    Assert.assertEquals(10, employee.getIncrement());
    Assert.assertEquals(0, employee.getTax());
  }

  @Test
  public void fireRules_usingSessionName() {

    Employee employee = new Employee();
    employee.setEmpName("John");
    employee.setRating(1);
    kSession = createSession("rules.employee.increment.session");
    List<Object> list = new ArrayList<>();
    list.add(employee);
    RuleTestResponse ruleTestResponse = fireRule(kSession, list, null);

    Assert.assertEquals(4, ruleTestResponse.getNumberOfRulesFired());
    Assert.assertEquals(10, employee.getIncrement());

    Assert.assertTrue(ruleTestResponse.getNameOfRulesFired().contains("Entitle for promotion"));
  }

  @Test
  public void fireRules_usingStateLessSessionName() {

    Employee employee = new Employee();
    employee.setEmpName("John");
    employee.setRating(1);
    statelessKieSession = createStatelessSession("rules.employee.increment.statelesssession");
    List<Object> list = new ArrayList<>();
    list.add(employee);
    RuleTestResponse ruleTestResponse = fireRule(statelessKieSession, list, null);

    Assert.assertEquals(4, ruleTestResponse.getNumberOfRulesFired());
    Assert.assertEquals(10, employee.getIncrement());

    Assert.assertTrue(ruleTestResponse.getNameOfRulesFired().contains("Entitle for promotion"));
  }

  @Test
  public void fireRules_nullFacts() {

    kSession = createSession("rules.employee.increment.session");

    RuleTestResponse ruleTestResponse = fireRule(kSession, null, null);

    Assert.assertEquals(1, ruleTestResponse.getNumberOfRulesFired());
  }

  @Test
  public void testNewFactwithStateless() {

    statelessKieSession = createStatelessSession("rules.employee.increment.statelesssession");
    Employee employee = new Employee();
    employee.setEmpName("John");
    employee.setRating(1);
    employee.setSalary(5000);
    List<Object> list = new ArrayList<>();
    list.add(employee);

    Map<String, Object> globalService = new HashMap<>();
    EmployeeService EmployeeService = new EmployeeServiceImpl();
    globalService.put("employeeService", EmployeeService);

    RuleTestResponse ruleTestResponse = fireRule(statelessKieSession, list, globalService);
    Assert.assertEquals(4, ruleTestResponse.getNumberOfRulesFired());
    Assert.assertEquals(10, employee.getIncrement());
    Assert.assertTrue(
        ruleTestResponse
            .getNameOfRulesFired()
            .contains("Print Employee name entitled for promotion"));
  }

  @Test
  public void filterFacts() {
    Employee employee = new Employee();
    employee.setEmpName("John");
    employee.setRating(1);
    kSession = createSession("rules.employee.increment.session");
    List<Object> list = new ArrayList<>();
    list.add(employee);
    RuleTestResponse ruleTestResponse = fireRule(kSession, list, null);

    Object[] promotion = getFactsFromKieSession(kSession, Promotion.class).toArray();
    Assert.assertNotNull(promotion[0]);
  }

  @Test
  public void starAwardTest() {
    Employee employee = new Employee();
    employee.setEmpName("John");
    employee.setRating(3);
    employee.setStarAwardReceived(true);
    kSession = createDefaultSession();
    List<Object> list = new ArrayList<>();
    list.add(employee);

    Map<String, Object> globalService = new HashMap<>();
    EmployeeService EmployeeService = new EmployeeServiceImpl();
    globalService.put("employeeService", EmployeeService);

    kSession.getAgenda().getAgendaGroup("Salary Increment").setFocus();
    kSession.getAgenda().getAgendaGroup("Salary Increment special").setFocus();

    RuleTestResponse ruleTestResponse = fireRule(kSession, list, globalService);

    Assert.assertEquals(5, ruleTestResponse.getNumberOfRulesFired());
    Assert.assertEquals(SalaryIncrement.BGRADEINCREMENT.getIncrement(), employee.getIncrement());
  }

  @Test
  public void activationGroupTest() {
    Employee employee = new Employee();
    employee.setEmpName("John");
    employee.setRating(1);

    kSession = createSession("rules.employee.increment.session");
    List<Object> list = new ArrayList<>();
    list.add(employee);

    RuleTestResponse ruleTestResponse = fireRule(kSession, list, null);

    Assert.assertEquals(4, ruleTestResponse.getNumberOfRulesFired());
  }
}
