package com.zinfitech.model;

import com.zinfitech.config.NoCodeConfigProperty;
import com.zinfitech.config.NoCodeConfigProperty.Properties;
import com.zinfitech.data.NoCodeDataReader;
import com.zinfitech.pojo.TestCase;
import com.zinfitech.headers.TestCaseHeader;
import com.zinfitech.headers.TestCaseHeader.Priority;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

public class TestCaseModel {

  private final NoCodeDataReader dataReader;
  private final Map<String, TestCase> testCases = new LinkedHashMap<>();
  private final Priority priority;
  private final String moduleName;

  public TestCaseModel(NoCodeDataReader dataReader) {
    this.dataReader = dataReader;
    loadTestCase();
    priority = getTestPriority();
    moduleName = getModuleName();
  }

  private Priority getTestPriority() {
    if (StringUtils.isEmpty(NoCodeConfigProperty.getConfigProperty(Properties.PRIORITY))) {
      return null;
    }
    return TestCaseHeader.getPriority(NoCodeConfigProperty.getConfigProperty(Properties.PRIORITY));
  }

  private String getModuleName() {
    if (StringUtils.isEmpty(NoCodeConfigProperty.getConfigProperty(Properties.MODULE_NAME))) {
      return null;
    }
    return NoCodeConfigProperty.getConfigProperty(Properties.MODULE_NAME);
  }

  private void loadTestCase() {
    List<TestCase> testCaseList = dataReader.getNoCodeTest();
    for (TestCase testCase : testCaseList) {
      if (isTestCaseEnabled(testCase)) {
        testCases.put(testCase.getTestCaseId(), testCase);
      }
    }
  }

  private boolean isTestCaseEnabled(TestCase testCase) {
    return testCase.isEnabled();
  }

  public TestCase getTestCase(String testCaseId) {
    return testCases.getOrDefault(testCaseId, null);
  }

  public List<String> getTestIdBasedOnModule() {
    List<String> testCaseIds = new ArrayList<>();
    testCases.keySet().forEach(testId -> {
      if (testModuleIsEnabled(testId)) {
        testCaseIds.add(testId);
      }
    });
    return testCaseIds;
  }

  private boolean checkRequiredModuleName(TestCase testCase) {
    if (StringUtils.isEmpty(this.moduleName)) {
      return true;
    }
    return StringUtils.equals(testCase.getModuleName(), this.moduleName);
  }

  private boolean checkRequiredPriority(TestCase testCase) {
    if (Objects.isNull(this.priority)) {
      return true;
    }
    return testCase.getPriority().equals(this.priority);
  }

  private boolean testModuleIsEnabled(String testId) {
    TestCase testCase = testCases.get(testId);
    if (!checkRequiredModuleName(testCase)) {
      return false;
    }
    if (!checkRequiredPriority(testCase)) {
      return false;
    }
    if (NoCodeConfigProperty.getConfigProperty(Properties.TAG).equals("any")) {
      return true;
    }
    return testCase.getTag()
        .contains(NoCodeConfigProperty.getConfigProperty(Properties.TAG));
  }

  public void close() {
    testCases.clear();
  }
}
