package com.zinfitech.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TestStepHandler implements InvocationHandler {

  @Override
  public Object invoke(Object object, Method method, Object[] args)
      throws InvocationTargetException, IllegalAccessException {
    return method.invoke(object, args);
  }
}
