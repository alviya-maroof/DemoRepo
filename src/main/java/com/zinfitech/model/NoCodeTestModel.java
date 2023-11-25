package com.zinfitech.model;

import com.zinfitech.data.NoCodeDataReader;
import com.zinfitech.pojo.NoCodeTest;
import com.zinfitech.pojo.TestCase;
import com.zinfitech.pojo.TestStep;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NoCodeTestModel implements NoCodeTestModelService {

  private final TestCaseModel testCaseModel;
  private final TestStepModel stepModel;
  private final TestDataModel testDataModel;
  private final LocatorModel locatorModel;
  private static NoCodeTestModelService modelService;
  private final RegisterTestCase registerTestCase = new RegisterTestCase();

  private NoCodeTestModel(NoCodeDataReader noCodeDataReader) {
    testCaseModel = new TestCaseModel(noCodeDataReader);
    stepModel = new TestStepModel(noCodeDataReader);
    testDataModel = new TestDataModel(noCodeDataReader);
    locatorModel = new LocatorModel(noCodeDataReader);
  }

  @Override
  public TestCase getTestCase(String testId) {
    return testCaseModel.getTestCase(testId);
  }

  @Override
  public List<TestStep> getTestStep(String testId) {
    return stepModel.getTestStep(testId);
  }

  @Override
  public List<String> getTestCaseIds() {
    return testCaseModel.getTestIdBasedOnModule();
  }

  @Override
  public TestCase getMergedStepIntoTest(String testId) {
    TestCase testCase = testCaseModel.getTestCase(testId);
    testCase.setTestStep(getTestStep(testId));
    return testCase;
  }

  public List<NoCodeTest> getTestCases(String testId) {
    List<NoCodeTest> testCases = new ArrayList<>();
    List<Map<String, String>> stepData = testDataModel.getTestCaseData(testId);
    TestCase testCase = getMergedStepIntoTest(testId);
    if (stepData.isEmpty()) {
      testCases.add(registerTestCase.getTestCase(testCase, Map.of()));
      return testCases;
    }
    stepData.forEach(testData -> testCases.add(registerTestCase.getTestCase(testCase, testData)));
    return testCases;
  }

  public static NoCodeTestModelService getModelService(NoCodeDataReader noCodeDataReader) {
    if (Objects.isNull(modelService)) {
      modelService = new NoCodeTestModel(noCodeDataReader);
    }
    return modelService;
  }

  public TestDataModel getTestDataModel() {
    return testDataModel;
  }

  public LocatorModel getLocatorModel() {
    return locatorModel;
  }

  @Override
  public void close() {
    testCaseModel.close();
    stepModel.close();
  }
}
