package org.example;

import java.util.concurrent.atomic.AtomicReference;

class Node<T> {
    public T val;
    public AtomicReference<Node<T>> next;

    public Node(T val, AtomicReference<Node<T>> next) {
        this.val = val;
        this.next = next;
    }
}

public class NonBlockingQueue<T> {
    private Node<T> dummy = new Node<>(null, new AtomicReference<Node<T>>(null));
    private AtomicReference<Node<T>> head = new AtomicReference<Node<T>>(dummy);
    private AtomicReference<Node<T>> tail = new AtomicReference<Node<T>>(dummy);

    public T pop() {
        while(true) {
            Node<T> head = this.head.get();
            Node<T> tail = this.tail.get();
            Node<T> nextHead = head.next.get();

            if(head == tail) {
                if(nextHead == null) {
                    throw new NullPointerException();
                } else {
                    this.tail.compareAndSet(tail, nextHead);
                }
            } else {
                var result = nextHead.val;
                if(this.head.compareAndSet(head, nextHead)) {
                    return result;
                }
            }
        }
    }

    public void push(T x) {
        var newTail = new Node<T>(x, new AtomicReference<Node<T>>(null));

        while(true) {
            var tail = this.tail.get();
            if(tail.next.compareAndSet(null, newTail)) {
                this.tail.compareAndSet(tail, newTail);
                return;
            } else {
                this.tail.compareAndSet(tail, newTail.next.get());
            }
        }
    }
}