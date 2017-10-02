/** */
package com.drools.domain.enums;

/** @author chandresh.mishra */
public enum Tax {
  STARTING_TAX(0),
  BASIC_TAX(20),
  HIGHER_TAX(40),
  ADDITIONAL_TAX(45);

  private int tax;

  /** @param tax */
  private Tax(int tax) {
    this.tax = tax;
  }

  /** @return the tax */
  public int getTax() {
    return tax;
  }
}
