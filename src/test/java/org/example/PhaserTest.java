package org.example;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

public class PhaserTest {
    @Test
    void PhaserWaitsForNArrivals() throws InterruptedException {
        AtomicBoolean marker = new AtomicBoolean(false);
        Phaser phaser = new Phaser(3);
        Runnable task = () -> {
            try {
                phaser.arriveAndAwaitAdvance();
                marker.set(true);
            } catch (InterruptedException e) {
                fail("Exception thrown in the test body");
            }
        };

        new Thread(task).start();
        sleep(5);
        assertFalse(marker.get());

        new Thread(task).start();
        sleep(5);
        assertFalse(marker.get());

        new Thread(task).start();
        sleep(5);
        assertTrue(marker.get());
    }

    @Test
    void PhaserRegisterTest() throws InterruptedException {
        AtomicBoolean marker = new AtomicBoolean(false);
        Phaser phaser = new Phaser();

        phaser.register();
        phaser.register();

        Runnable task = () -> {
            try {
                phaser.arriveAndAwaitAdvance();
                marker.set(true);
            } catch (InterruptedException e) {
                fail("Exception thrown in the test body");
            }
        };

        new Thread(task).start();
        sleep(5);
        assertFalse(marker.get());

        new Thread(task).start();
        sleep(5);
        assertTrue(marker.get());
    }
}