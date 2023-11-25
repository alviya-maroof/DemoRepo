package com.zinfitech.zinfilistners;

import com.zinfitech.annotations.Listeners;
import com.zinfitech.zinfiexception.ZinfiTechException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class AnnotationsBuilder {

  Class<?> listnerClass;

  public AnnotationsBuilder(Class<?> listnerClass) {
    this.listnerClass = listnerClass;
  }

  public List<ZinfiTechListener> build() {
    if (isAnnotationPresent() && isEnabled()) {
      Listeners zinfiListeners = listnerClass.getAnnotation(Listeners.class);
      return getListeners(List.of(zinfiListeners.listeners()));
    }
    return List.of();
  }

  private boolean isAnnotationPresent() {
    return listnerClass.isAnnotationPresent(Listeners.class);
  }

  private boolean isEnabled() {
    return listnerClass.getAnnotation(Listeners.class).enable();
  }

  private List<ZinfiTechListener> getListeners(List<String> classname) {
    ArrayList<ZinfiTechListener> listeners = new ArrayList<>();
    for (String cls : classname) {
      try {
        listeners.add(
            (ZinfiTechListener) ClassLoader.getSystemClassLoader().loadClass(cls).getConstructor()
                .newInstance());
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
               NoSuchMethodException | ClassNotFoundException e) {
        throw new ZinfiTechException("class not found", e);
      }
    }
    return listeners;
  }
}
