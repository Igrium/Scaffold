package org.scaffoldeditor.scaffold.core;

import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.apache.logging.log4j.LogManager;

/**
 * An object that provides a thread service.
 */
public class ServiceProvider implements AutoCloseable, Executor {
	private Thread thread;
	private String threadName;
	private boolean isRunning = true;
	private Queue<Runnable> queue = new ConcurrentLinkedQueue<>();

	CountDownLatch stopLatch = new CountDownLatch(1);
	
	/**
	 * Create a new service provider.
	 * @param threadName Name to assign to the service thread.
	 */
	public ServiceProvider(String threadName) {
		this.threadName = threadName;
		thread = new Thread(this::threadLoop, getThreadName());
		thread.start();
	}

	private void threadLoop() {
		while (isRunning) {
			while (queue.size() > 0) {
				Runnable task = this.queue.remove();
				try {
					task.run();
				} catch (Throwable e) {
					except(e);
				}
			}
			if (isRunning) LockSupport.park(thread);
		}

		stopLatch.countDown();
	}

	
	/**
	 * Check if we're currently on the service thread.
	 */
	public boolean isOnThread() {
		return Thread.currentThread().equals(thread);
	}

	public boolean isRunning() {
		return isRunning;
	}
	
	/**
	 * Execute the given command on the service thread at some time in the future.
	 * If we're already on the service thread, execute it now.
	 * 
	 * @param r The runnable task.
	 * @see ExecutorService#execute(Runnable)
	 */
	public void execute(Runnable r) {
		if (r == null) throw new NullPointerException("Service provider can't execute null.");
		if (!isRunning) throw new IllegalStateException("Can't execute on stopped service provider!");

		if (isOnThread()) {
			r.run();
		} else {
			queue.add(r);
			LockSupport.unpark(thread);
		}
	}

	/**
	 * Submits a value-returning task for execution on the service thread and
	 * returns a completable future representing the pending results of the task.
	 * 
	 * If we're already on the service thread, executes it now and returns a future
	 * that has already been completed.
	 * 
	 * @param r The task to run.
	 */
	public <T> CompletableFuture<T> submit(Callable<T> r) {
		CompletableFuture<T> future = new CompletableFuture<>();
		execute(() -> {
			try {
				future.complete(r.call());
			} catch (Throwable e) {
				future.completeExceptionally(e);
			}
		});
		return future;
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

	/**
	 * Called when an uncaught exception is thrown in the executor thread.
	 * @param e The exception.
	 */
	protected void except(Throwable e) {
		LogManager.getLogger().fatal("Uncaught error in "+getThreadName(), e);
	}
	
	@Override
	public void close() {
		LogManager.getLogger().info("Shutting down executor service.");
		isRunning = false;
		try {
			boolean timeout = stopLatch.await(5, TimeUnit.SECONDS);

			if (!timeout) {
				LogManager.getLogger().error("Service provider thread failed to stop in time!");
				thread.interrupt();
			}

		} catch (InterruptedException e) {
			LogManager.getLogger().error("Error awaiting service provider shutdown.", e);
		}
	}
}
