package com.zinfitech.context;

import com.zinfitech.handler.StepRunner;
import com.zinfitech.pojo.NoCodeTest;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractTestContext {
  private NoCodeTest testCase;
  private List<StepContext> stepContexts;
  private AbstractContainer container;
  public ExecuteRunner executeRunner;
  StepRunner stepRunner = new StepRunner();

  public abstract void saveTestContext(NoCodeTest testCase);

  public abstract void execute(AbstractTestContext testCaseContext);

  protected AbstractTestContext(AbstractContainer containerClass, NoCodeTest testCase,
      ExecuteRunner executeRunner) {
    setContainer(containerClass);
    this.executeRunner = executeRunner;
    this.saveTestContext(testCase);
  }

  @Override
  public String toString() {
    return testCase.getName();
  }
}
