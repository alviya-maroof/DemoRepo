package com.zinfitech.context;

import com.zinfitech.pojo.NoCodeStep;
import java.lang.reflect.Method;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractStepContext {

  private Method method;
  private Class<?> aClass;
  private Object object;
  private Object[] args;
  private AbstractContainer container;
  public final ExecuteRunner executeRunner;

  protected AbstractStepContext(NoCodeStep testStep, AbstractContainer container,
      ExecuteRunner executeRunner) {
    this.executeRunner = executeRunner;
    setContainer(container);
    saveStepContext(testStep);
  }

  abstract void saveStepContext(NoCodeStep testStep);
}
