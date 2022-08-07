package com.kalachev.task7.events;

import org.springframework.context.ApplicationEvent;

public class InitializationEvent extends ApplicationEvent {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public InitializationEvent(Object source) {
    super(source);
  }

}
