package com.zinfitech.zinfiexception;

import com.zinfitech.context.StepContext;
import com.zinfitech.context.TestCaseContext;
import com.zinfitech.zinfilistners.Status;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Objects;

public class Logger extends PrintWriter {

  public static final String TEXT_RED = "\u001B[31m";
  public static final String TEXT_GREEN = "\u001B[32m";
  public static final String TEXT_YELLOW = "\u001B[33m";
  public static final String TEXT_WHITE = "\u001B[37m";
  public static final String ANSI_RESET = "\u001B[0m";

  private static final Logger logger = new Logger(System.out);

  public Logger(PrintStream printStream) {
    super(printStream);
  }

  public static Logger getInstance() {
    return logger;
  }

  public void updateTestStatusLog(TestCaseContext testCaseContext) {
    testCaseContext.getStepContexts().forEach(this::println);
  }

  public void println(StepContext stepContext) {
    if (Objects.isNull(stepContext.getTestStep().getResult())) {
      println(TEXT_YELLOW + stepContext.getTestStep().getName());
    } else if (stepContext.getTestStep().getResult().getStatus() == Status.PASS) {
      println(TEXT_GREEN + stepContext.getTestStep().getName());
    } else if (stepContext.getTestStep().getResult().getStatus() == Status.FAIL) {
      println(TEXT_RED + stepContext.getTestStep().getName());
    } else if (stepContext.getTestStep().getResult().getStatus() == Status.BROKEN) {
      println(TEXT_WHITE + stepContext.getTestStep().getName());
    }
    println(ANSI_RESET);
    this.flush();
  }
}
