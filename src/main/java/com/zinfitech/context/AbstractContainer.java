package com.zinfitech.context;

import com.zinfitech.zinfiexception.ZinfiTechException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractContainer {

  private final Map<Class<?>, Object> beans = new ConcurrentHashMap<>();
  private final Map<String, Method> methodBeans = new ConcurrentHashMap<>();

  public final String packageName;

  AbstractContainer(String packageName) {
    this.packageName = packageName;
    this.loadContainer();
  }

  abstract void loadContainer();

  public Method getMethod(String methodName) {
    if (!methodBeans.containsKey(methodName)) {
      throw new ZinfiTechException(methodName + " step not found, update correct step");
    }
    return methodBeans.get(methodName);
  }

  public Class<?> getClassLoader(Method method) {
    if (!methodBeans.containsValue(method)) {
      throw new ZinfiTechException(method.getName() + " method not found or method not loaded");
    }
    if (!beans.containsKey(method.getDeclaringClass())) {
      throw new ZinfiTechException(
          method.getName() + " method class name not found or class not loaded");
    }
    return method.getDeclaringClass();
  }

  public Object getClassObject(Class<?> className) {
    if (!beans.containsKey(className)) {
      throw new ZinfiTechException(className + " class object not found or incorrect class name");
    }
    return beans.get(className);
  }

  public void updateContainer(Class<?> className) {
    this.createInstanceOfBean(className);
    for (Method method : className.getDeclaredMethods()) {
      methodBeans.put(method.getName(), method);
    }
  }

  private void createInstanceOfBean(Class<?> className) {
    try {
      beans.put(className, className.getConstructor().newInstance());
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
             NoSuchMethodException e) {
      throw new ZinfiTechException("unable to create instance of " + className.getName() + "\n", e);
    }
  }
}
