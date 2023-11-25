package com.zinfitech.data;

import com.zinfitech.config.NoCodeConfigProperty;
import com.zinfitech.config.NoCodeConfigProperty.Properties;
import com.zinfitech.excelapi.ExcelSheetDataReader;
import com.zinfitech.googleapi.GoogleSheetDataReader;
import com.zinfitech.onedriveapi.OneDriveExcel;

public class ZinfiTechDataFactory {

  private ZinfiTechDataFactory() {
  }

  public static NoCodeDataReader getDataReader() {
    switch (NoCodeConfigProperty.getConfigProperty(Properties.NO_CODE_TYPE)) {
      case "csv":
        return null;
      case "xls":
      case "xlsx":
        return new ExcelSheetDataReader();
      case "google":
        return new GoogleSheetDataReader();
      case "onedrive":
        return new OneDriveExcel();
      default:
        throw new UnsupportedOperationException("Format not supported");
    }
  }
}
