package com.employee;

import com.rules.base.RulesBaseTest;
import java.util.ArrayList;
import java.util.List;
import org.drools.domain.Employee;
import org.kie.api.runtime.KieSession;
import org.testng.annotations.Test;

public class EmployeeTest extends RulesBaseTest {

  private KieSession kSession;

  @Test
  public void incrementTest() {

    Employee employee = new Employee();
    employee.setEmpName("John");
    employee.setRating(1);
    kSession = createKieSessionFromDRL("rules/employee/increment/Salary_Increment_Rule.drl");
    List<Object> list = new ArrayList<>();
    list.add(employee);
    /* int firedrule = fireRule(kSession, list, null);

    System.out.println("rules fired" + firedrule);
    Assert.assertEquals(10, employee.getIncrement());
    destroy(kSession);*/
  }
}
