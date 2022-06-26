package com.kalachev.task7.events;

import org.springframework.context.ApplicationEvent;

public class initializationEvent extends ApplicationEvent {

  public initializationEvent(Object source) {
    super(source);
  }

}
