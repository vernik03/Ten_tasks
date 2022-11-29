package org.example;

public class Phaser {
    private int registered = 0;
    private int arrived = 0;

    public Phaser(int registered) {
        this.registered = registered;
    }

    public Phaser() {
        this(0);
    }

    public synchronized void register() {
        ++registered;
    }

    public void arriveAndDeregister() throws InterruptedException {
        boolean exited = false;
        synchronized (this) {
            --registered;
            if(arrived == registered) {
                arrived = 0;
                exited = true;
                notifyAll();
            }
        }

        if(!exited) {
            wait();
        }
    }

    public void arriveAndAwaitAdvance() throws InterruptedException {
        boolean exited = false;
        synchronized (this) {
            ++arrived;
            if(arrived == registered) {
                arrived = 0;
                exited = true;
                notifyAll();
            }
        }

        if(!exited) {
            synchronized (this) {
                wait();
            }
        }
    }
}