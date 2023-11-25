package com.zinfitech.context;

import com.zinfitech.handler.StepRunner;
import com.zinfitech.pojo.NoCodeStep;
import java.lang.reflect.Method;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StepContext extends AbstractStepContext {

  private NoCodeStep testStep;

  public StepContext(NoCodeStep testStep, AbstractContainer containerClass,
      ExecuteRunner executeRunner) {
    super(testStep, containerClass, executeRunner);
  }

  public void executeStep(StepRunner runner) throws Throwable {
    executeRunner.onStepStart(this.getTestStep());
    runner.run(this);
    testStep.setResult(executeRunner.getTestResult(null));
    executeRunner.onStepFinish(this.getTestStep());
  }

  @Override
  void saveStepContext(NoCodeStep testStep) {
    setAClass(getClass(testStep.getActionName()));
    setMethod(getMethod(testStep.getActionName()));
    setArgs(getMethodArgs(getMethod()));
    setObject(getObject(getAClass()));
    setTestStep(testStep);
  }

  private Class<?> getClass(String actionName) {
    return this.getContainer().getClassLoader(getMethod(actionName));
  }

  private Method getMethod(String actionName) {
    return this.getContainer().getMethod(actionName);
  }

  private Object[] getMethodArgs(Method method) {
    return method.getParameters();
  }

  private Object getObject(Class<?> cls) {
    return Optional.of(this.getContainer().getClassObject(cls)).get();
  }
}
