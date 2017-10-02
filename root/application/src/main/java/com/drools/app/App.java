/** */
package com.drools.app;

import com.drools.service.EmployeeService;
import com.drools.service.EmployeeServiceImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.drools.domain.Employee;
import org.drools.domain.Promotion;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import rules.api.config.RulesConfig;
import rules.api.engine.RulesEngine;
import rules.api.enums.SessionType;
import rules.api.message.RulesRequest;
import rules.api.message.RulesResponse;

/** @author chandresh.mishra */
public class App {

  public static void main(String[] args) {
    System.out.println("Bootstrapping the Rule Engine ...");

    ApplicationContext ctx = new AnnotationConfigApplicationContext(RulesConfig.class);

    RulesEngine ApiHelper = ctx.getBean(RulesEngine.class);

    Employee employee = new Employee();
    employee.setEmpID(4722);
    employee.setEmpName("John");
    employee.setRating(1);
    employee.setSalary(32000);

    EmployeeService EmployeeService = new EmployeeServiceImpl();

    List<Object> facts = new ArrayList<>();
    facts.add(employee);

    List<Class> factsReturned = new ArrayList<>();
    factsReturned.add(Promotion.class);

    Map<String, Object> globalService = new HashMap();
    globalService.put("employeeService", EmployeeService);

    RulesRequest droolsParam =
        new RulesRequest.RulesRequestBuilder()
            .facts(facts)
            .buildSessionByKieBase(false)
            .sessionName("rules.employee.increment.statelesssession")
            .sessionType(SessionType.STATELESS)
            .globalService(globalService)
            .build();
    RulesRequest droolsParam2 =
        new RulesRequest.RulesRequestBuilder()
            .facts(facts)
            .buildSessionByKieBase(true)
            .sessionName("rules.employee.tax.session")
            .sessionType(SessionType.STATEFUL)
            .globalService(globalService)
            .kieBasename("rules.employee.tax")
            .build();

    //while(true) {
    RulesResponse droolsResponse = ApiHelper.fireRules(droolsParam2, factsReturned);
    System.out.println("Number of Rules executed = " + droolsResponse.getNumberOfRulesFired());
    System.out.println("Increment: " + employee.getIncrement());
    System.out.println("Tax Rate is: " + employee.getTaxRate());
    System.out.println("Nino is: " + employee.getNino());
    System.out.println("New fact inserted");
    droolsResponse.getFactsFromSession().forEach(object -> System.out.println(object.toString()));

    //}

  }
}
