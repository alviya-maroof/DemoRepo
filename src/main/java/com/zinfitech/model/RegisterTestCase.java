package com.zinfitech.model;

import com.google.gson.Gson;
import com.zinfitech.pojo.NoCodeStep;
import com.zinfitech.pojo.NoCodeTest;
import com.zinfitech.pojo.TestCase;
import com.zinfitech.pojo.TestStep;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterTestCase {

  private final Gson gson = new Gson();

  public NoCodeTest getTestCase(TestCase testCase, Map<String, String> data) {
    NoCodeTest noCodeTest = gson.fromJson(gson.toJson(testCase), NoCodeTest.class);
    Map<String, Object> dynamicData = new HashMap<>();
    noCodeTest.setDynamicData(dynamicData);
    noCodeTest.setSteps(
        getStep(testCase.getTestStep(), data, dynamicData, testCase.getTestCaseId()));
    return noCodeTest;
  }

  private List<NoCodeStep> getStep(List<TestStep> steps, Map<String, String> data,
      Map<String, Object> dynamicData, String testId) {
    List<NoCodeStep> noCodeSteps = new ArrayList<>();
    for (TestStep step : steps) {
      NoCodeStep noCodeStep = gson.fromJson(gson.toJson(step), NoCodeStep.class);
      noCodeStep.setStepData(data);
      noCodeStep.setDynamicData(dynamicData);
      noCodeStep.setTestCaseId(testId);
      noCodeSteps.add(noCodeStep);
    }
    return noCodeSteps;
  }
}
