/** */
package com.app.test;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;
import uk.gov.hmrc.application.rules.domain.Employee;
import uk.gov.hmrc.application.rules.domain.Promotion;
import uk.gov.hmrc.application.rules.service.EmployeeService;
import uk.gov.hmrc.application.rules.service.EmployeeServiceImpl;
import uk.gov.hmrc.itmp.service.common.rules.api.engine.RulesEngine;
import uk.gov.hmrc.itmp.service.common.rules.api.enums.SessionType;
import uk.gov.hmrc.itmp.service.common.rules.api.exception.RulesApiException;
import uk.gov.hmrc.itmp.service.common.rules.api.message.RulesRequest;
import uk.gov.hmrc.itmp.service.common.rules.api.message.RulesRequest.RulesRequestBuilder;
import uk.gov.hmrc.itmp.service.common.rules.api.message.RulesResponse;
import uk.gov.hmrc.itmp.service.common.rules.api.utils.RulesUtils;

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
      RulesRequest droolsParam = populateData().build();
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

                  RulesRequest droolsParam = populateData().build();
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

    RulesRequest droolsParam = populateData().build();
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
    RulesRequest droolsParam = new RulesRequest.RulesRequestBuilder().build();
    RulesResponse droolsResponse = rulesEngine.fireRules(droolsParam);
    Assert.assertEquals(droolsResponse.getNumberOfRulesFired(), 1);
  }

  @Test
  public void testReturnAllFacts() {
    RulesRequest droolsParam =
        populateData().sessionName("rules.employee.increment.session").globalElement(null).build();

    RulesResponse droolsResponse = rulesEngine.fireRules(droolsParam);
    Assert.assertEquals(droolsResponse.getNumberOfRulesFired(), 4);
    Assert.assertEquals(droolsResponse.getFactsFromSession().size(), 1);
    Assert.assertEquals(droolsResponse.getFactsFromSession().get(0).getClass(), Promotion.class);
  }

  @Test
  public void testReturnSpecificFact_whichIsNotPresent() {
    RulesRequest droolsParam = populateData().build();

    List<Class<?>> returnedFactsClass = new ArrayList<>();
    returnedFactsClass.add(Promotion.class);

    RulesResponse droolsResponse = rulesEngine.fireRules(droolsParam);
    Assert.assertEquals(droolsResponse.getNumberOfRulesFired(), 2);
    Assert.assertEquals(droolsResponse.getFactsFromSession().size(), 0);
  }

  @Test
  public void testReturnSpecificFact_whichIsPresent() {
    RulesRequest droolsParam =
        populateData()
            .sessionName("rules.employee.increment.statelesssession")
            .sessionType(SessionType.STATELESS)
            .build();

    List<Class<?>> returnedFactsClass = new ArrayList<>();
    returnedFactsClass.add(Promotion.class);

    RulesResponse droolsResponse = rulesEngine.fireRules(droolsParam);
    List<Object> filteredFacts =
        RulesUtils.filterFacts(droolsResponse.getFactsFromSession(), returnedFactsClass);
    Assert.assertEquals(droolsResponse.getNumberOfRulesFired(), 4);
    Assert.assertEquals(droolsResponse.getFactsFromSession().size(), 1);
    Assert.assertTrue(
        filteredFacts
            .get(0)
            .getClass()
            .toString()
            .equals("class uk.gov.hmrc.application.rules.domain.Promotion"));
  }

  @Test
  public void testReturnAllPresentFact() {
    RulesRequest droolsParam =
        populateData()
            .sessionName("rules.employee.increment.statelesssession")
            .sessionType(SessionType.STATELESS)
            .build();

    RulesResponse droolsResponse = rulesEngine.fireRules(droolsParam);
    Assert.assertEquals(droolsResponse.getNumberOfRulesFired(), 4);
    Assert.assertEquals(droolsResponse.getFactsFromSession().size(), 1);
  }

  @Test(enabled = false)
  public void testCommonRules() {
    RulesRequest droolsParam =
        populateData()
            .kieBasename("rules.common")
            .buildSessionByKieBase(true)
            .sessionType(SessionType.STATELESS)
            .build();

    int droolsResponse = rulesEngine.fireRules(droolsParam).getNumberOfRulesFired();
    Assert.assertEquals(droolsResponse, 1);
  }

  @Test(enabled = false)
  public void testCommonRuleReload() {
    //while (true) {
    RulesRequest droolsParam =
        populateData()
            .kieBasename("rules.common")
            .buildSessionByKieBase(true)
            .sessionType(SessionType.STATELESS)
            .build();

    int droolsResponse = rulesEngine.fireRules(droolsParam).getNumberOfRulesFired();
    Assert.assertEquals(droolsResponse, 1);
    // }
  }

  @Test
  public void testReturnedRuleNames() {

    RulesRequest rulesRequest = populateData().build();
    RulesResponse rulesResponse = rulesEngine.fireRules(rulesRequest);
    rulesResponse.getNameOfRulesFired();
    Assert.assertEquals(rulesResponse.getNumberOfRulesFired(), 2);
    Assert.assertTrue(rulesResponse.getNameOfRulesFired().contains("Starting rate limit "));
  }

  @Test(expectedExceptions = RulesApiException.class)
  public void testExceptionHandling() {
    RulesRequest droolsParam =
        populateData()
            .sessionName("rules.employee.increment.statelesssession")
            .sessionType(SessionType.STATEFUL)
            .build();

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
    RulesRequest droolsParam =
        populateData()
            .sessionName("rules.employee.increment.statelesssession123")
            .sessionType(SessionType.STATEFUL)
            .build();
    ;

    RulesResponse droolsResponse = rulesEngine.fireRules(droolsParam);
    Assert.assertEquals(droolsResponse.getNumberOfRulesFired(), 3);
    Assert.assertEquals(droolsResponse.getFactsFromSession().size(), 1);
  }

  @Test(expectedExceptions = UnsupportedOperationException.class)
  public void testImmutability() {
    RulesRequest rulesRequest = populateData().build();
    RulesResponse rulesResponse = rulesEngine.fireRules(rulesRequest);

    rulesResponse.getNameOfRulesFired().add("Dummy Rules");
  }

  private RulesRequestBuilder populateData() {

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
    globalService.put("startingLimit", 5000);

    RulesRequestBuilder rulesRequestBuilder =
        new RulesRequest.RulesRequestBuilder()
            .facts(facts)
            .buildSessionByKieBase(false)
            .sessionName("rules.employee.tax.session")
            .sessionType(SessionType.STATEFUL)
            .globalElement(globalService)
            .kieBasename("rules.employee.tax");
    ++salary;
    ++empId;
    return rulesRequestBuilder;
  }
}
