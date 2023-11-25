package com.zinfitech.context;

import static io.cucumber.core.exception.ExceptionUtils.throwAsUncheckedException;

import com.qapitol.sauron.logging.SauronLogger;
import com.qapitol.sauron.logging.SimpleLogger;
import com.zinfitech.pojo.NoCodeStep;
import com.zinfitech.pojo.NoCodeTest;
import com.zinfitech.zinfiexception.Logger;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestCaseContext extends AbstractTestContext {

  private static final SimpleLogger logger = SauronLogger.getLogger();

  public TestCaseContext(NoCodeTest testCase, AbstractContainer containerClass,
      ExecuteRunner executeRunner) {
    super(containerClass, testCase, executeRunner);

  }

  @Override
  public void saveTestContext(NoCodeTest testCase) {
    setTestCase(testCase);
    setStepContexts(getTestStepContexts(testCase.getSteps()));
  }

  List<StepContext> getTestStepContexts(List<NoCodeStep> testSteps) {
    List<StepContext> testStepContexts1 = new ArrayList<>();
    for (NoCodeStep testStep : testSteps) {
      testStepContexts1.add(
          new StepContext(testStep, this.getContainer(), executeRunner));
    }
    return testStepContexts1;
  }

  @Override
  public void execute(AbstractTestContext testCaseContext) {
    testCaseContext.getStepContexts().forEach(stepContext -> {
      try {
        stepContext.executeStep(stepRunner);
      } catch (Throwable e) {
        stepContext.getTestStep().setResult(executeRunner.getTestResult(e));
        testCaseContext.getTestCase().setTestResult(executeRunner.getTestResult(e));
        executeRunner.onStepFinish(stepContext.getTestStep());
        executeRunner.onTestFinish(testCaseContext.getTestCase());
        Logger.getInstance().updateTestStatusLog((TestCaseContext) testCaseContext);
        throwAsUncheckedException(e.getCause());
      }
    });
    Logger.getInstance().updateTestStatusLog((TestCaseContext) testCaseContext);
  }
}
