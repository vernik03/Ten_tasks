package org.example;

import java.util.concurrent.atomic.AtomicMarkableReference;
public class LockFreeSkipList<T> {
    final long maxLevel;
    final node<T> head;
    final node<T> tail;
    public LockFreeSkipList(long maxLevel) {
        this.maxLevel = maxLevel;
        this.head = new node<T>(Integer.MIN_VALUE, (int)maxLevel);
        this.tail = new node<T>(Integer.MAX_VALUE, (int)maxLevel);
        for (int i = 0; i < head.next.length; i++) {
            head.next[i] = new AtomicMarkableReference<node<T>>(tail, false);
        }
    }

    public LockFreeSkipList() {
        this(1024);
    }

    public boolean add(T x) {
        int topLevel = (int)(Math.random() * maxLevel);
        node<T>[] predecessors = new node[(int)maxLevel + 1];
        node<T>[] successors = new node[(int)maxLevel + 1];
        while (true) {
            boolean found = find(x, predecessors, successors);
            if (found) {
                return false;
            } else {
                node<T> newNode = new node<T>(x, topLevel, (int)maxLevel);
                for (int level = 0; level <= topLevel; level++) {
                    node<T> successor = successors[level];
                    newNode.next[level].set(successor, false);
                }
                node<T> predecessor = predecessors[0];
                node<T> successor = successors[0];
                newNode.next[0].set(successor, false);
                if (!predecessor.next[0].compareAndSet(successor, newNode, false, false)) {
                    continue;
                }
                for (int level = 1; level <= topLevel; level++) {
                    while (true) {
                        predecessor = predecessors[level];
                        successor = successors[level];
                        if (predecessor.next[level].compareAndSet(successor, newNode, false, false))
                            break;
                        find(x, predecessors, successors);
                    }
                }
                return true;
            }
        }
    }

    public boolean remove(T x) {
        int bottomLevel = 0;
        node<T>[] predecessors = new node[(int)maxLevel + 1];
        node<T>[] succesors = new node[(int)maxLevel + 1];
        node<T> successor = null;
        while (true) {
            boolean found = find(x, predecessors, succesors);
            if (!found) {
                return false;
            } else {
                node<T> nodeToRemove = succesors[bottomLevel];
                for (int level = nodeToRemove.maxLevel;
                     level >= bottomLevel+1; level--) {
                    boolean[] marked = {false};
                    successor = nodeToRemove.next[level].get(marked);
                    while (!marked[0]) {
                        nodeToRemove.next[level].attemptMark(successor, true);
                        successor = nodeToRemove.next[level].get(marked);
                    }
                }
                boolean[] marked = {false};
                successor = nodeToRemove.next[bottomLevel].get(marked);
                while (true) {
                    boolean iMarkedIt =
                            nodeToRemove.next[bottomLevel].compareAndSet(successor, successor,
                                    false, true);
                    successor = succesors[bottomLevel].next[bottomLevel].get(marked);
                    if (iMarkedIt) {
                        find(x, predecessors, succesors);
                        return true;
                    }
                    else if (marked[0]) return false;
                }
            }
        }
    }

    private boolean find(T x, node<T>[] preds, node<T>[] succs) {
        int bottomLevel = 0;
        int key = x.hashCode();
        boolean[] marked = {false};
        boolean snip;
        node<T> pred = null, curr = null, succ = null;
        retry:
        while (true) {
            pred = head;
            for (int level = (int)maxLevel; level >= bottomLevel; level--) {
                curr = pred.next[level].getReference();
                while (true) {
                    succ = curr.next[level].get(marked);
                    while (marked[0]) {
                        snip = pred.next[level].compareAndSet(curr, succ,
                                false, false);
                        if (!snip) continue retry;
                        curr = pred.next[level].getReference();
                        succ = curr.next[level].get(marked);
                    }
                    if (curr.key < key){
                        pred = curr; curr = succ;
                    } else {
                        break;
                    }
                }
                preds[level] = pred;
                succs[level] = curr;
            }
            return (curr.key == key);
        }
    }

    public boolean contains(T x) {
        int bottomLevel = 0;
        int v = x.hashCode();
        boolean[] marked = {false};
        node<T> pred = head, curr = null, succ = null;
        for (int level = (int)maxLevel; level >= bottomLevel; level--) {
            curr = pred.next[level].getReference();
            while (true) {
                succ = curr.next[level].get(marked);
                while (marked[0]) {
                    curr = pred.next[level].getReference();
                    succ = curr.next[level].get(marked);
                }
                if (curr.key < v){
                    pred = curr;
                    curr = succ;
                } else {
                    break;
                }
            }
        }
        return (curr.key == v);
    }

    private static final class node<T> {
        final T value;
        final int key;
        final AtomicMarkableReference<node<T>>[] next;
        private int maxLevel;
        public node(int key, int maxLevel) {
            this.maxLevel = maxLevel;
            this.value = null;
            this.key = key;
            this.next = new AtomicMarkableReference[maxLevel + 1];
            for (int i = 0; i < next.length; i++) {
                next[i] = new AtomicMarkableReference<node<T>>(null,false);
            }
        }
        public node(T x, int height, int maxLevel) {
            this.maxLevel = maxLevel;
            this.value = null;
            this.key = x.hashCode();
            this.next = new AtomicMarkableReference[maxLevel + 1];
            for (int i = 0; i < next.length; i++) {
                next[i] = new AtomicMarkableReference<node<T>>(null,false);
            }
        }
    }
}