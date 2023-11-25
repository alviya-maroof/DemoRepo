package com.zinfitech.model;

import com.zinfitech.pojo.NoCodeTest;
import com.zinfitech.pojo.TestCase;
import com.zinfitech.pojo.TestStep;
import java.util.List;

public interface NoCodeTestModelService extends AutoCloseable{

  public TestCase getTestCase(String testId);

  public List<NoCodeTest> getTestCases(String testId);

  public List<TestStep> getTestStep(String testId);

  public List<String> getTestCaseIds();

  public TestCase getMergedStepIntoTest(String testId);

  public TestDataModel getTestDataModel();

  public LocatorModel getLocatorModel();
}
