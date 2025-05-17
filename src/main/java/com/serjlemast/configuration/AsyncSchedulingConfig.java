package com.serjlemast.configuration;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.Executor;


/**
 * 1) Requires @EnableAsync to enable asynchronous method execution
 * 2) Requires @EnableScheduling for scheduled task support
 * 3) Methods in {@link com.serjlemast.scheduler.SchedulerProcessor} can be annotated with @Async to run on a separate (virtual) thread
 */
@EnableAsync
@Configuration
@EnableScheduling
public class AsyncSchedulingConfig implements AsyncConfigurer {

  @Override
  public Executor getAsyncExecutor() {
    var executor = new SimpleAsyncTaskExecutor();
    executor.setVirtualThreads(true);
    executor.setThreadNamePrefix("a-v-thread-");
    return executor;
  }

  @Override
  public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
    return new SimpleAsyncUncaughtExceptionHandler();
  }
}