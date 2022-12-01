package org.example;

public class ReentrantLock {
    private boolean locked = false;
    private Thread lockedBy = null;
    private int lockCount = 0;
    public synchronized void lock() throws InterruptedException {
        while(locked && lockedBy != Thread.currentThread()) {
            wait();
        }

        locked = true;
        lockedBy = Thread.currentThread();
        ++lockCount;
    }

    public synchronized void unlock() {
        if(lockedBy == Thread.currentThread()) {
            if(lockCount != 0) {
                --lockCount;
            } else {
                throw new IllegalStateException("Can't unlock thread that isn't locked");
            }
        }

        if(lockCount == 0) {
            locked = false;
            notify();
        }
    }
}