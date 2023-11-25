package com.zinfitech.zinfilistners;

import com.zinfitech.pojo.NoCodeStep;
import com.zinfitech.pojo.NoCodeTest;
import java.util.List;

public class EventHandler implements ZinfiTechListener {

  private final EventCallback eventCallback;

  public EventHandler(Class<?> className) {
    this.eventCallback = EventCallback.getInstance(new AnnotationsBuilder(className));
  }

  @Override
  public void onStartSuite(List<NoCodeTest> testCases) {
    eventCallback.execute(zinfiTechListener -> zinfiTechListener.onStartSuite(testCases));
  }

  @Override
  public void onFinishSuite(List<NoCodeTest> testCases) {
    eventCallback.execute(zinfiTechListener -> zinfiTechListener.onFinishSuite(testCases));
  }

  @Override
  public void onStartTestStart(NoCodeTest testCase) {
    eventCallback.execute(zinfiTechListener -> zinfiTechListener.onStartTestStart(testCase));
  }

  @Override
  public void onTestFinish(NoCodeTest testCase) {
    eventCallback.execute(zinfiTechListener -> zinfiTechListener.onTestFinish(testCase));
  }

  @Override
  public void onStepStart(NoCodeStep step) {
    eventCallback.execute(zinfiTechListener -> zinfiTechListener.onStepStart(step));
  }

  @Override
  public void onStepFinish(NoCodeStep step) {
    eventCallback.execute(zinfiTechListener -> zinfiTechListener.onStepFinish(step));
  }

}
