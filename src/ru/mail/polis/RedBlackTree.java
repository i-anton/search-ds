package ru.mail.polis;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;

public class RedBlackTree<E extends Comparable<E>> extends AbstractSet<E> implements BalancedSortedSet<E> {

    private final Comparator<E> comparator;
    private Node root;
    private int size;

    public RedBlackTree() {
        this(null);
    }
    public RedBlackTree(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    /**
     * Вставляет элемент в дерево.
     * Инвариант: на вход всегда приходит NotNull объект, который имеет корректный тип
     *
     * @param value элемент который необходимо вставить
     * @return true, если элемент в дереве отсутствовал
     */
    @Override
    public boolean add(E value) {
        if (root == null) {
            root = new Node(value);
            size++;
            return true;
        }
        Node curr = root;
        while (true) {
            int cmp = compare(value, curr.value);
            if (cmp < 0) {
                if (curr.left == null) {
                    curr.left = new Node(value);
                    curr.left.parent = curr;
                    balanceAfterInsertion(curr.left);
                    size++;
                    break;
                }
                curr = curr.left;
            } else if (cmp > 0) {
                if (curr.right == null) {
                    curr.right = new Node(value);
                    curr.right.parent = curr;
                    balanceAfterInsertion(curr.right);
                    size++;
                    break;
                }
                curr = curr.right;
            } else {
                return false;
            }
        }
        return true;
    }

    private void balanceAfterInsertion(Node curr) {
        setColor(curr, Color.RED);
        if ((curr != null) && (curr != root) && isRed(getParent(curr))) {
            if (isRed(getSibling(getParent(curr)))) {
                setColor(getParent(curr), Color.BLACK);
                setColor(getSibling(getParent(curr)), Color.BLACK);
                setColor(getGrandparent(curr), Color.RED);
                balanceAfterInsertion(getGrandparent(curr));
            } else if (getParent(curr) == getLeft(getGrandparent(curr))) {
                if (curr == getRight(getParent(curr))) {
                    rotateLeft(curr = getParent(curr));
                }
                setColor(getParent(curr), Color.BLACK);
                setColor(getGrandparent(curr), Color.RED);
                rotateRight(getGrandparent(curr));
            } else if (getParent(curr) == getRight(getGrandparent(curr))) {
                if (curr == getLeft(getParent(curr))) {
                    rotateRight(curr = getParent(curr));
                }
                setColor(getParent(curr), Color.BLACK);
                setColor(getGrandparent(curr), Color.RED);
                rotateLeft(getGrandparent(curr));
            }
        }
        setColor(root, Color.BLACK);
    }

    /**
     * Удаляет элемент с таким же значением из дерева.
     * Инвариант: на вход всегда приходит NotNull объект, который имеет корректный тип
     *
     * @param object элемент который необходимо вставить
     * @return true, если элемент содержался в дереве
     */
    @Override
    public boolean remove(Object object) {
        @SuppressWarnings("unchecked")
        E value = (E) object;
        if (root ==null) {
            return false;
        }
        Node curr = root;
        int cmp;
        while ((cmp = compare(curr.value, value)) != 0) {
            if (cmp > 0) {
                curr = curr.left;
            } else {
                curr = curr.right;
            }
            if (curr == null) {
                return false;
            }
        }
        if ((curr.left != null) && (curr.right != null)) {
            Node predecessor = getPredecessor(curr);
            curr.value = predecessor.value;
            curr = predecessor;
        }
        Node pullUp = (curr.left == null) ? curr.right : curr.left;
        if (pullUp != null) {
            if (curr == root) {
                if (pullUp.parent.left == pullUp) {
                    pullUp.parent.left = null;
                } else {
                    pullUp.parent.right = null;
                }
                pullUp.parent = null;
                root = pullUp;
            } else if (curr.parent.left == curr) {
                curr.parent.left = pullUp;
                curr.parent.left.parent = curr.parent;
            } else {
                curr.parent.right = pullUp;
                curr.parent.right.parent = curr.parent;
            }
            if (!isRed(curr)) {
                balanceAfterRemoval(pullUp);
            }
        } else if (curr == root) {
            root = null;
        } else {
            if (!isRed(curr)) {
                balanceAfterRemoval(curr);
            }
            if (curr.parent.left == curr) {
                curr.parent.left = null;
            } else {
                curr.parent.right = null;
            }
            curr.parent = null;
        }
        size--;
        return true;
    }

    private void balanceAfterRemoval(Node curr) {
        while ((curr != root) && !isRed(curr)) {
            if (curr == getLeft(getParent(curr))) {
                Node sibling = getRight(getParent(curr));
                if (isRed(sibling)) {
                    setColor(sibling, Color.BLACK);
                    setColor(getParent(curr), Color.RED);
                    rotateLeft(getParent(curr));
                    sibling = getRight(getParent(curr));
                }
                if (!isRed(getLeft(sibling)) && !isRed(getRight(sibling))) {
                    setColor(sibling, Color.RED);
                    curr = getParent(curr);
                } else {
                    if (!isRed(getRight(sibling))) {
                        setColor(getLeft(sibling), Color.BLACK);
                        setColor(sibling, Color.RED);
                        rotateRight(sibling);
                        sibling = getRight(getParent(curr));
                    }
                    setColor(sibling, isRed(getParent(curr)) ? Color.RED : Color.BLACK);
                    setColor(getParent(curr), Color.BLACK);
                    setColor(getRight(sibling), Color.BLACK);
                    rotateLeft(getParent(curr));
                    curr = root;
                }
            } else {
                Node sibling = getLeft(getParent(curr));
                if (isRed(sibling)) {
                    setColor(sibling, Color.BLACK);
                    setColor(getParent(curr), Color.RED);
                    rotateRight(getParent(curr));
                    sibling = getLeft(getParent(curr));
                }
                if (!isRed(getLeft(sibling)) && !isRed(getRight(sibling))) {
                    setColor(sibling, Color.RED);
                    curr = getParent(curr);
                } else {
                    if (!isRed(getLeft(sibling))) {
                        setColor(getRight(sibling), Color.BLACK);
                        setColor(sibling, Color.RED);
                        rotateLeft(sibling);
                        sibling = getLeft(getParent(curr));
                    }
                    setColor(sibling, isRed(getParent(curr)) ? Color.RED : Color.BLACK);
                    setColor(getParent(curr), Color.BLACK);
                    setColor(getLeft(sibling), Color.BLACK);
                    rotateRight(getParent(curr));
                    curr = root;
                }
            }
        }
        setColor(curr, Color.BLACK);
    }

    /**
     * Ищет элемент с таким же значением в дереве.
     * Инвариант: на вход всегда приходит NotNull объект, который имеет корректный тип
     *
     * @param object элемент который необходимо поискать
     * @return true, если такой элемент содержится в дереве
     */
    @Override
    public boolean contains(Object object) {
        @SuppressWarnings("unchecked")
        E value = (E) object;
        return find(root, value);
    }

    private boolean find(Node curr, E value) {
        if (curr == null) {
            return false;
        }
        if (compare(value, curr.value) < 0) {
            return find(curr.left, value);
        } else {
            return compare(value, curr.value) <= 0 || find(curr.right, value);
        }
    }

    /**
     * Ищет наименьший элемент в дереве
     * @return Возвращает наименьший элемент в дереве
     * @throws NoSuchElementException если дерево пустое
     */
    @Override
    public E first() {
        if (root == null) {
            throw new NoSuchElementException("first");
        }
        Node curr = root;
        while (curr.left != null) {
            curr = curr.left;
        }
        return curr.value;
    }

    /**
     * Ищет наибольший элемент в дереве
     * @return Возвращает наибольший элемент в дереве
     * @throws NoSuchElementException если дерево пустое
     */
    @Override
    public E last() {
        if (root == null) {
            throw new NoSuchElementException("last");
        }
        Node curr = root;
        while (curr.right != null) {
            curr = curr.right;
        }
        return curr.value;
    }

    private int compare(E v1, E v2) {
        return comparator == null ? v1.compareTo(v2) : comparator.compare(v1, v2);
    }

    @Override
    public Comparator<? super E> comparator() {
        return comparator;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public String toString() {
        return "RBTree{" +
                "size=" + size + ", " +
                "tree=" + root +
                '}';
    }

    @Override
    public SortedSet<E> subSet(E fromElement, E toElement) {
        throw new UnsupportedOperationException("subSet");
    }

    @Override
    public SortedSet<E> headSet(E toElement) {
        throw new UnsupportedOperationException("headSet");
    }

    @Override
    public SortedSet<E> tailSet(E fromElement) {
        throw new UnsupportedOperationException("tailSet");
    }

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException("iterator");
    }

    /**
     * Обходит дерево и проверяет выполнение свойств сбалансированного красно-чёрного дерева
     * <p>
     * 1) Корень всегда чёрный.
     * 2) Если узел красный, то его потомки должны быть чёрными (обратное не всегда верно)
     * 3) Все пути от узла до листьев содержат одинаковое количество чёрных узлов (чёрная высота)
     *
     * @throws NotBalancedTreeException если какое-либо свойство невыполнено
     */
    @Override
    public void checkBalanced() throws NotBalancedTreeException {
        if (root != null) {
            if (root.color != Color.BLACK) {
                throw new NotBalancedTreeException("Root must be black");
            }
            traverseTreeAndCheckBalanced(root);
        }
    }

    private int traverseTreeAndCheckBalanced(Node node) throws NotBalancedTreeException {
        if (node == null) {
            return 1;
        }
        int leftBlackHeight = traverseTreeAndCheckBalanced(node.left);
        int rightBlackHeight = traverseTreeAndCheckBalanced(node.right);
        if (leftBlackHeight != rightBlackHeight) {
            throw NotBalancedTreeException.create("Black height must be equal.", leftBlackHeight, rightBlackHeight, node.toString());
        }
        if (node.color == Color.RED) {
            checkRedNodeRule(node);
            return leftBlackHeight;
        }
        return leftBlackHeight + 1;
    }

    private void checkRedNodeRule(Node node) throws NotBalancedTreeException {
        if (node.left != null && node.left.color != Color.BLACK) {
            throw new NotBalancedTreeException("If a node is red, then left child must be black.\n" + node.toString());
        }
        if (node.right != null && node.right.color != Color.BLACK) {
            throw new NotBalancedTreeException("If a node is red, then right child must be black.\n" + node.toString());
        }
    }

    private void setColor(Node curr, Color clr) {
        if (curr != null) {
            curr.color = clr;
        }
    }

    private boolean isRed(Node curr) {
        return (curr != null) && (curr.color == Color.RED);
    }

    private Node getParent(Node curr) {
        return (curr == null) ? null : curr.parent;
    }

    private Node getGrandparent(Node curr) {
        if ((curr == null) || (getParent(curr) == null)) {
            return null;
        } else {
            return curr.parent.parent;
        }
    }

    private Node getSibling(Node curr) {
        if (curr == null || curr.parent == null) {
            return null;
        }
        if (curr == curr.parent.left) {
            return curr.parent.right;
        } else {
            return curr.parent.left;
        }
    }

    private Node getLeft(Node curr) {
        return (curr == null) ? null : curr.left;
    }

    private Node getRight(Node curr) {
        return (curr == null) ? null : curr.right;
    }

    private void rotateLeft(Node curr) {
        Node rightNode = curr.right;
        curr.right = rightNode.left;
        if (curr.right != null) {
            curr.right.parent = curr;
        }
        if (curr.parent == null) {
            rightNode.parent = null;
            root = rightNode;
        } else if (curr.parent.left == curr) {
            curr.parent.left = rightNode;
            curr.parent.left.parent = curr.parent;
        } else {
            curr.parent.right = rightNode;
            curr.parent.right.parent = curr.parent;
        }
        rightNode.left = curr;
        rightNode.left.parent = rightNode;
    }

    private void rotateRight(Node curr) {
        Node leftNode = curr.left;
        curr.left = leftNode.right;
        if (curr.left != null) {
            curr.left.parent = curr;
        }
        if (curr.parent == null) {
            leftNode.parent = null;
            root = leftNode;
        } else if (curr.parent.left == curr) {
            curr.parent.left = leftNode;
            curr.parent.left.parent = curr.parent;
        } else {
            curr.parent.right = leftNode;
            curr.parent.right.parent = curr.parent;
        }
        leftNode.right = curr;
        leftNode.right.parent = leftNode;
    }

    private Node getPredecessor(Node curr) {
        Node node = curr.left;
        if (node != null) {
            while (node.right != null) {
                node = node.right;
            }
        }
        return node;
    }

    enum Color {
        RED, BLACK
    }

    final class Node {
        E value;
        Node left;
        Node right;
        Node parent;
        Color color = Color.BLACK;

        Node (E value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "value=" + value +
                    ", left=" + left +
                    ", right=" + right +
                    ", color=" + color +
                    '}';
        }
    }
}
