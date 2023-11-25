package com.zinfitech.headers;

public enum StepHeader {
  DISABLE("ENABLE/DISABLE"),
  STEP("STEPS"),
  TESTCASE_ID("TESTCASEID"),
  LOCATORS("LOCATORS/INPUTS"),
  OUTPUT("OUTPUT"),
  DESCRIPTION("DESCRIPTION");

  private final String step;

  StepHeader(String stepHeader) {
    this.step = stepHeader;
  }

  @Override
  public String toString() {
    return step;
  }
}
