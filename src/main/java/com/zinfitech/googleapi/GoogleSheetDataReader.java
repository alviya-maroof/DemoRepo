package com.zinfitech.googleapi;

import com.google.api.services.sheets.v4.Sheets;
import com.zinfitech.config.NoCodeConfigProperty;
import com.zinfitech.config.NoCodeConfigProperty.Properties;
import com.zinfitech.data.Excel;
import com.zinfitech.data.NoCodeDataReader;
import com.zinfitech.pojo.TestCase;
import com.zinfitech.pojo.TestStep;
import com.zinfitech.zinfiexception.ZinfiTechException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class GoogleSheetDataReader implements NoCodeDataReader {

  private final Sheets sheets = GoogleSheet.getInstance().getGoogleSheetInstance();
  private final Excel excel = new Excel();

  private List<List<Object>> getTestCaseData(String spreadsheetId, String range) {
    try {
      return sheets.spreadsheets().values().get(spreadsheetId, range).execute().getValues();
    } catch (IOException e) {
      throw new ZinfiTechException(e);
    }
  }

  @Override
  public List<TestCase> getNoCodeTest() {
    List<List<Object>> testData = this.getTestCaseData(
        NoCodeConfigProperty.getConfigProperty(Properties.NO_CODE_TEST_SHEET_NAME),
        NoCodeConfigProperty.getConfigProperty(Properties.TESTCASE_SHEET_TESTCASE));
    return excel.getNoCodeTest(testData);
  }

  @Override
  public List<TestStep> getNoCodeStep() {
    List<List<Object>> stepData = this.getTestCaseData(
        NoCodeConfigProperty.getConfigProperty(Properties.NO_CODE_TEST_SHEET_NAME),
        NoCodeConfigProperty.getConfigProperty(Properties.STEP_SHEET_NAME));
    return excel.getNoCodeStep(stepData);
  }

  @Override
  public Map<String, List<Map<String, String>>> getTestData() {
    List<List<Object>> data = this.getTestCaseData(
        NoCodeConfigProperty.getConfigProperty(Properties.NO_CODE_TEST_SHEET_NAME),
        NoCodeConfigProperty.getConfigProperty(Properties.TESTDATA_SHEET_NAME));
    return excel.getTestData(data);
  }

  public Map<String, String> getDefaultTestData() {
    List<List<Object>> data = this.getTestCaseData(
        NoCodeConfigProperty.getConfigProperty(Properties.NO_CODE_DEFAULT_TEST_DATA_SHEET_NAME),
        NoCodeConfigProperty.getConfigProperty(Properties.DEFAULT_TESTDATA_SHEET_NAME));
    return excel.getDefaultTestData(data);
  }

  @Override
  public List<Map<String, String>> getLocators() {
    List<List<Object>> data = this.getTestCaseData(
        NoCodeConfigProperty.getConfigProperty(Properties.NO_CODE_SHEET_LOCATORS_MASTER_NAME),
        NoCodeConfigProperty.getConfigProperty(Properties.LOCATOR_SHEET_NAME));
    return excel.getLocators(data);
  }

  @Override
  public String getSheetName(String spreadsheetId, String type) {
    try {
      Sheets.Spreadsheets.Get request = sheets.spreadsheets().get(spreadsheetId);
      return request.execute().getProperties().get(type).toString();
    } catch (IOException e) {
      throw new ZinfiTechException(e);
    }
  }
}
