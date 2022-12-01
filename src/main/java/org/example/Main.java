package org.example;

import static java.lang.Thread.sleep;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        nonBlockingQueueExample();
        cyclicBarrierExample();
        skipListExample();
        phaserExample();
        threadPoolExample();
        initThreadGroup();
    }

    private static void initThreadGroup(){
        ThreadGroup group = createDummyThreadGroup();
        group.list();

        ThreadInformationPrinter printer = new ThreadInformationPrinter(group, 1000);
    }

    private static ThreadGroup createDummyThreadGroup() {
        ThreadGroup thGroup1 = new ThreadGroup("Root thread group");
        ThreadGroup thGroup2 = new ThreadGroup(thGroup1,"Child thread group");
        ThreadGroup thGroup3 = new ThreadGroup(thGroup1,"Other child thread group");
        ThreadGroup thGroup4 = new ThreadGroup(thGroup3,"Yet another thread group");

        Thread someThread = new Thread(thGroup3, () -> { while(true) {} });
        someThread.start();

        Thread shortThread = new Thread(thGroup2, () -> {
            try {
                sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        shortThread.start();

        return thGroup1;
    }

    private static void cyclicBarrierExample()  throws InterruptedException {
        CyclicBarrier barrier = new CyclicBarrier(8);

        for(int i = 0; i < 10; ++i) {
            int finalI = i;
            new Thread(() -> {
                System.out.println("Thread #" + Integer.toString(finalI) + " has started.");
                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                System.out.println("Barrier for thread #" + Integer.toString(finalI) + " has been broken.");
            }).start();
        }

        sleep(1000);
    }

    private static void skipListExample() {
        LockFreeSkipList<Integer> skipList = new LockFreeSkipList<>();
        skipList.add(42);
        skipList.add(420);
        skipList.add(4200);
        System.out.println(skipList.contains(41));
        System.out.println(skipList.contains(42));
        System.out.println(skipList.contains(43));

        skipList.remove(42);
        System.out.println(skipList.contains(42));
    }

    private static void phaserExample() throws InterruptedException {
        Phaser phaser = new Phaser(4);

        while(true) {
            Thread th = new Thread(() -> {
                try {
                    System.out.println("Waiting for advance");
                    phaser.arriveAndAwaitAdvance();
                    System.out.println("Advanced");
                } catch (InterruptedException e) {
                    System.out.println("Interrupted");
                }
            });
            th.start();
            sleep(1000);
        }
    }

    private static void threadPoolExample() throws InterruptedException {
        ThreadPool th = new ThreadPool(4);

        int i = 0;
        while(true) {
            ++i;
            int finalI = i;
            th.addTask(() -> {
                System.out.println(finalI);
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("Interrupted");
                }
            });
        }
    }

    private static void nonBlockingQueueExample() {
        NonBlockingQueue<Integer> nonBlockingQueue = new NonBlockingQueue<Integer>();
        nonBlockingQueue.push(42);
        System.out.println(nonBlockingQueue.pop());
    }


}