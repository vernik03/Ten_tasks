package org.example;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class ThreadPoolTest {
    @Test
    void ThreadPoolCreationTest() {
        ThreadPool th = new ThreadPool(4);
        th.stop();
    }

    @Test
    void ThreadPoolExecutesTask() {
        AtomicBoolean marker = new AtomicBoolean(false);
        ThreadPool th = new ThreadPool(4);
        th.addTask(() -> {
            marker.set(true);
        });
        th.stop();

        assertTrue(marker.get());
    }
}