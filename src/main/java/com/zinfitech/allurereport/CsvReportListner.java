package com.zinfitech.allurereport;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.csv.CsvSchema.Builder;
import com.qapitol.sauron.logging.SauronLogger;
import com.qapitol.sauron.logging.SimpleLogger;
import com.qapitol.sauron.report.core.ScreenshotContext;
import com.zinfitech.config.NoCodeConfigProperty;
import com.zinfitech.config.NoCodeConfigProperty.Properties;
import com.zinfitech.data.ZinfiTechDataFactory;
import com.zinfitech.model.LocatorModel;
import com.zinfitech.model.NoCodeTestModel;
import com.zinfitech.model.TestDataModel;
import com.zinfitech.pojo.NoCodeStep;
import com.zinfitech.pojo.NoCodeTest;
import com.zinfitech.pojo.ZiniFunctionalInterface;
import com.zinfitech.zinfiexception.ZinfiTechException;
import com.zinfitech.zinfilistners.ZinfiTechListener;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.tuple.Pair;

public class CsvReportListner implements ZinfiTechListener, ZiniFunctionalInterface {

  private static SimpleLogger logger = SauronLogger.getLogger();

  private static final List<CsvReportModel> csvReportModels = new ArrayList<>();
  private final ThreadLocal<List<CsvReportModel>> steps = new ThreadLocal<>();
  private final ThreadLocal<Pair<String, String>> stepTime = new ThreadLocal<>();

  private final ThreadLocal<Pair<String, String>> suiteStartTime = new ThreadLocal<>();
  private final ThreadLocal<List<String>> tags = new ThreadLocal<>();

  private final TestDataModel testData = NoCodeTestModel.getModelService(
      ZinfiTechDataFactory.getDataReader()).getTestDataModel();


  private final LocatorModel locatorUtil = NoCodeTestModel.getModelService(
      ZinfiTechDataFactory.getDataReader()).getLocatorModel();
  private static final String SUITE_TIME_PATTERN = "dd-MMMM-yyyy HH_mm";
  private static final String STEP_TIME_PATTERN = "dd-MM-yyyy HH:mm:ss";

  @Override
  public int getPriority() {
    return 2;
  }

  @Override
  public void onStartSuite(List<NoCodeTest> testCases) {
    suiteStartTime.set(getTime(SUITE_TIME_PATTERN));
  }

  @Override
  public void onFinishSuite(List<NoCodeTest> testCases) {
    if (csvReportModels.isEmpty()) {
      logger.info("test cases executions status not found");
      return;
    }
    JsonNode jsonNode = getJsonNode(csvReportModels);
    Builder csvSchemaBuilder = CsvSchema.builder();
    CsvSchema csvSchema = getHeaders(jsonNode, csvSchemaBuilder).build().withHeader();
    CsvMapper csvMapper = new CsvMapper();
    try {
      csvMapper.writerFor(JsonNode.class)
          .with(csvSchema)
          .writeValue(new File(getCsvReportName()), jsonNode);
      suiteStartTime.remove();
    } catch (IOException e) {
      throw new ZinfiTechException("unable to generate csv report " + e);
    }
  }

  private String getCsvReportName() {
    return NoCodeConfigProperty.getConfigProperty(Properties.REPORT_FOLDER) + File.separator
        + NoCodeConfigProperty.getConfigProperty(
        Properties.REPORT_NAME) + NoCodeConfigProperty.getConfigProperty(Properties.ENV) + "-"
        + suiteStartTime.get().getLeft() + "-"
        + suiteStartTime.get().getRight() + ".csv";
  }

  private JsonNode getJsonNode(List<CsvReportModel> models) {
    return new ObjectMapper().valueToTree(models);
  }

  private Builder getHeaders(JsonNode jsonNode, Builder csvBuilder) {
    JsonNode firstObject = jsonNode.elements().next();
    firstObject.fieldNames().forEachRemaining(csvBuilder::addColumn);
    return csvBuilder;
  }

  @Override
  public void onStartTestStart(NoCodeTest testCase) {
    steps.set(new ArrayList<>());
    tags.set(testCase.getTags());
  }

  @Override
  public void onTestFinish(NoCodeTest testCase) {
    CsvReportModel firstObject = steps.get().remove(0);
    firstObject.setTestCaseId(testCase.getTestCaseId());
    firstObject.setTestCaseStatus(testCase.getTestResult().getStatus());
    firstObject.setTestCaseName(testCase.getName());
    csvReportModels.add(firstObject);
    csvReportModels.addAll(steps.get());
    steps.remove();
    tags.remove();
  }

  @Override
  public void onStepStart(NoCodeStep step) {
    stepTime.set(getTime(STEP_TIME_PATTERN));
  }

  @Override
  public void onStepFinish(NoCodeStep step) {
    steps.get().add(CsvReportModel.builder()
        .stepName(step.getName()).event(step.getActionName())
        .stepStatus(step.getResult().getStatus())
        .tag(tags(tags.get()))
        .date(stepTime.get().getLeft()).time(stepTime.get().getRight())
        .testData(getParams(step))
        .error(Objects.isNull(step.getResult().getThrowable()) ? ""
            : step.getResult().getThrowable().toString()).screenShot(getScreenshots())
        .build());
    stepTime.remove();
  }

  private String getScreenshots() {
    StringBuilder builder = new StringBuilder();
    for (Pair<String, String> pair : ScreenshotContext.getAllScreenshot()) {
      builder.append(pair.getLeft()).append(" ");
    }
    return builder.toString();
  }

  private Pair<String, String> getTime(String pattern) {
    DateTimeFormatter simpleDateFormat = DateTimeFormatter.ofPattern(pattern);
    List<String> dateAndTime = List.of(simpleDateFormat.format(LocalDateTime.now()).split(" "));
    return Pair.of(dateAndTime.get(0), dateAndTime.get(1));
  }

  private String tags(List<String> tags) {
    StringBuilder stringBuilder = new StringBuilder();
    for (String tag : tags) {
      stringBuilder.append(tag).append(" ");
    }
    return stringBuilder.toString();
  }

  private String getParams(NoCodeStep step) {
    if (Objects.nonNull(step.getResult().getThrowable()) && step.getResult()
        .getThrowable() instanceof ZinfiTechException) {
      return " ";
    }
    StringBuilder stringBuilder = new StringBuilder();
    for (String loc : step.getLocators()) {
      if (startStringWith(dollarBiPredicate, loc, 0)) {
        stringBuilder.append(loc).append(":")
            .append(testData.getDynamicTestData(step.getDynamicData(), loc).toString())
            .append("\n");
      } else if (splitString(splitByDot, loc).size() >= 2 && splitString(splitByDot, loc).contains(
          "locator")) {
        stringBuilder.append(loc).append(":")
            .append(locatorUtil.getLocatorFroLoadLoadLocator(loc)).append("\n");
      } else if (splitString(splitByDot, loc).size() >= 2) {
        if (step.getStepData().containsKey(splitString(splitByDot, loc).get(1))) {
          stringBuilder.append(loc).append(":")
              .append(step.getStepData().get(splitString(splitByDot, loc).get(1)));
        } else {
          stringBuilder.append(loc).append(":")
              .append(testData.getDefaultTestData(splitString(splitByDot, loc).get(1)));
        }
      }
    }
    return stringBuilder.toString();
  }
}
