/** */
package com.rules.application.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmrc.application.rules.domain.Employee;
import uk.gov.hmrc.application.rules.service.EmployeeService;
import uk.gov.hmrc.application.rules.service.EmployeeServiceImpl;
import uk.gov.hmrc.itmp.service.common.rules.api.engine.RulesEngine;
import uk.gov.hmrc.itmp.service.common.rules.api.enums.SessionType;
import uk.gov.hmrc.itmp.service.common.rules.api.message.RulesRequest;
import uk.gov.hmrc.itmp.service.common.rules.api.message.RulesResponse;

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
            .globalElement(globalService)
            .kieBasename("rules.employee.tax")
            .build();

    return rulesRequest;
  }
}
