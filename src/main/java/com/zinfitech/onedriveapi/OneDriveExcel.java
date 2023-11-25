package com.zinfitech.onedriveapi;

import com.microsoft.graph.options.HeaderOption;
import com.zinfitech.config.NoCodeConfigProperty;
import com.zinfitech.config.NoCodeConfigProperty.Properties;
import com.zinfitech.data.Excel;
import com.zinfitech.data.NoCodeDataReader;
import com.zinfitech.headers.Headers.DeFaultTestData;
import com.zinfitech.headers.Headers.Locators;
import com.zinfitech.headers.Headers.TestData;
import com.zinfitech.headers.StepHeader;
import com.zinfitech.pojo.TestCase;
import com.zinfitech.headers.TestCaseHeader;
import com.zinfitech.pojo.TestStep;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class OneDriveExcel implements NoCodeDataReader {

  private SharePointApi sharePointApi = null;
  private final Excel excel = new Excel();
  private final List<HeaderOption> options = List.of(new HeaderOption("Accept",
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

  public OneDriveExcel() {
    if (Objects.isNull(sharePointApi)) {
      this.sharePointApi = new SharePointApi();
    }
  }

  @Override
  public List<TestCase> getNoCodeTest() {
    List<List<Object>> testData = excel.readExcelData(sharePointApi.getDriveItem(
            NoCodeConfigProperty.getConfigProperty(Properties.NO_CODE_TEST_SHEET_NAME), options),
        NoCodeConfigProperty.getConfigProperty(Properties.TESTCASE_SHEET_TESTCASE),
        TestCaseHeader.values().length);
    return excel.getNoCodeTest(testData);
  }

  @Override
  public List<TestStep> getNoCodeStep() {
    List<List<Object>> testData = excel.readExcelData(sharePointApi.getDriveItem(
            NoCodeConfigProperty.getConfigProperty(Properties.NO_CODE_TEST_SHEET_NAME), options),
        NoCodeConfigProperty.getConfigProperty(Properties.STEP_SHEET_NAME),
        StepHeader.values().length);
    return excel.getNoCodeStep(testData);
  }

  @Override
  public Map<String, List<Map<String, String>>> getTestData() {
    List<List<Object>> testData = excel.readExcelData(sharePointApi.getDriveItem(
            NoCodeConfigProperty.getConfigProperty(Properties.NO_CODE_TEST_SHEET_NAME), options),
        NoCodeConfigProperty.getConfigProperty(Properties.TESTDATA_SHEET_NAME),
        TestData.values().length);
    return excel.getTestData(testData);
  }

  @Override
  public Map<String, String> getDefaultTestData() {
    List<List<Object>> testData = excel.readExcelData(sharePointApi.getDriveItem(
            NoCodeConfigProperty.getConfigProperty(Properties.NO_CODE_DEFAULT_TEST_DATA_SHEET_NAME),
            options), NoCodeConfigProperty.getConfigProperty(Properties.DEFAULT_TESTDATA_SHEET_NAME),
        DeFaultTestData.values().length);
    return excel.getDefaultTestData(testData);
  }

  @Override
  public List<Map<String, String>> getLocators() {
    List<List<Object>> testData = excel.readExcelData(sharePointApi.getDriveItem(
            NoCodeConfigProperty.getConfigProperty(Properties.NO_CODE_SHEET_LOCATORS_MASTER_NAME),
            options), NoCodeConfigProperty.getConfigProperty(Properties.LOCATOR_SHEET_NAME),
        Locators.values().length);
    return excel.getLocators(testData);
  }

  @Override
  public String getSheetName(String sheetName, String type) {
    return sheetName;
  }
}
