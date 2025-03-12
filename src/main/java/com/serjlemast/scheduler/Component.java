package com.serjlemast.scheduler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class Component {

  /**
   * Utility function to sleep for the specified amount of milliseconds. An {@link
   * InterruptedException} will be catched and ignored while setting the interrupt flag again.
   *
   * @param milliseconds Time in milliseconds to sleep
   */
  protected void sleep(long milliseconds) {
    try {
      Thread.sleep(milliseconds);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  /**
   * Utility function to trigger a simple event handler. If the handler is currently null it gets
   * silently ignored.
   *
   * @param handler Event handler to call or null
   */
  protected void triggerSimpleEvent(SimpleEventHandler handler) {
    if (handler != null) {
      handler.handle();
    }
  }

  /**
   * Generic functional interface for simple event handlers, which are event handlers without a
   * parameter.
   */
  @FunctionalInterface
  public interface SimpleEventHandler {
    /** Handles a specific simple event based on implementation needs. */
    void handle();
  }
}
