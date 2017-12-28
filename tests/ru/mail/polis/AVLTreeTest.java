package ru.mail.polis;

import org.junit.Test;

import static org.junit.Assert.*;

public class AVLTreeTest {
    private static AVLTree<Integer> tree = new AVLTree<>();

    @Test
    public void add() throws Exception {

        assertTrue(tree.add(5));
        assertTrue(tree.add(2));
        assertTrue(tree.add(3));
        assertTrue(tree.add(6));
        assertFalse(tree.add(3));
    }

    @Test
    public void remove() throws Exception {
        assertTrue(tree.add(5));
        assertTrue(tree.add(2));
        assertTrue(tree.add(3));
        assertTrue(tree.add(6));
        assertTrue(tree.remove(2));
        assertTrue(tree.remove(3));
        assertFalse(tree.remove(10));
        tree.preOrder();
    }

    @Test
    public void contains() throws Exception {
        assertTrue(tree.add(5));
        assertTrue(tree.add(2));
        assertTrue(tree.add(3));
        assertTrue(tree.add(6));
        tree.preOrder();
        assertTrue(tree.contains(5));
        assertTrue(tree.contains(6));
        assertFalse(tree.contains(10));

    }

    @Test
    public void first() throws Exception {
        assertTrue(tree.add(5));
        assertTrue(tree.add(2));
        assertTrue(tree.add(3));
        assertTrue(tree.add(6));
        assertEquals(2,  tree.first().intValue());
    }

    @Test
    public void last() throws Exception {

        assertTrue(tree.add(5));
        assertTrue(tree.add(2));
        assertTrue(tree.add(3));
        assertTrue(tree.add(6));
        assertEquals(6,  tree.last().intValue());
    }

    @Test
    public void checkBalanced() throws Exception {
        assertTrue(tree.add(5));
        assertTrue(tree.add(2));
        assertTrue(tree.add(3));
        assertTrue(tree.add(6));
        tree.checkBalanced();
    }

}