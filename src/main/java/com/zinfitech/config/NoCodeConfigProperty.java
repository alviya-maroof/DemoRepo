package com.zinfitech.config;

import com.qapitol.sauron.logging.SauronLogger;
import com.qapitol.sauron.logging.SimpleLogger;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.lang3.StringUtils;

public class NoCodeConfigProperty {

  private static final SimpleLogger logger = SauronLogger.getLogger();
  private static volatile BaseConfiguration config;// NOSONAR
  private static final String GOOGLE_SHEET_CONFIG_FILE_NAME = "google_sheet-";

  private NoCodeConfigProperty() {
    // Utility class. So hide the constructor
  }

  /**
   * Enum which contain the Sauron logger properties. To be used with {@link NoCodeConfigProperty}.
   */
  public enum Properties {
    NO_CODE_TYPE("nocode.type", "csv"),
    PACKAGE_NAME("zinfi.page.package", "com.zinfitech.pages"),
    ENV("env", "ft47"),
    TAG("tag", "any"),
    NO_CODE_TEST_SHEET_NAME("noCode.testsheet.name", ""),
    NO_CODE_SHEET_LOCATORS_MASTER_NAME("noCode.locators.sheet.name", ""),
    NO_CODE_DEFAULT_TEST_DATA_SHEET_NAME("node.default.sheet.name", ""),
    TESTCASE_SHEET_TESTCASE("testcase.sheet.name", "TestCases"),
    STEP_SHEET_NAME("step.sheet.name", "Steps"),
    LOCATOR_SHEET_NAME("locator.sheet.name", "Locators"),
    TESTDATA_SHEET_NAME("testdata.sheet.name", "TestData"),
    DEFAULT_TESTDATA_SHEET_NAME("default.testdata.sheet.name", "Default"),
    REPORT_NAME("report.name", "TestReport-"),
    REPORT_FOLDER("report.folder", "sauronFiles"),
    SCREENSHOT_MODE("screenshot.mode", "all"),
    PRIORITY("priority", ""),
    MODULE_NAME("moduleName", "");

    private final String propertyName;
    private final String defaultValue;

    Properties(String configName, String defaultValue) {
      this.propertyName = configName;
      this.defaultValue = defaultValue;
    }

    public String getName() {
      return this.propertyName;
    }

    public String getDefaultValue() {
      return this.defaultValue;
    }
  }

  private static BaseConfiguration getConfig() {
    if (config != null) {
      return config;
    }
    initConfig();
    loadProperties();
    loadFromSystemProperty();
    return config;
  }

  private static synchronized void initConfig() {
    config = new BaseConfiguration();

    // don't auto throw on missing property
    config.setThrowExceptionOnMissing(false);
    /*
     * Setup the defaults
     */
    for (Properties prop : Properties.values()) {
      config.setProperty(prop.getName(), prop.getDefaultValue());
    }
  }

  public static String getConfigProperty(Properties property) {
    return NoCodeConfigProperty.getConfig().getString(property.getName());
  }

  public static boolean getBooleanConfigProperty(Properties property) {
    return Boolean.parseBoolean(NoCodeConfigProperty.getConfig().getString(property.getName()));
  }

  private static void loadProperties() {
    String configFileName =
        Objects.nonNull(System.getenv("env")) ? GOOGLE_SHEET_CONFIG_FILE_NAME
            + System.getenv("env") + ".properties"
            : GOOGLE_SHEET_CONFIG_FILE_NAME + Properties.valueOf(
                Properties.ENV.toString()).defaultValue + ".properties";
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    Optional<InputStream> propertyInputStream = Optional.ofNullable(
        loader.getResourceAsStream(configFileName));
    if (propertyInputStream.isPresent()) {
      java.util.Properties reportConfigProperty = new java.util.Properties();
      try {
        reportConfigProperty.load(propertyInputStream.get());
        for (Properties prop : Properties.values()) {
          if (reportConfigProperty.containsKey(prop.getName()) && StringUtils.isNotEmpty(
              reportConfigProperty.getProperty(prop.getName()))) {
            config.setProperty(prop.getName(), reportConfigProperty.getProperty(prop.getName()));
          }
        }
      } catch (IOException e) {
        logger.info("Unable to find report config or invalid resource property");
      }
    }
  }

  private static void loadFromSystemProperty() {
    for (NoCodeConfigProperty.Properties prop : NoCodeConfigProperty.Properties.values()) {
      if (Objects.nonNull(System.getProperty(prop.getName())) && StringUtils.isNotEmpty(
          System.getProperty(prop.getName()))) {
        config.setProperty(prop.getName(), System.getProperty(prop.getName()));
      }
      if (Objects.nonNull(System.getenv(prop.getName())) && StringUtils.isNotEmpty(
          System.getenv(prop.getName()))) {
        config.setProperty(prop.getName(), System.getenv(prop.getName()));
      }
    }
  }

  public static String getScreenShotPropertyValue() {
    return NoCodeConfigProperty.getConfigProperty(Properties.SCREENSHOT_MODE);
  }
}
