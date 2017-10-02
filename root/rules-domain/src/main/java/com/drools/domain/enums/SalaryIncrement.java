/** */
package com.drools.domain.enums;

/** @author chandresh.mishra */
public enum SalaryIncrement {
  AGRADEINCREMENT(10),
  BGRADEINCREMENT(7),
  CGRADEINCREMENT(5),
  DGRADEINCREMENT(0);

  private int increment;

  /** @param increment */
  private SalaryIncrement(int increment) {
    this.increment = increment;
  }

  /** @return the increment */
  public int getIncrement() {
    return increment;
  }
}
