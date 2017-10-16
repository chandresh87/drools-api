/** */
package com.rules.application.service;

import com.drools.service.EmployeeService;
import com.drools.service.EmployeeServiceImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.drools.domain.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rules.api.engine.RulesEngine;
import rules.api.enums.SessionType;
import rules.api.message.RulesRequest;
import rules.api.message.RulesResponse;

/** @author chandresh.mishra */
@Service
public class RulesService {

  @Autowired private RulesEngine rulesEngine;

  public int fireAppRules() {
    RulesRequest rulesRequest = populateData();
    RulesResponse rulesResponse = rulesEngine.fireRules(rulesRequest);
    return rulesResponse.getNumberOfRulesFired();
  }

  private RulesRequest populateData() {

    Employee employee = new Employee();
    employee.setEmpID(1);
    employee.setEmpName("John");
    employee.setRating(1);
    employee.setSalary(50000);

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

    return rulesRequest;
  }
}
