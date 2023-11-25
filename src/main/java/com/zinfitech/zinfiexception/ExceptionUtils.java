package com.zinfitech.zinfiexception;

import static java.util.Objects.requireNonNull;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtils {

  private ExceptionUtils() {
  }

  public static void throwAsUncheckedException(Throwable throwable) {
    requireNonNull(throwable, "throwable may not be null");
    throwAs(throwable);
  }

  @SuppressWarnings("unchecked")
  private static <T extends Throwable> void throwAs(Throwable t) throws T {
    throw (T) t;
  }

  public static String printStackTrace(Throwable throwable) {
    requireNonNull(throwable, "throwable may not be null");
    StringWriter stringWriter = new StringWriter();
    try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
      throwable.printStackTrace(printWriter);
    }
    return stringWriter.toString();
  }
}
