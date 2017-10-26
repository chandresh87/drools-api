/** */
package com.app.test;

import com.drools.service.EmployeeService;
import com.drools.service.EmployeeServiceImpl;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.drools.domain.Employee;
import org.drools.domain.Promotion;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;
import rules.api.engine.RulesEngine;
import rules.api.enums.SessionType;
import rules.api.exception.RulesApiException;
import rules.api.message.RulesRequest;
import rules.api.message.RulesResponse;

/** @author chandresh.mishra */
public class StressTesting extends BaseTest {

  @Autowired RulesEngine rulesEngine;

  private Logger logger = LogManager.getLogger(this);
  private ExecutorService executorService = Executors.newFixedThreadPool(50);
  private static int salary = 4975;
  private static int empId = 4763;

  @Test
  public void multithreadedEnvTest_withDiffObject()
      throws InterruptedException, ExecutionException {

    List<Class<?>> factsReturned = new ArrayList<>();
    factsReturned.add(Promotion.class);

    for (int count = 0; count < 50; count++) {
      RulesRequest droolsParam = populateData();
      Future<RulesResponse> future =
          executorService.submit(
              new Callable() {
                public RulesResponse call() throws Exception {

                  RulesResponse droolsResponse = rulesEngine.fireRules(droolsParam);

                  return droolsResponse;
                }
              });

      RulesResponse droolsResponse = future.get();

      logger.info("Response from rules engine");
      logger.info("Number of Rules executed = " + droolsResponse.getNumberOfRulesFired());
      Employee employee = (Employee) droolsParam.getFacts().get(0);
      logger.info("Employee ID: " + employee.getEmpID());
      logger.info("Salary is: " + employee.getSalary());
      logger.info("Increment: " + employee.getIncrement());
      logger.info("Tax Rate is: " + employee.getTaxRate());
      logger.info("Nino is: " + employee.getNino());
    }
  }

  @Test
  public void multithreadedEnvTest_withDiffObject2()
      throws InterruptedException, ExecutionException {
    salary = 0;
    List<Class<?>> factsReturned = new ArrayList<>();
    factsReturned.add(Employee.class);

    for (int count = 0; count < 50; count++) {

      Future<RulesResponse> future =
          executorService.submit(
              new Callable() {
                public RulesResponse call() throws Exception {

                  RulesRequest droolsParam = populateData();
                  RulesResponse droolsResponse = rulesEngine.fireRules(droolsParam);

                  Employee employee = (Employee) droolsParam.getFacts().get(0);
                  logger.info("Employee ID: " + employee.getEmpID());
                  logger.info("Salary is: " + employee.getSalary());
                  logger.info("Increment: " + employee.getIncrement());
                  logger.info("Tax Rate is: " + employee.getTaxRate());
                  logger.info("Nino is: " + employee.getNino());

                  return droolsResponse;
                }
              });

      RulesResponse droolsResponse = future.get();

      logger.info("Response from rules engine");
      logger.info("Number of Rules executed = " + droolsResponse.getNumberOfRulesFired());
    }
  }

  @Test
  public void multithreadedEnvTest_withSameObject()
      throws InterruptedException, ExecutionException {

    RulesRequest droolsParam = populateData();
    List<Class> factsReturned = new ArrayList<>();
    //factsReturned.add(Employee.class);

    for (int count = 0; count < 50; count++) {

      Future<RulesResponse> future =
          executorService.submit(
              new Callable() {
                public RulesResponse call() throws Exception {

                  RulesResponse droolsResponse = rulesEngine.fireRules(droolsParam);

                  return droolsResponse;
                }
              });

      RulesResponse droolsResponse = future.get();

      logger.info("Response from rules engine");
      logger.info("Number of Rules executed = " + droolsResponse.getNumberOfRulesFired());
      Employee employee = (Employee) droolsParam.getFacts().get(0);
      logger.info("Employee ID: " + employee.getEmpID());
      logger.info("Salary is: " + employee.getSalary());
      logger.info("Increment: " + employee.getIncrement());
      logger.info("Tax Rate is: " + employee.getTaxRate());
      logger.info("Nino is: " + employee.getNino());
    }
  }

  @Test
  public void testNullFacts()
      throws NoSuchMethodException, SecurityException, IllegalAccessException,
          IllegalArgumentException, InvocationTargetException, InstantiationException {
    RulesRequest droolsParam = new RulesRequest();
    RulesResponse droolsResponse = rulesEngine.fireRules(droolsParam);
    Assert.assertEquals(droolsResponse.getNumberOfRulesFired(), 1);
  }

  @Test
  public void testReturnAllFacts() {
    RulesRequest droolsParam = populateData();
    droolsParam.setSessionName("rules.employee.increment.session");
    droolsParam.setGlobalElement(null);
    RulesResponse droolsResponse = rulesEngine.fireRules(droolsParam);
    Assert.assertEquals(droolsResponse.getNumberOfRulesFired(), 4);
    Assert.assertEquals(droolsResponse.getFactsFromSession().size(), 1);
    Assert.assertEquals(droolsResponse.getFactsFromSession().get(0).getClass(), Promotion.class);
  }

  @Test
  public void testReturnSpecificFact_whichIsNotPresent() {
    RulesRequest droolsParam = populateData();

    List<Class<?>> returnedFactsClass = new ArrayList<>();
    returnedFactsClass.add(Promotion.class);

    RulesResponse droolsResponse = rulesEngine.fireRules(droolsParam);
    Assert.assertEquals(droolsResponse.getNumberOfRulesFired(), 2);
    Assert.assertEquals(droolsResponse.getFactsFromSession().size(), 0);
  }

  @Test
  public void testReturnSpecificFact_whichIsPresent() {
    RulesRequest droolsParam = populateData();
    droolsParam.setSessionName("rules.employee.increment.statelesssession");
    droolsParam.setSessionType(SessionType.STATELESS);

    List<Class<?>> returnedFactsClass = new ArrayList<>();
    returnedFactsClass.add(Promotion.class);

    RulesResponse droolsResponse = rulesEngine.fireRules(droolsParam);
    List<Object> filteredFacts =
        rulesEngine.filterFacts(droolsResponse.getFactsFromSession(), returnedFactsClass);
    Assert.assertEquals(droolsResponse.getNumberOfRulesFired(), 4);
    Assert.assertEquals(droolsResponse.getFactsFromSession().size(), 1);
    Assert.assertTrue(
        filteredFacts.get(0).getClass().toString().equals("class org.drools.domain.Promotion"));
  }

  @Test
  public void testReturnAllPresentFact() {
    RulesRequest droolsParam = populateData();
    droolsParam.setSessionName("rules.employee.increment.statelesssession");
    droolsParam.setSessionType(SessionType.STATELESS);

    RulesResponse droolsResponse = rulesEngine.fireRules(droolsParam);
    Assert.assertEquals(droolsResponse.getNumberOfRulesFired(), 4);
    Assert.assertEquals(droolsResponse.getFactsFromSession().size(), 1);
  }

  @Test(enabled = false)
  public void testCommonRules() {
    RulesRequest droolsParam = populateData();
    droolsParam.setKieBasename("rules.common");
    droolsParam.setSessionType(SessionType.STATELESS);
    droolsParam.setBuildSessionByKieBase(true);
    int droolsResponse = rulesEngine.fireRules(droolsParam).getNumberOfRulesFired();
    Assert.assertEquals(droolsResponse, 1);
  }

  @Test(enabled = false)
  public void testCommonRuleReload() {
    //while (true) {
    RulesRequest droolsParam = populateData();
    droolsParam.setKieBasename("rules.common");
    droolsParam.setSessionType(SessionType.STATELESS);
    droolsParam.setBuildSessionByKieBase(true);
    int droolsResponse = rulesEngine.fireRules(droolsParam).getNumberOfRulesFired();
    Assert.assertEquals(droolsResponse, 1);
    // }
  }

  @Test
  public void testReturnedRuleNames() {

    RulesRequest rulesRequest = populateData();
    RulesResponse rulesResponse = rulesEngine.fireRules(rulesRequest);
    rulesResponse.getNameOfRulesFired();
    Assert.assertEquals(rulesResponse.getNumberOfRulesFired(), 2);
    Assert.assertTrue(rulesResponse.getNameOfRulesFired().contains("Starting rate limit "));
  }

  @Test(expectedExceptions = RulesApiException.class)
  public void testExceptionHandling() {
    RulesRequest droolsParam = populateData();
    droolsParam.setSessionName("rules.employee.increment.statelesssession");
    droolsParam.setSessionType(SessionType.STATEFUL);

    RulesResponse droolsResponse = rulesEngine.fireRules(droolsParam);
    Assert.assertEquals(droolsResponse.getNumberOfRulesFired(), 3);
    Assert.assertEquals(droolsResponse.getFactsFromSession().size(), 1);
  }

  @Test(expectedExceptions = RulesApiException.class)
  public void testExceptionHandlingWithNull() {

    RulesResponse droolsResponse = rulesEngine.fireRules(null);
    Assert.assertEquals(droolsResponse.getNumberOfRulesFired(), 3);
    Assert.assertEquals(droolsResponse.getFactsFromSession().size(), 1);
  }

  @Test(expectedExceptions = RulesApiException.class)
  public void testExceptionHandlingWithUnknownSession() {
    RulesRequest droolsParam = populateData();
    droolsParam.setSessionName("rules.employee.increment.statelesssession123");
    droolsParam.setSessionType(SessionType.STATEFUL);

    RulesResponse droolsResponse = rulesEngine.fireRules(droolsParam);
    Assert.assertEquals(droolsResponse.getNumberOfRulesFired(), 3);
    Assert.assertEquals(droolsResponse.getFactsFromSession().size(), 1);
  }

  private RulesRequest populateData() {

    Employee employee = new Employee();
    employee.setEmpID(empId);
    employee.setEmpName("John");
    employee.setRating(1);
    employee.setSalary(salary);

    EmployeeService EmployeeService = new EmployeeServiceImpl();

    List<Object> facts = new ArrayList<>();
    facts.add(employee);

    Map<String, Object> globalService = new HashMap();
    globalService.put("employeeService", EmployeeService);

    RulesRequest rulesRequest =
        new RulesRequest.RulesRequestBuilder()
            .facts(facts)
            .buildSessionByKieBase(false)
            .sessionName("rules.employee.tax.session")
            .sessionType(SessionType.STATEFUL)
            .globalService(globalService)
            .kieBasename("rules.employee.tax")
            .build();
    ++salary;
    ++empId;
    return rulesRequest;
  }
}
