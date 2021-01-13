package vn.com.vndirect.report.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.DisposableBean;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:thuan.nhu@homedirect.com.vn
 * Nov 1, 2017
 */
public abstract class ExecutableService implements DisposableBean {

  protected ScheduledExecutorService scheduledExecutor;
  
  public ExecutableService(int initialDelay, int delay) {
    scheduledExecutor = Executors.newScheduledThreadPool(1);
    scheduledExecutor.scheduleWithFixedDelay(() -> execute(), initialDelay, delay, TimeUnit.SECONDS);
  }
  
  public abstract void execute();

  @Override
  public void destroy() throws Exception {
    scheduledExecutor.shutdown();
  }
}