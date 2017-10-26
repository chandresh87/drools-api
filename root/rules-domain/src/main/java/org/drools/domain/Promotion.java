/** */
package org.drools.domain;

/** @author chandresh.mishra */
public class Promotion {

  private int empID;
  private String empName;

  /**
   * @param empID
   * @param empName
   */
  public Promotion(int empID, String empName) {
    super();
    this.empID = empID;
    this.empName = empName;
  }
  /** @return the empID */
  public int getEmpID() {
    return empID;
  }
  /** @param empID the empID to set */
  public void setEmpID(int empID) {
    this.empID = empID;
  }
  /** @return the empName */
  public String getEmpName() {
    return empName;
  }
  /** @param empName the empName to set */
  public void setEmpName(String empName) {
    this.empName = empName;
  }
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder
        .append("Promotion [empID=")
        .append(empID)
        .append(", empName=")
        .append(empName)
        .append("]");
    return builder.toString();
  }
}
