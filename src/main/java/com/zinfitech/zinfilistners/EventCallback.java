package com.zinfitech.zinfilistners;

import com.zinfitech.pojo.ZiniFunctionalInterface;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class EventCallback implements ListnerRunner, ZiniFunctionalInterface {

  AnnotationsBuilder annotationsBuilder;
  ThreadLocal<List<ZinfiTechListener>> listThreadLocal = ThreadLocal.withInitial(
      () -> annotationsBuilder.build());

  private static EventCallback eventCallback;

  public EventCallback(AnnotationsBuilder annotationsBuilder) {
    this.annotationsBuilder = annotationsBuilder;
  }

  public static EventCallback getInstance(AnnotationsBuilder builder) {
    if (Objects.isNull(eventCallback)) {
      eventCallback = new EventCallback(builder);
    }
    return eventCallback;
  }

  @Override
  public void execute(Consumer<ZinfiTechListener> zinfiTechListenerConsumer) {
    if (!listThreadLocal.get().isEmpty()) {
      Collections.sort(listThreadLocal.get());
    }
    for (ZinfiTechListener zinfiTechListener : listThreadLocal.get()) {
      zinfiTechListenerConsumer.accept(zinfiTechListener);
    }
  }
}
