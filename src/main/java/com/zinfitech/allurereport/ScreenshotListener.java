package com.zinfitech.allurereport;

import com.qapitol.sauron.common.sessions.AbstractTestSession;
import com.qapitol.sauron.core.Grid;
import com.qapitol.sauron.report.core.ScreenshotContext;
import com.qapitol.sauron.report.core.config.ReportConfig;
import com.qapitol.sauron.report.core.config.ReportConfig.Properties;
import com.qapitol.sauron.report.core.config.ScreenshotMode;
import com.zinfitech.config.NoCodeConfigProperty;
import com.zinfitech.pojo.NoCodeStep;
import com.zinfitech.pojo.NoCodeTest;
import com.zinfitech.zinfilistners.Status;
import com.zinfitech.zinfilistners.ZinfiTechListener;
import com.zinfitech.zinfiexception.ZinfiTechException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;

public class ScreenshotListener implements ZinfiTechListener {

  @Override
  public int getPriority() {
    return 1;
  }

  private void takeScreenshot(String title) {
    String reportFolder = ReportConfig.getConfigProperty(Properties.REPORT_FOLDER);
    Optional<String> currentSessionName = Optional.ofNullable(Grid.getSessionName());
    for (AbstractTestSession session : Grid.getAllTestSessions().values()) {
      if (session.isStarted()) { // skipping if session is not used
        Grid.setSessionName(session.getSessionName());
        String screenshot =
            "screenshots" + File.separator + "screenshot_" + System.currentTimeMillis() + ".png";
        try {
          FileUtils.copyFile(Grid.driver().getScreenshotAs(OutputType.FILE),
              new File(reportFolder + File.separator + screenshot));
        } catch (IOException e) {
          throw new ZinfiTechException(e);
        }
        ScreenshotContext.addScreenshot(screenshot, Grid.getSessionName() + " " + title);

      }
    }
    currentSessionName.ifPresent(Grid::setSessionName);
  }

  @Override
  public void onStartSuite(List<NoCodeTest> testCases) {
    //NO SONAR
  }

  @Override
  public void onFinishSuite(List<NoCodeTest> testCases) {
    //NO SONAR
  }

  @Override
  public void onStartTestStart(NoCodeTest testCase) {
    //NO SONAR
  }

  @Override
  public void onTestFinish(NoCodeTest testCase) {
    String title = "screen Shot";
    ScreenshotMode screenshotMode =
        NoCodeConfigProperty.getScreenShotPropertyValue().equalsIgnoreCase(Status.FAIL.toString())
            ? ScreenshotMode.FAILED
            : ScreenshotMode.ALL;
    switch (screenshotMode) {
      case FAILED:
        if (
            testCase.getTestResult().getStatus() == Status.FAIL) {
          takeScreenshot(title);
        }
        break;
      case ALL:
        takeScreenshot(title);
        break;
      default:
    }
  }

  @Override
  public void onStepStart(NoCodeStep step) {
    // NO SONAR
  }

  @Override
  public void onStepFinish(NoCodeStep step) {
    String title = "screen Shot";
    ScreenshotMode screenshotMode =
        NoCodeConfigProperty.getScreenShotPropertyValue().equalsIgnoreCase(Status.FAIL.toString())
            ? ScreenshotMode.FAILED
            : ScreenshotMode.ALL;
    switch (screenshotMode) {
      case FAILED:
        if (step.getResult().getStatus() == Status.FAIL) {
          takeScreenshot(title);
        }
        break;
      case ALL:
        takeScreenshot(title);
        break;
      default:
    }
  }
}
