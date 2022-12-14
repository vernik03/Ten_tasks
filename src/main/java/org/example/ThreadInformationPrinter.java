package org.example;

import static java.lang.Thread.sleep;

public class ThreadInformationPrinter {
    private final ThreadGroup threadGroup;
    private final long timeout;

    public ThreadInformationPrinter(ThreadGroup threadGroup, long timeout) {
        this.threadGroup = threadGroup;
        this.timeout = timeout;
        Thread printerThread = new Thread(this::threadInfoPrinterWorkerThread);
        printerThread.start();
    }

    private static void printThreadGroupInformation(ThreadGroup group) {
        ThreadGroup[] groups = new ThreadGroup[group.activeGroupCount() + 1];
        group.enumerate(groups);
        groups[groups.length - 1] = group;

        for(ThreadGroup threadGroup : groups) {
            System.out.println("ThreadGroup: " + threadGroup.getName() + "; "
                    + "parent group: " + threadGroup.getParent().getName() + "; "
                    + (threadGroup.isDaemon() ? "daemon; " : "not daemon; "));
        }
    }

    private static void printThreadInformation(ThreadGroup group) {
        Thread[] threads = new Thread[group.activeCount()];
        group.enumerate(threads);

        for(Thread thread : threads) {
            System.out.println("Thread: " + thread.getName() + "; "
                    + "thread group: " + thread.getThreadGroup().getName() + "; "
                    + (thread.isDaemon() ? "daemon; " : "not daemon; ")
                    + "id: " + thread.getId());
        }
    }
    private void threadInfoPrinterWorkerThread() {
        while(true) {
            System.out.println("Thread group hierarchy: ");
            printThreadGroupInformation(threadGroup);
            System.out.println();
            System.out.println("Thread information: ");
            printThreadInformation(threadGroup);
            System.out.println();
            System.out.println();

            try {
                sleep(timeout);
            } catch (InterruptedException e) {
                System.out.println("Interrupted exception");
            }
        }
    }
}