package com.zinfitech;


import com.qapitol.sauron.common.annotations.SauronTest;
import com.zinfitech.annotations.Listeners;
import com.zinfitech.context.TestCaseContext;
import com.zinfitech.context.ZinfiTestRunner;
import java.util.List;
import java.util.stream.Collectors;
import org.testng.ITestContext;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Listeners(enable = true, listeners = {"com.zinfitech.allurereport.ZinfiTechAllureReport",
    "com.zinfitech.allurereport.ScreenshotListener",
    "com.zinfitech.allurereport.CsvReportListner",
    "com.qapitol.sauron.azuredevops.listeners.SauronAzureTestPlanListener"
})
public class TestRunner {

  ZinfiTestRunner zinfiTestRunner = null;

  @BeforeClass(alwaysRun = true)
  public void setupClass(ITestContext context) {
    zinfiTestRunner = new ZinfiTestRunner(this.getClass());
  }


  @Test(description = "Runs Cucumber Scenarios",
      dataProvider = "scenarios")
  @SauronTest
  public void runScenario(TestCaseContext testContext) {
    zinfiTestRunner.runScenarios(testContext);
  }

  @DataProvider(parallel = true)
  public Object[][] scenarios() {
    List<TestCaseContext> testCaseContexts = zinfiTestRunner.testCaseContextsProvider();
    Object[][] scenarios = testCaseContexts.parallelStream()
        .map(testCaseContext -> new TestCaseContext[]{testCaseContext})
        .collect(Collectors.toList()).toArray(new Object[0][0]);
    testCaseContexts.clear();
    return scenarios;
  }

  @AfterSuite
  public void afterSuite() {
    zinfiTestRunner.finishSuite();
  }
}
