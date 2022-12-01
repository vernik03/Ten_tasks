package org.example;
public class CyclicBarrier {
    private int waitingParties = 0;
    private final int trippingParties;
    public CyclicBarrier(int trippingParties) {
        this.trippingParties = trippingParties;
    }
    public synchronized void await() throws InterruptedException {
        ++waitingParties;
        if(waitingParties != trippingParties) {
            wait();
            return;
        }
        waitingParties = 0;
        notifyAll();
    }
}