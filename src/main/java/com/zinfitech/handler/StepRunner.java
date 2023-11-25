package com.zinfitech.handler;

import com.zinfitech.context.StepContext;
import com.zinfitech.pojo.NoCodeStep;
import java.lang.reflect.InvocationHandler;

public class StepRunner {

  private static final InvocationHandler invocationHandler = new TestStepHandler();

  public void run(StepContext abstractStepContexts) throws Throwable {
    if (abstractStepContexts.getArgs().length == 0) {
      invocationHandler.invoke(abstractStepContexts.getObject(),
          abstractStepContexts.getMethod(), null);
    } else {
      if (abstractStepContexts.getArgs().length == 1) {
        invocationHandler.invoke(abstractStepContexts.getObject(),
            abstractStepContexts.getMethod(),
            new NoCodeStep[]{abstractStepContexts.getTestStep()});
      }
    }
  }
}
