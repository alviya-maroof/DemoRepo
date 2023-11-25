package com.zinfitech.allurereport;

import static io.qameta.allure.util.ResultsUtils.createFrameworkLabel;
import static io.qameta.allure.util.ResultsUtils.createHostLabel;
import static io.qameta.allure.util.ResultsUtils.createLanguageLabel;
import static io.qameta.allure.util.ResultsUtils.createPackageLabel;
import static io.qameta.allure.util.ResultsUtils.createParentSuiteLabel;
import static io.qameta.allure.util.ResultsUtils.createSuiteLabel;
import static io.qameta.allure.util.ResultsUtils.createTestClassLabel;
import static io.qameta.allure.util.ResultsUtils.createTestMethodLabel;
import static io.qameta.allure.util.ResultsUtils.createThreadLabel;
import static io.qameta.allure.util.ResultsUtils.getProvidedLabels;
import static io.qameta.allure.util.ResultsUtils.getStatusDetails;

import com.qapitol.sauron.logging.SauronLogger;
import com.qapitol.sauron.logging.SimpleLogger;
import com.qapitol.sauron.report.core.ScreenshotContext;
import com.qapitol.sauron.report.core.config.ReportConfig;
import com.qapitol.sauron.report.core.config.ReportConfig.Properties;
import com.zinfitech.config.NoCodeConfigProperty;
import com.zinfitech.data.ZinfiTechDataFactory;
import com.zinfitech.model.LocatorModel;
import com.zinfitech.model.NoCodeTestModel;
import com.zinfitech.model.TestDataModel;
import com.zinfitech.pojo.NoCodeStep;
import com.zinfitech.pojo.NoCodeTest;
import com.zinfitech.pojo.ZiniFunctionalInterface;
import com.zinfitech.zinfiexception.ZinfiTechException;
import com.zinfitech.zinfilistners.ZinfiTechListener;
import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.Label;
import io.qameta.allure.model.Parameter;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StatusDetails;
import io.qameta.allure.model.StepResult;
import io.qameta.allure.model.TestResult;
import io.qameta.allure.model.TestResultContainer;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;

public class ZinfiTechAllureReport implements ZinfiTechListener, ZiniFunctionalInterface {

  private static final SimpleLogger logger = SauronLogger.getLogger();

  private static final String ALLURE_LANGUAGE = "java";
  private static final String ALLURE_FRAMEWORK_LABEL = "testng";
  private static final String PACKAGE_NAME = "com.zinfitech.page";
  private static final String WEB_ACTION_CLASS = "webactionClass";
  private final ThreadLocal<String> currentTestContainer = ThreadLocal  //NOSONAR
      .withInitial(() -> UUID.randomUUID().toString());
  private final ThreadLocal<String> testCasesUuid = new ThreadLocal<>(); //NOSONAR

  private final ThreadLocal<String> stepUuid = new ThreadLocal<>(); //NOSONAR
  private static final AllureLifecycle lifecycle = Allure.getLifecycle();

  private final TestDataModel testData = NoCodeTestModel.getModelService(
      ZinfiTechDataFactory.getDataReader()).getTestDataModel();


  private final LocatorModel locatorUtil = NoCodeTestModel.getModelService(
      ZinfiTechDataFactory.getDataReader()).getLocatorModel();

  public AllureLifecycle getLifecycle() {
    return lifecycle;
  }

  private String getUniqueUuid() {
    return UUID.randomUUID().toString();
  }

  @Override
  public int getPriority() {
    return 3;
  }

  @Override
  public void onStartSuite(List<NoCodeTest> testCases) {
    final TestResultContainer result = new TestResultContainer().setUuid(currentTestContainer.get())
        .setName("allureReport").setStart(System.currentTimeMillis());
    getLifecycle().startTestContainer(result);
  }

  @Override
  public void onFinishSuite(List<NoCodeTest> testCases) {
    final String uuid = currentTestContainer.get();
    getLifecycle().stopTestContainer(uuid);
    getLifecycle().writeTestContainer(uuid);
    ZinfiSystemInfo.attachAllureSystemInfo();
    ZinfiSystemInfo.attachCategory();
  }

  @Override
  public void onStartTestStart(NoCodeTest testCase) {
    final String parentUuid = currentTestContainer.get();
    testCasesUuid.set(getUniqueUuid());
    startTestCases(parentUuid, testCasesUuid.get(), testCase);
  }

  @Override
  public void onTestFinish(NoCodeTest testCase) {
    if (testCase.getTestResult().getStatus() == com.zinfitech.zinfilistners.Status.PASS) {
      getLifecycle().updateTestCase(testCasesUuid.get(), setStatus(Status.PASSED));
    } else if (testCase.getTestResult().getStatus() == com.zinfitech.zinfilistners.Status.BROKEN) {
      getLifecycle().updateTestCase(testCasesUuid.get(),
          setStatus(Status.BROKEN, getStatusDetail(testCase.getTestResult().getThrowable())));
    } else {
      getLifecycle().updateTestCase(testCasesUuid.get(),
          setStatus(Status.FAILED, getStatusDetail(testCase.getTestResult().getThrowable())));
    }
    attachScreenshot();
    getLifecycle().stopTestCase(testCasesUuid.get());
    getLifecycle().writeTestCase(testCasesUuid.get());
  }

  @Override
  public void onStepStart(NoCodeStep step) {
    final String parentUuid = testCasesUuid.get();
    stepUuid.set(getUniqueUuid());
    startStep(parentUuid, stepUuid.get(), step);
  }

  @Override
  public void onStepFinish(NoCodeStep step) {
    if (step.getResult().getStatus() == com.zinfitech.zinfilistners.Status.FAIL) {
      getLifecycle().updateStep(stepUuid.get(),
          setStepStatus(Status.FAILED, getStatusDetail(step.getResult().getThrowable()), step));
    } else if (step.getResult().getStatus() == com.zinfitech.zinfilistners.Status.BROKEN) {
      getLifecycle().updateStep(stepUuid.get(),
          setStepStatus(Status.BROKEN, getStatusDetail(step.getResult().getThrowable())));
    } else {
      getLifecycle().updateStep(stepUuid.get(), setStepStatus(Status.PASSED, step));
    }
    attachScreenshot();
    getLifecycle().stopStep(stepUuid.get());

  }

  private void startTestCases(String parentUuid, String uuid, NoCodeTest method) {
    final List<Label> labels = new ArrayList<>();
    labels.addAll(getProvidedLabels());
    labels.addAll(
        Arrays.asList(createPackageLabel(PACKAGE_NAME), createTestClassLabel(WEB_ACTION_CLASS),
            createTestMethodLabel(method.getTestCaseId()+": "+method.getName()), createParentSuiteLabel(
                ReportConfig.getConfigProperty(Properties.REPORT_ALLURE_SUITE_NAME)),
            createSuiteLabel(NoCodeConfigProperty.getConfigProperty(
                NoCodeConfigProperty.Properties.TAG)), createHostLabel(),
            createThreadLabel(), createFrameworkLabel(ALLURE_FRAMEWORK_LABEL),
            createLanguageLabel(ALLURE_LANGUAGE)));
    String testName = method.getTestCaseId()+": "+method.getName();
    final TestResult result = new TestResult().setUuid(uuid).setName(testName).setFullName(testName)
        .setLabels(labels).setStatusDetails(new StatusDetails().setFlaky(false).setMuted(false));
    getLifecycle().scheduleTestCase(parentUuid, result);
    getLifecycle().startTestCase(uuid);
  }

  private void startStep(String parentUuid, String uuid, NoCodeStep step) {
    String stepName = step.getName();
    final StepResult result = new StepResult().setName(stepName)
        .setStatusDetails(new StatusDetails().setFlaky(false).setMuted(false));
    getLifecycle().startStep(parentUuid, uuid, result);
  }

  private List<Parameter> getParams(NoCodeStep step) {
    List<Parameter> parameters = new ArrayList<>();
    for (String loc : step.getLocators()) {
      if (startStringWith(dollarBiPredicate, loc, 0)) {
        parameters.add(new Parameter().setName(loc)
            .setValue(testData.getDynamicTestData(step.getDynamicData(), loc).toString()));
      } else if (splitString(splitByDot, loc).size() >= 2 && splitString(splitByDot, loc).contains(
          "locator")) {
        parameters.add(
            new Parameter().setName(loc).setValue(locatorUtil.getLocatorFroLoadLoadLocator(loc)));
      } else if (splitString(splitByDot, loc).size() >= 2) {
        if (step.getStepData().containsKey(splitString(splitByDot, loc).get(1))) {
          parameters.add(new Parameter().setName(loc).setValue(
              step.getStepData().get(splitString(splitByDot, loc).get(1))));
        } else {
          parameters.add(new Parameter().setName(loc)
              .setValue(testData.getDefaultTestData(splitString(splitByDot, loc).get(1))));
        }
      }
    }
    return parameters;
  }

  private Consumer<StepResult> setStepStatus(final Status status, NoCodeStep step) {
    return result -> result.setStatus(status).setParameters(getParams(step));
  }

  private Consumer<StepResult> setStepStatus(final Status status, final StatusDetails statusDetails,
      NoCodeStep step) {
    return result -> result.setStatus(status).setStatusDetails(statusDetails)
        .setParameters(getParams(step));
  }

  private Consumer<StepResult> setStepStatus(final Status status,
      final StatusDetails statusDetails) {
    return result -> result.setStatus(status).setStatusDetails(statusDetails);
  }

  private Consumer<TestResult> setStatus(final Status status) {
    return result -> result.setStatus(status);
  }

  private Consumer<TestResult> setStatus(final Status status, final StatusDetails statusDetails) {
    return result -> result.setStatus(status).setStatusDetails(statusDetails);
  }

  private StatusDetails getStatusDetail(final Throwable throwable) {
    return getStatusDetails(throwable).orElse(new StatusDetails());
  }

  private void attachScreenshot() {
    String strategy = ReportConfig.getConfigProperty(Properties.REPORT_SCREENSHOT_STORAGE);
    if (strategy.equalsIgnoreCase("embed")) {
      for (Pair<String, String> screenshot : ScreenshotContext.getAllScreenshot()) {
        lifecycle.addAttachment("Screen Shot", "image/png", "",
            new ByteArrayInputStream(fileToBase64(screenshot.getLeft())));
      }
    } else {
      for (Pair<String, String> screenshot : ScreenshotContext.getAllScreenshot()) {
        File file = new File(
            ReportConfig.getConfigProperty(Properties.REPORT_FOLDER) + File.separator
                + screenshot.getLeft());
        try {
          lifecycle.addAttachment("Screen Shot", "image/png", "", FileUtils.openInputStream(file));
        } catch (IOException e) {
          throw new ZinfiTechException(e);
        }
      }
    }
    ScreenshotContext.clear();
  }

  private byte[] fileToBase64(String filePath) {
    File file = new File(
        ReportConfig.getConfigProperty(Properties.REPORT_FOLDER) + File.separator + filePath);
    try {
      return Files.readAllBytes(file.toPath());
    } catch (IOException e) {
      logger.severe("unable attach screen shot: " + e.getMessage());
    }
    return " ".getBytes();
  }
}
