package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NonBlockingQueueTest {
    @Test
    void NonBlockingQueueCreation() {
        NonBlockingQueue<Integer> nonBlockingQueue = new NonBlockingQueue<Integer>();
        nonBlockingQueue.push(42);
        nonBlockingQueue.push(43);
        nonBlockingQueue.push(44);
    }

    @Test
    void NonBlockingQueueOrdering() {
        NonBlockingQueue<Integer> nonBlockingQueue = new NonBlockingQueue<Integer>();
        nonBlockingQueue.push(42);
        nonBlockingQueue.push(43);
        nonBlockingQueue.push(44);

        assertEquals(nonBlockingQueue.pop().intValue(), 42);
        assertEquals(nonBlockingQueue.pop().intValue(), 43);
        assertEquals(nonBlockingQueue.pop().intValue(), 44);
    }
}