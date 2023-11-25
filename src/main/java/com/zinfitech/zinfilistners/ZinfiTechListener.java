package com.zinfitech.zinfilistners;

import com.zinfitech.pojo.NoCodeStep;
import com.zinfitech.pojo.NoCodeTest;
import java.util.List;

public interface ZinfiTechListener extends Comparable<ZinfiTechListener> {


  default int getPriority() {
    return 1;
  }

  public default int compareTo(ZinfiTechListener listener) {
    return (this.getPriority() - listener.getPriority());
  }

  public void onStartSuite(List<NoCodeTest> testCases);

  public void onFinishSuite(List<NoCodeTest> testCases);

  public void onStartTestStart(NoCodeTest testCase);

  public void onTestFinish(NoCodeTest testCase);

  public void onStepStart(NoCodeStep step);

  public void onStepFinish(NoCodeStep step);

}
