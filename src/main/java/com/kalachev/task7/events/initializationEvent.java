package com.kalachev.task7.events;

import org.springframework.context.ApplicationEvent;

public class initializationEvent extends ApplicationEvent {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public initializationEvent(Object source) {
    super(source);
  }

}
