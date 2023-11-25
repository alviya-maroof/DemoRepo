package com.zinfitech.excelapi;

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


public class ExcelSheetDataReader implements NoCodeDataReader {

  private static final String EXCEL_SHEET_ROOT_DIRECTORY = "nocode";
  private final Excel excel = new Excel();

  @Override
  public List<TestCase> getNoCodeTest() {
    List<List<Object>> testData = excel.readExcelData(EXCEL_SHEET_ROOT_DIRECTORY,
        NoCodeConfigProperty.getConfigProperty(Properties.NO_CODE_TEST_SHEET_NAME) + "."
            + NoCodeConfigProperty.getConfigProperty(Properties.NO_CODE_TYPE),
        NoCodeConfigProperty.getConfigProperty(Properties.TESTCASE_SHEET_TESTCASE),
        TestCaseHeader.values().length);
    return excel.getNoCodeTest(testData);
  }

  @Override
  public List<TestStep> getNoCodeStep() {
    List<List<Object>> stepData = excel.readExcelData(EXCEL_SHEET_ROOT_DIRECTORY,
        NoCodeConfigProperty.getConfigProperty(Properties.NO_CODE_TEST_SHEET_NAME) + "."
            + NoCodeConfigProperty.getConfigProperty(Properties.NO_CODE_TYPE),
        NoCodeConfigProperty.getConfigProperty(Properties.STEP_SHEET_NAME),
        StepHeader.values().length);
    return excel.getNoCodeStep(stepData);
  }

  @Override
  public Map<String, List<Map<String, String>>> getTestData() {
    List<List<Object>> testData = excel.readExcelData(EXCEL_SHEET_ROOT_DIRECTORY,
        NoCodeConfigProperty.getConfigProperty(Properties.NO_CODE_TEST_SHEET_NAME) + "."
            + NoCodeConfigProperty.getConfigProperty(Properties.NO_CODE_TYPE),
        NoCodeConfigProperty.getConfigProperty(Properties.TESTDATA_SHEET_NAME),
        TestData.values().length);
    return excel.getTestData(testData);
  }


  @Override
  public Map<String, String> getDefaultTestData() {
    List<List<Object>> data = excel.readExcelData(EXCEL_SHEET_ROOT_DIRECTORY,
        NoCodeConfigProperty.getConfigProperty(Properties.NO_CODE_DEFAULT_TEST_DATA_SHEET_NAME)
            + "."
            + NoCodeConfigProperty.getConfigProperty(Properties.NO_CODE_TYPE),
        NoCodeConfigProperty.getConfigProperty(Properties.DEFAULT_TESTDATA_SHEET_NAME),
        DeFaultTestData.values().length);
    return excel.getDefaultTestData(data);
  }

  @Override
  public List<Map<String, String>> getLocators() {
    List<List<Object>> data = excel.readExcelData(EXCEL_SHEET_ROOT_DIRECTORY,
        NoCodeConfigProperty.getConfigProperty(Properties.NO_CODE_SHEET_LOCATORS_MASTER_NAME) + "."
            + NoCodeConfigProperty.getConfigProperty(Properties.NO_CODE_TYPE),
        NoCodeConfigProperty.getConfigProperty(Properties.LOCATOR_SHEET_NAME),
        Locators.values().length);
    return excel.getLocators(data);
  }

  @Override
  public String getSheetName(String sheetName, String type) {
    return sheetName;
  }
}
