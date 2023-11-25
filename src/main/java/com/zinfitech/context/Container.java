package com.zinfitech.context;

import com.zinfitech.zinfiexception.ZinfiTechException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

public class Container extends AbstractContainer {

  public Container(String packageName) {
    super(packageName);
  }

  @Override
  void loadContainer() {
    List<Class<?>> classes = findAllClassesUsingClassLoader(this.packageName);
    for (Class<?> cls : classes) {
      updateContainer(cls);
    }
  }

  private List<Class<?>> findAllClassesUsingClassLoader(String packageName) {
    InputStream stream = ClassLoader.getSystemClassLoader()
        .getResourceAsStream(packageName.replaceAll("[.]", "/"));
    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
    return reader.lines()
        .filter(line -> line.endsWith(".class"))
        .map(line -> getClass(line, packageName))
        .collect(Collectors.toList());
  }

  private Class<?> getClass(String className, String packageName) {
    try {
      return Class.forName(packageName + "."
          + className.substring(0, className.lastIndexOf('.')));
    } catch (ClassNotFoundException e) {
      throw new ZinfiTechException("unable to load " + packageName + "." + className + " class", e);
    }
  }
}
