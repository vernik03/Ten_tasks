package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LockFreeSkipListTest {
    @Test
    void LockFreeSkipListInsertion() {
        LockFreeSkipList<Integer> skipList = new LockFreeSkipList<Integer>();
        assertTrue(skipList.add(42));
        assertTrue(skipList.contains(42));
        assertFalse(skipList.contains(43));
    }

    @Test
    void LockFreeSkipListRemoval() {
        LockFreeSkipList<Integer> skipList = new LockFreeSkipList<Integer>();
        assertTrue(skipList.add(42));
        assertTrue(skipList.contains(42));
        assertTrue(skipList.remove(42));
        assertFalse(skipList.contains(42));
    }

    @Test
    void LockFreeSkipListRemoveNonExistent() {
        LockFreeSkipList<Integer> skipList = new LockFreeSkipList<Integer>();
        assertFalse(skipList.remove(42));
    }
}