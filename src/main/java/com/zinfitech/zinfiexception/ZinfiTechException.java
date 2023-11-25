package com.zinfitech.zinfiexception;

public class ZinfiTechException extends RuntimeException {

  public ZinfiTechException(String message) {
    super(message);
  }

  public ZinfiTechException(String message, Throwable cause) {
    super(message, cause);
  }

  public ZinfiTechException(Throwable cause) {
    super(cause);
  }


}
