package org.scaffoldeditor.scaffold.core;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;

/**
 * An object that provides a thread service.
 */
public class ServiceProvider implements AutoCloseable {
	private final ExecutorService executor;
	private Thread thread;
	private String threadName;
	
	/**
	 * Create a new service provider.
	 * @param threadName Name to assign to the service thread.
	 */
	public ServiceProvider(String threadName) {
		this.threadName = threadName;
		executor = Executors.newSingleThreadExecutor(runnable -> {
			thread = new Thread(runnable, getThreadName());
			return thread;
		});
	}
	
	/**
	 * Check if we're currently on the service thread.
	 */
	public boolean isOnThread() {
		return Thread.currentThread().equals(thread);
	}
	
	/**
	 * Execute the given command on the service thread at some time in the future.
	 * If we're already on the service thread, execute it now.
	 * 
	 * @param r The runnable task.
	 * @see ExecutorService#execute(Runnable)
	 */
	public void execute(Runnable r) {
		if (isOnThread()) {
			r.run();
		} else {
			executor.execute(r);
		}
	}

	/**
	 * Submits a value-returning task for execution on the service thread and returns a Future representing
	 * the pending results of the task. TheFuture's get method will return the
	 * task's result uponsuccessful completion.
	 * 
	 * If we're already on the service thread, executes it now and returns a Future that has already been completed.
	 * 
	 * @see ExecutorService#submit(Callable)
	 */
	public <T> Future<T> submit(Callable<T> r) {
		if (isOnThread()) {
			CompletableFuture<T> future = new CompletableFuture<T>();
			try {
				future.complete(r.call());
			} catch (Exception e) {
				LogManager.getLogger().error(e);
				future.completeExceptionally(e);
			}
			return future;
		} else {
			return executor.submit(r);
		}
	}
	
	/**
	 * Get the executor behind this service.
	 */
	public ExecutorService getExecutor() {
		return executor;
	}
	
	/**
	 * Get the thread behind this service.
	 */
	public Thread getThread() {
		return thread;
	}
	
	/**
	 * Get the name of the service's thread.
	 * @return Service thread name.
	 */
	public String getThreadName() {
		return threadName;
	}
	
	@Override
	public void close() {
		try {
			LogManager.getLogger().info("Shutting down executor service.");
			executor.shutdown();
			executor.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (!executor.isTerminated()) {
				LogManager.getLogger().error("Executor service failed to close in time!");
			}
			executor.shutdownNow(); 
		}
	}
}
