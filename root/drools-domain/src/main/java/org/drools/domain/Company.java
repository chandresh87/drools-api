/** */
package org.drools.domain;

import java.util.List;

/** @author chandresh.mishra */
public class Company {

  private String name;
  private List<Employee> employeeList;

  public Company() {}

  public Company(String name, List<Employee> employeeList) {
    super();
    this.name = name;
    this.employeeList = employeeList;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Employee> getEmployeeList() {
    return employeeList;
  }

  public void setEmployeeList(List<Employee> employeeList) {
    this.employeeList = employeeList;
  }
}
