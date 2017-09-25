/** */
package org.drools.domain;

/** @author chandresh.mishra */
public class Employee {

  private int empID;
  private String empName;
  private int salary;
  private int rating;
  private int increment;
  private int taxRate;
  private String nino;

  /** @return the empID */
  public int getEmpID() {
    return empID;
  }
  /** @param empID the empID to set */
  public void setEmpID(int empID) {
    this.empID = empID;
  }
  /** @return the taxRate */
  public int getTaxRate() {
    return taxRate;
  }
  /** @param taxRate the taxRate to set */
  public void setTaxRate(int taxRate) {
    this.taxRate = taxRate;
  }
  /** @return the nino */
  public String getNino() {
    return nino;
  }
  /** @param nino the nino to set */
  public void setNino(String nino) {
    this.nino = nino;
  }

  public String getEmpName() {
    return empName;
  }

  public void setEmpName(String empName) {
    this.empName = empName;
  }

  public int getSalary() {
    return salary;
  }

  public void setSalary(int salary) {
    this.salary = salary;
  }

  public int getRating() {
    return rating;
  }

  public void setRating(int rating) {
    this.rating = rating;
  }

  public int getIncrement() {
    return increment;
  }

  public void setIncrement(int increment) {
    this.increment = increment;
  }

  public int getTax() {
    return taxRate;
  }

  public void setTax(int tax) {
    this.taxRate = tax;
  }
}
