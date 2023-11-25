package com.zinfitech.allurereport;

import com.qapitol.sauron.logging.SauronLogger;
import com.qapitol.sauron.logging.SimpleLogger;
import com.qapitol.sauron.report.core.config.ReportConfig;
import com.qapitol.sauron.report.core.config.ReportConfig.Properties;
import com.qapitol.sauron.report.core.utils.SystemInfo;
import com.zinfitech.config.NoCodeConfigProperty;
import com.zinfitech.data.NoCodeDataReader;
import com.zinfitech.data.ZinfiTechDataFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ZinfiSystemInfo {

  private static final SimpleLogger logger = SauronLogger.getLogger();
  private static final NoCodeDataReader noCodeDataReader = ZinfiTechDataFactory.getDataReader();

  private ZinfiSystemInfo() {
  }

  public static Map<String, String> attachSystemInfo() {
    Map<String, String> map = new HashMap<>();
    Map<Object, String> systemProperties = getSystemProperties();
    map.put("os.name", systemProperties.get("os.name"));
    map.put("os.arch", systemProperties.get("os.arch"));
    map.put("java.version", systemProperties.get("java.version"));
    map.put("java.vendor", systemProperties.get("java.vendor"));
    map.put("env", Objects.nonNull(System.getenv("env")) ? System.getenv("env") : "uat");
    map.put("TestCase Sheet", noCodeDataReader.getSheetName(NoCodeConfigProperty.getConfigProperty(
        NoCodeConfigProperty.Properties.NO_CODE_TEST_SHEET_NAME), "title"));
    map.put("module", NoCodeConfigProperty.getConfigProperty(
        NoCodeConfigProperty.Properties.TAG));
    return map;
  }

  private static Map<Object, String> getSystemProperties() {
    Map<Object, String> map = new HashMap<>();
    try {
      java.util.Properties properties = System.getProperties();
      Set<Object> sysPropertiesKeys = properties.keySet();
      for (Object key : sysPropertiesKeys) {
        map.put(key, properties.getProperty((String) key));
      }
    } catch (Exception e) {
      e.printStackTrace();
      logger.severe("unable to fetch system info: " + e.getMessage());
    }
    return map;
  }

  public static void attachAllureSystemInfo() {
    Path path = Paths.get("", ReportConfig.getConfigProperty(Properties.REPORT_ALLURE_RESULT));
    java.util.Properties properties = new java.util.Properties();
    properties.putAll(attachSystemInfo());
    if (path.toFile().exists()) {
      try (FileOutputStream fileOutputStream = new FileOutputStream(
          path + File.separator + ReportConfig.getConfigProperty(
              Properties.REPORT_ALLURE_ENVIRONMENT))) {
        properties.store(fileOutputStream, "");
      } catch (IOException e) {
        logger.severe("unable fetch the system properties: "
            + e.getMessage());
      }
    }
  }

  public static void attachCategory() {
    try (InputStream is = SystemInfo.class.getClassLoader()
        .getResourceAsStream(ReportConfig.getConfigProperty(
            Properties.REPORT_ALLURE_CATEGORIES))) {
      try (FileOutputStream fileOutputStream = new FileOutputStream(
          Paths.get("", ReportConfig.getConfigProperty(Properties.REPORT_ALLURE_RESULT))
              + File.separator + ReportConfig.getConfigProperty(
              Properties.REPORT_ALLURE_CATEGORIES))) {
        fileOutputStream.write(is.readAllBytes());
      }
    } catch (IOException e) {
      logger.severe("unable fetch the category json file to allure result directory: "
          + e.getMessage());
    }
  }
}
