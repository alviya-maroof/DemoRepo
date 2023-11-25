package com.zinfitech.context;

import com.zinfitech.zinfilistners.EventHandler;
import com.zinfitech.zinfilistners.Status;
import com.zinfitech.zinfilistners.TestResult;
import com.zinfitech.zinfiexception.ZinfiTechException;
import java.util.Objects;

public class ExecuteRunner extends EventHandler {

  public ExecuteRunner(Class<?> listenersClassBuilder) {
    super(listenersClassBuilder);
  }

  public TestResult getTestResult(Throwable throwable) {
    if (Objects.isNull(throwable)) {
      return TestResult.builder().status(Status.PASS).throwable(null).build();
    } else {
      return TestResult.builder().status(getStatus(throwable.getCause()))
          .throwable(throwable.getCause())
          .build();
    }
  }

  private Status getStatus(Throwable throwable) {
    if (throwable instanceof ZinfiTechException) {
      return Status.BROKEN;
    } else if (throwable instanceof AssertionError) {
      return Status.FAIL;
    }
    return Status.FAIL;
  }
}
