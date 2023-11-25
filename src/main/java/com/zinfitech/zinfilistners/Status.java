package com.zinfitech.zinfilistners;

public enum Status {
  PASS("pass"),
  FAIL("fail"),
  BROKEN("broken");

  private final String testStatus;

  Status(String status) {
    this.testStatus = status;
  }

  @Override
  public String toString() {
    return testStatus;
  }
}
