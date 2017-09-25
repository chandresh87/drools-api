/** */
package com.app.test;

import com.drools.service.EmployeeService;
import com.drools.service.EmployeeServiceImpl;
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
import org.testng.annotations.Test;
import rules.api.engine.RulesEngine;
import rules.api.enums.SessionType;
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

    List<Class> factsReturned = new ArrayList<>();
    factsReturned.add(Promotion.class);

    for (int count = 0; count < 50; count++) {
      RulesRequest droolsParam = populateData();
      Future<RulesResponse> future =
          executorService.submit(
              new Callable() {
                public RulesResponse call() throws Exception {

                  RulesResponse droolsResponse = rulesEngine.fireRules(droolsParam, factsReturned);

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
    List<Class> factsReturned = new ArrayList<>();
    factsReturned.add(Employee.class);

    for (int count = 0; count < 50; count++) {

      Future<RulesResponse> future =
          executorService.submit(
              new Callable() {
                public RulesResponse call() throws Exception {

                  RulesRequest droolsParam = populateData();
                  RulesResponse droolsResponse = rulesEngine.fireRules(droolsParam, factsReturned);

                  return droolsResponse;
                }
              });

      RulesResponse droolsResponse = future.get();

      logger.info("Response from rules engine");
      logger.info("Number of Rules executed = " + droolsResponse.getNumberOfRulesFired());
      Employee employee = (Employee) droolsResponse.getFactsFromSession().get(0);
      logger.info("Employee ID: " + employee.getEmpID());
      logger.info("Salary is: " + employee.getSalary());
      logger.info("Increment: " + employee.getIncrement());
      logger.info("Tax Rate is: " + employee.getTaxRate());
      logger.info("Nino is: " + employee.getNino());
    }
  }
    @Test
    public void multithreadedEnvTest_withSameObject()
        throws InterruptedException, ExecutionException {
  	  
       RulesRequest droolsParam = populateData();
      List<Class> factsReturned = new ArrayList<>();
      factsReturned.add(Employee.class);

      for (int count = 0; count < 50; count++) {

        Future<RulesResponse> future =
            executorService.submit(
                new Callable() {
                  public RulesResponse call() throws Exception {

                   
                    RulesResponse droolsResponse = rulesEngine.fireRules(droolsParam, factsReturned);

                    return droolsResponse;
                  }
                });

        RulesResponse droolsResponse = future.get();

        logger.info("Response from rules engine");
        logger.info("Number of Rules executed = " + droolsResponse.getNumberOfRulesFired());
        Employee employee = (Employee) droolsResponse.getFactsFromSession().get(0);
        logger.info("Employee ID: " + employee.getEmpID());
        logger.info("Salary is: " + employee.getSalary());
        logger.info("Increment: " + employee.getIncrement());
        logger.info("Tax Rate is: " + employee.getTaxRate());
        logger.info("Nino is: " + employee.getNino());
      }
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

    List<Class> factsReturned = new ArrayList<>();
    factsReturned.add(Promotion.class);

    Map<String, Object> globalService = new HashMap();
    globalService.put("employeeService", EmployeeService);

    RulesRequest rulesRequest =
        new RulesRequest.RulesRequestBuilder(facts, false)
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
