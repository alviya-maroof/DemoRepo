package com.qapitol.sauron.azuredevops.listeners;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.qapitol.sauron.azuredevops.configuration.AzureDevopsConfigProperty;
import com.qapitol.sauron.azuredevops.configuration.AzureTestState;
import com.qapitol.sauron.azuredevops.configuration.AzureTestStatus;
import com.qapitol.sauron.azuredevops.pojo.TestPointResult;
import com.qapitol.sauron.azuredevops.pojo.TestResultAttachment;
import com.qapitol.sauron.azuredevops.pojo.TestRun;
import com.qapitol.sauron.azuredevops.pojo.TestRunResult;
import com.qapitol.sauron.azuredevops.testplan.AzureTestPlan;
import com.qapitol.sauron.azuredevops.testplan.AzureTestPlanService;
import com.qapitol.sauron.common.LinkedListener;
import com.qapitol.sauron.common.Platform;
import com.qapitol.sauron.common.SauronCoreListener;
import com.qapitol.sauron.common.configuration.Config;
import com.qapitol.sauron.common.pojos.Response;
import com.qapitol.sauron.common.sessions.AbstractTestSession;
import com.qapitol.sauron.logging.SauronLogger;
import com.qapitol.sauron.logging.SimpleLogger;
import com.qapitol.sauron.report.core.ScreenshotContext;
import com.zinfitech.pojo.NoCodeStep;
import com.zinfitech.pojo.NoCodeTest;
import com.zinfitech.zinfilistners.Status;
import com.zinfitech.zinfilistners.ZinfiTechListener;
import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;

public class SauronAzureTestPlanListener implements SauronCoreListener<AzureDevopsConfigProperty>,
    LinkedListener, IInvokedMethodListener, ISuiteListener, ZinfiTechListener {

  private final AzureTestPlanService service = new AzureTestPlan();
  private static final SimpleLogger logger = SauronLogger.getLogger();

  private static final String VALUES = "value";
  private static final String AZURE_BUILD_ID = System.getenv("BUILD_BUILDID");
  private static final String OS_NAME = System.getenv("os.name");
  private static final String COMMENT = "This test run is completed";

  private static final Map<String, Integer> testCasePoints = new HashMap<>();
  private static final Map<Integer, String> testPointResultId = new HashMap<>();
  private final Gson gson = new Gson();


  @Override
  public void onStartSuite(List<NoCodeTest> testCases) {
    if (!service.isAzureDevopsEnabled()) {
      logger.log(Level.INFO, "azure devops is not enabled, skipping test runner creation");
      return;
    }
    if (!service.isProjectNameNotNull()) {
      logger.log(Level.INFO, "project name must not be null or empty");
      return;
    }
    List<Integer> planAndTestPoints = service.getTestCasePoints(getTestCases(testCases));
    TestRun runModel = TestRun.builder().name("first azure test plan").pointIds(planAndTestPoints)
        .automated(true).startedDate(Instant.now().toString()).plan(Map.of("id",
            Config.getConfigProperty(AzureDevopsConfigProperty.AZURE_DEVOPS_TEST_PLAN))).build();
    Response response = service.createTestRun(
        Config.getConfigProperty(AzureDevopsConfigProperty.AZURE_DEVOPS_PROJECT_NAME),
        getUpdatedTestRunModel(runModel));
    Config.setConfigProperty(AzureDevopsConfigProperty.AZURE_DEVOPS_TEST_RUN,
        service.getTestRunnerID(response.getBody()));
    updateTestCasePointsAndTestRunResultIds(service.getTestRunResult(
        Config.getConfigProperty(AzureDevopsConfigProperty.AZURE_DEVOPS_TEST_RUN),
        Config.getConfigProperty(AzureDevopsConfigProperty.AZURE_DEVOPS_PROJECT_NAME)).getBody());
  }

  @Override
  public void onFinishSuite(List<NoCodeTest> testCases) {
    if (!service.isAzureDevopsEnabled() || !service.isAzureDevopsTestRunEmpty()) {
      logger.log(Level.INFO,
          "azure devops is not enabled, empty testcases, skipping test runner creation");
      return;
    }
    TestRunResult testRunResultModel = TestRunResult.builder()
        .id(Config.getConfigProperty(AzureDevopsConfigProperty.AZURE_DEVOPS_TEST_RUN))
        .comment(COMMENT).buildPlatform(OS_NAME).completedDate(Instant.now().toString())
        .state(AzureTestState.getState(AzureTestState.COMPLETED))
        .testOutcome(AzureTestStatus.getAzureTestStatus(AzureTestStatus.PASS)).build();
    for (NoCodeTest testCase : testCases) {
      if (testCase.getTestResult().getStatus() != Status.PASS) {
        testRunResultModel.setTestOutcome(AzureTestStatus.getAzureTestStatus(AzureTestStatus.FAIL));
        break;
      }
    }
    service.closeTestRunner(
        Config.getConfigProperty(AzureDevopsConfigProperty.AZURE_DEVOPS_PROJECT_NAME),
        Config.getConfigProperty(AzureDevopsConfigProperty.AZURE_DEVOPS_TEST_RUN),
        getUpdatedTestRunResultModel(testRunResultModel));
  }

  @Override
  public void onStartTestStart(NoCodeTest testCase) {
    // not implemented
  }


  @Override
  public void onTestFinish(NoCodeTest testCase) {
    if (service.isBDDType()) {
      return;
    }

    if (!service.isProjectNameNotNull()) {
      logger.log(Level.INFO, "project name must not be null or empty");
      return;
    }

    if (!service.isAzureDevopsTestRunEmpty() || !service.isAzureDevopsEnabled()) {
      logger.log(Level.INFO,
          "azure devops test plan execution is disabled or test runner id is not created, skipping test case status to update in test runner");
      return;
    }
    String testStatus = testCase.getTestResult().getStatus().equals(Status.PASS)
        ? AzureTestStatus.getAzureTestStatus(AzureTestStatus.PASS)
        : AzureTestStatus.getAzureTestStatus(AzureTestStatus.FAIL);
    TestPointResult testResultUpdateModel = TestPointResult.builder()
        .id(testPointResultId.get(testCasePoints.get(testCase.getTestCaseId()))).outcome(testStatus)
        .state(AzureTestState.getState(AzureTestState.COMPLETED))
        .testPoint(String.valueOf(testCasePoints.get(testCase.getTestCaseId())))
        .testPlan(Config.getConfigProperty(AzureDevopsConfigProperty.AZURE_DEVOPS_TEST_PLAN))
        .completedDate(Instant.now().toString()).build();
    if (Objects.nonNull(testCase.getTestResult().getThrowable())) {
      testResultUpdateModel.setErrorMessage(testCase.getTestResult().getThrowable().toString());
    }
    service.updateTestPointResult(
        Config.getConfigProperty(AzureDevopsConfigProperty.AZURE_DEVOPS_PROJECT_NAME),
        Config.getConfigProperty(AzureDevopsConfigProperty.AZURE_DEVOPS_TEST_RUN),
        testResultUpdateModel);
    this.attachTestResult(testCase,
        testPointResultId.get(testCasePoints.get(testCase.getTestCaseId())));
    if (testCase.getTestResult().getStatus().equals(Status.FAIL) && service.isEnabledCreateBug()) {
      service.createBug(
          Config.getConfigProperty(AzureDevopsConfigProperty.AZURE_DEVOPS_PROJECT_NAME),
          Config.getConfigProperty(AzureDevopsConfigProperty.AZURE_CREATE_ISSUE_TYPE),
          testCase.getTestCaseId(), testCase.getTestResult().getThrowable().toString());
    }
  }

  @Override
  public void onStepStart(NoCodeStep step) {
    // not implemented
  }

  @Override
  public void onStepFinish(NoCodeStep step) {
    // not implemented
  }

  private void updateTestCasePointsAndTestRunResultIds(String jsonBody) {
    JsonObject jsonObject = gson.fromJson(jsonBody, JsonObject.class);
    JsonArray jsonArray = jsonObject.getAsJsonArray(VALUES);
    for (int i = 0; i < jsonArray.size(); i++) {
      JsonObject jsonObject1 = jsonArray.get(i).getAsJsonObject();
      testCasePoints.put(jsonObject1.getAsJsonObject("testCase").get("id").getAsString(),
          jsonObject1.getAsJsonObject("testPoint").get("id").getAsInt());
      testPointResultId.put(jsonObject1.getAsJsonObject("testPoint").get("id").getAsInt(),
          jsonObject1.get("id").getAsString());
    }
  }

  private List<String> getTestCases(List<NoCodeTest> testCaseContexts) {
    return testCaseContexts.parallelStream().map(NoCodeTest::getTestCaseId)
        .collect(Collectors.toList());
  }

  private TestRun getUpdatedTestRunModel(TestRun runModel) {
    if (Objects.nonNull(AZURE_BUILD_ID)) {
      runModel.setBuild(Map.of("id", AZURE_BUILD_ID));
      runModel.setBuildPlatform(OS_NAME);
      return runModel;
    }
    return runModel;
  }

  private TestRunResult getUpdatedTestRunResultModel(TestRunResult runModel) {
    if (Objects.nonNull(AZURE_BUILD_ID)) {
      Response response = service.getBuildById(
          Config.getConfigProperty(AzureDevopsConfigProperty.AZURE_DEVOPS_PROJECT_NAME),
          AZURE_BUILD_ID);
      JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
      runModel.setComment(jsonObject.get("result").getAsString());
      runModel.setCompletedDate(jsonObject.get("finishTime").getAsString());
      return runModel;
    }
    return runModel;
  }

  private void attachTestResult(NoCodeTest testCase, String testCaseResultId) {
    if (testCase.getTestResult().getStatus() == Status.FAIL) {
      for (Pair<String, String> screenshot : ScreenshotContext.getAllScreenshot()) {
        TestResultAttachment attachment = TestResultAttachment.builder()
            .attachmentType("GeneralAttachment")
            .fileName(screenshot.getLeft().replaceAll(Pattern.quote("\\"), "\\\\").split("\\\\")[1])
            .stream(service.getBase64Format("sauronFiles"+ File.separator +screenshot.getLeft())).build();
        service.testResultAttachment(
            Config.getConfigProperty(AzureDevopsConfigProperty.AZURE_DEVOPS_PROJECT_NAME),
            Config.getConfigProperty(AzureDevopsConfigProperty.AZURE_DEVOPS_TEST_RUN),
            testCaseResultId, attachment);
      }
    }
  }

  @Override
  public void alterTestNgSuite(List<XmlSuite> suites) {
    // not implemented
  }

  @Override
  public void onStartSuite(ISuite suite) {
    // not implemented
  }

  @Override
  public void onFinishSuite(ISuite suite) {
    // not implemented
  }

  @Override
  public void onStartTest(ITestContext context) {
    // not implemented
  }

  @Override
  public void onFinishTest(ITestContext context) {
    // not implemented
  }

  @Override
  public List<AbstractTestSession> initSessions(IInvokedMethod method, ITestResult testResult,
      ITestContext context) {
    return new ArrayList<>();
  }

  @Override
  public List<AbstractTestSession> finishSessions(IInvokedMethod method, ITestResult testResult,
      ITestContext context) {
    return new ArrayList<>();
  }

  @Override
  public List<Platform> getSupportedPlatforms() {
    return new ArrayList<>();
  }

  @Override
  public List<AzureDevopsConfigProperty> getConfigProperties() {
    return new ArrayList<>();
  }

  @Override
  public void onSessionsConfigReceived(List<Map<String, Object>> sessionConfigs) {
    // not implemented
  }

  @Override
  public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
    // not implemented
  }

  @Override
  public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
    // not implemented
  }
}
