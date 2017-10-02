/** */
package com.drools.domain.enums;

/** @author chandresh.mishra */
public enum Rating {
  ONE(1),
  TWO(2),
  THREE(3),
  FOUR(4);

  private int rating;

  /** @param rating */
  private Rating(int rating) {
    this.rating = rating;
  }

  /** @return the rating */
  public int getRating() {
    return rating;
  }
}
