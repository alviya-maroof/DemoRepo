package com.zinfitech.zinfilistners;

import java.util.function.Consumer;

public interface ListnerRunner {
  void execute(Consumer<ZinfiTechListener> zinfiTechListenerConsumer);

}
