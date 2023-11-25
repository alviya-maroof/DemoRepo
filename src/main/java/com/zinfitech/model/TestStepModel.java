package com.zinfitech.model;

import com.zinfitech.data.NoCodeDataReader;
import com.zinfitech.pojo.TestStep;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class TestStepModel {

  private final NoCodeDataReader dataReader;
  private final Map<String, List<TestStep>> testStep = new HashMap<>();

  public TestStepModel(NoCodeDataReader dataReader) {
    this.dataReader = dataReader;
    loadSteps();
  }

  private void loadSteps() {
    List<TestStep> steps = dataReader.getNoCodeStep();
    String keyName = null;

    for (TestStep step : steps) {
      if (StringUtils.isNotEmpty(step.getTestCaseId()) && StringUtils.isNotBlank(step.getTestCaseId())) {
        keyName = step.getTestCaseId();
      }
      if (isStepEnabled(step)) {
        testStep.putAll(updateTestStep(keyName, testStep, step));
      }
    }
  }

  private Map<String, List<TestStep>> updateTestStep(String keyName,
      Map<String, List<TestStep>> testStep2, TestStep step) {
    List<TestStep> copyStep = new ArrayList<>();
    if (testStep2.containsKey(keyName)) {
      copyStep.addAll(testStep2.get(keyName));
    }
    copyStep.add(step);
    testStep2.put(keyName, copyStep);
    return testStep2;
  }

  private boolean isStepEnabled(TestStep testStep) {
    return testStep.isEnabled();
  }

  public List<TestStep> getTestStep(String stepId) {
    return testStep.getOrDefault(stepId, List.of());
  }

  public void close() {
    testStep.clear();
  }
}
