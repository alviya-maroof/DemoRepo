package com.zinfitech.headers;

import com.zinfitech.zinfiexception.ZinfiTechException;

public enum TestCaseHeader {
  DISABLE("ENABLE/DISABLE"),
  TESTCASE_ID("TESTCASEID"),
  DESCRIPTION("DESCRIPTION"),
  MODULE("TAG"),
  MODULE_NAME("MODULE NAME"),
  PRIORITY("PRIORITY");
  private final String tHeaderName;

  TestCaseHeader(String tHeaderName) {
    this.tHeaderName = tHeaderName;
  }

  @Override
  public String toString() {
    return tHeaderName;
  }

  public enum Priority {
    P0("P0"),
    P1("P1"),
    P2("p2"),
    P3("p3"),
    P4("p4");
    private final String priority;

    Priority(String priority) {
      this.priority = priority;
    }

    @Override
    public String toString() {
      return priority;
    }
  }

  public static Priority getPriority(String key) {
    switch (key) {
      case "P0":
        return Priority.P0;
      case "P1":
        return Priority.P1;
      case "P2":
        return Priority.P2;
      case "P3":
        return Priority.P3;
      case "P4":
        return Priority.P4;
      default:
        throw new ZinfiTechException(key + " priority not found, define correct between P0 to P4");
    }
  }
}
