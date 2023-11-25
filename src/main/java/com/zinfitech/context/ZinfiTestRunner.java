package com.zinfitech.context;


import com.zinfitech.config.NoCodeConfigProperty;
import com.zinfitech.config.NoCodeConfigProperty.Properties;
import com.zinfitech.data.ZinfiTechDataFactory;
import com.zinfitech.model.NoCodeTestModel;
import com.zinfitech.model.NoCodeTestModelService;
import com.zinfitech.pojo.NoCodeTest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ZinfiTestRunner {

  private final AbstractContainer container;
  private final ExecuteRunner executeRunner;
  private final List<TestCaseContext> contexts;

  public ZinfiTestRunner(Class<?> claas) {
    container = new Container(NoCodeConfigProperty.getConfigProperty(Properties.PACKAGE_NAME));
    executeRunner = new ExecuteRunner(claas);
    contexts = getTestContexts();
    executeRunner.onStartSuite(contexts.stream().map(AbstractTestContext::getTestCase).collect(
        Collectors.toList()));
  }

  public void runScenarios(AbstractTestContext testContext) {
    executeRunner.onStartTestStart(testContext.getTestCase());
    testContext.execute(testContext);
    testContext.getTestCase().setTestResult(executeRunner.getTestResult(null));
    executeRunner.onTestFinish(testContext.getTestCase());
  }

  private List<TestCaseContext> getTestContexts() {
    List<TestCaseContext> testCaseContexts = new ArrayList<>();
    try (NoCodeTestModelService noCodeTestModelService = NoCodeTestModel.getModelService(
        ZinfiTechDataFactory.getDataReader());) {
      List<String> testId = noCodeTestModelService.getTestCaseIds();
      for (String id : testId) {
        List<NoCodeTest> testCases = noCodeTestModelService.getTestCases(id);
        for (NoCodeTest testCase : testCases) {
          testCaseContexts.add(
              new TestCaseContext(testCase, container,
                  executeRunner));
        }
      }
      return testCaseContexts;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public List<TestCaseContext> testCaseContextsProvider() {
    return this.contexts;
  }

  public void finishSuite() {
    executeRunner.onFinishSuite(contexts.stream().map(AbstractTestContext::getTestCase).collect(
        Collectors.toList()));
  }
}
