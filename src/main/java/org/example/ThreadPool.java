package org.example;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadPool {
    private final ArrayBlockingQueue<Runnable> scheduled;
    private final AtomicBoolean shouldStop;
    private final Thread[] threads;
    private class ThreadPoolWorkerThread extends Thread {
        public void run() {
            while (!shouldStop.getPlain()) {
                Runnable task = null;

                synchronized (scheduled) {
                    try {
                        task = scheduled.take();
                    } catch (InterruptedException e) {
                        System.out.println("Interrupted exception");
                    }
                }

                if(task != null) task.run();
            }
        }
    }

    public ThreadPool(int threadCount) {
        shouldStop = new AtomicBoolean(false);
        scheduled = new ArrayBlockingQueue<>(256);
        threads = new Thread[threadCount];
        for(int i = 0; i < threadCount; ++i) {
            threads[i] = new Thread(new ThreadPoolWorkerThread());
            threads[i].start();
        }
    }
    public void addTask(Runnable task) {
        scheduled.offer(task);
    }
    public void stop() {
        shouldStop.set(true);

        for(int i = 0; i < threads.length; ++i) {
            addTask(() -> {});
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.out.println("Interrupted exception");
            }
        }
    }
}