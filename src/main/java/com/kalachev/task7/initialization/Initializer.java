package com.kalachev.task7.initialization;

import com.kalachev.task7.events.InitializationEvent;

public interface Initializer {

  void initializeTablesEvent(InitializationEvent event);

}