package ru.mail.polis;

import java.util.*;

public class RedBlackTree<E extends Comparable<E>> extends AbstractSet<E> implements BalancedSortedSet<E> {

    private final Comparator<E> comparator;
    private Node root;
    private int size;

    enum Color {
        RED, BLACK
    }

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
        Node current = root;
        while (true) {
            int compare = compare(value, current.value);
            if (compare < 0) {
                if (current.left == null) {
                    current.left = new Node(value);
                    current.left.parent = current;
                    balanceAfterInsertion(current.left);
                    size++;
                    break;
                }
                current = current.left;
            } else if (compare > 0) {
                if (current.right == null) {
                    current.right = new Node(value);
                    current.right.parent = current;
                    balanceAfterInsertion(current.right);
                    size++;
                    break;
                }
                current = current.right;
            } else {
                return false;
            }
        }
        return true;
    }

    private void balanceAfterInsertion(Node current) {
        setNodeColor(current, Color.RED);
        if ((current != null) && (current != root) && isRed(getParent(current))) {
            if (isRed(getSibling(getParent(current)))) {
                setNodeColor(getParent(current), Color.BLACK);
                setNodeColor(getSibling(getParent(current)), Color.BLACK);
                setNodeColor(getGrandparent(current), Color.RED);
                balanceAfterInsertion(getGrandparent(current));
            } else if (getParent(current) == getLeft(getGrandparent(current))) {
                if (current == getRight(getParent(current))) {
                    rotateLeft(current = getParent(current));
                }
                setNodeColor(getParent(current), Color.BLACK);
                setNodeColor(getGrandparent(current), Color.RED);
                rotateRight(getGrandparent(current));
            } else if (getParent(current) == getRight(getGrandparent(current))) {
                if (current == getLeft(getParent(current))) {
                    rotateRight(current = getParent(current));
                }
                setNodeColor(getParent(current), Color.BLACK);
                setNodeColor(getGrandparent(current), Color.RED);
                rotateLeft(getGrandparent(current));
            }
        }
        setNodeColor(root, Color.BLACK);
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
        if (root == null) {
            return false;
        }
        Node current = root;
        int compare;
        while ((compare = compare(current.value, value)) != 0) {
            if (compare > 0) {
                current = current.left;
            } else {
                current = current.right;
            }
            if (current == null) {
                return false;
            }
        }
        if ((current.left != null) && (current.right != null)) {
            Node predecessor = getPredecessor(current);
            current.value = predecessor.value;
            current = predecessor;
        }
        Node nodeToMoveUp = (current.left == null) ? current.right : current.left;
        if (nodeToMoveUp != null) {
            if (current == root) {
                if (nodeToMoveUp.parent.left == nodeToMoveUp) {
                    nodeToMoveUp.parent.left = null;
                } else {
                    nodeToMoveUp.parent.right = null;
                }
                nodeToMoveUp.parent = null;
                root = nodeToMoveUp;
            } else if (current.parent.left == current) {
                current.parent.left = nodeToMoveUp;
                current.parent.left.parent = current.parent;
            } else {
                current.parent.right = nodeToMoveUp;
                current.parent.right.parent = current.parent;
            }
            if (!isRed(current)) {
                balanceAfterRemoval(nodeToMoveUp);
            }
        } else if (current == root) {
            root = null;
        } else {
            if (!isRed(current)) {
                balanceAfterRemoval(current);
            }
            if (current.parent.left == current) {
                current.parent.left = null;
            } else {
                current.parent.right = null;
            }
            current.parent = null;
        }
        size--;
        return true;
    }

    private void balanceAfterRemoval(Node current) {
        while ((current != root) && !isRed(current)) {
            if (current == getLeft(getParent(current))) {
                Node sibling = getRight(getParent(current));
                if (isRed(sibling)) {
                    setNodeColor(sibling, Color.BLACK);
                    setNodeColor(getParent(current), Color.RED);
                    rotateLeft(getParent(current));
                    sibling = getRight(getParent(current));
                }
                if (!isRed(getLeft(sibling)) && !isRed(getRight(sibling))) {
                    setNodeColor(sibling, Color.RED);
                    current = getParent(current);
                } else {
                    if (!isRed(getRight(sibling))) {
                        setNodeColor(getLeft(sibling), Color.BLACK);
                        setNodeColor(sibling, Color.RED);
                        rotateRight(sibling);
                        sibling = getRight(getParent(current));
                    }
                    setNodeColor(sibling, isRed(getParent(current)) ? Color.RED : Color.BLACK);
                    setNodeColor(getParent(current), Color.BLACK);
                    setNodeColor(getRight(sibling), Color.BLACK);
                    rotateLeft(getParent(current));
                    current = root;
                }
            } else {
                Node sibling = getLeft(getParent(current));
                if (isRed(sibling)) {
                    setNodeColor(sibling, Color.BLACK);
                    setNodeColor(getParent(current), Color.RED);
                    rotateRight(getParent(current));
                    sibling = getLeft(getParent(current));
                }
                if (!isRed(getLeft(sibling)) && !isRed(getRight(sibling))) {
                    setNodeColor(sibling, Color.RED);
                    current = getParent(current);
                } else {
                    if (!isRed(getLeft(sibling))) {
                        setNodeColor(getRight(sibling), Color.BLACK);
                        setNodeColor(sibling, Color.RED);
                        rotateLeft(sibling);
                        sibling = getLeft(getParent(current));
                    }
                    setNodeColor(sibling, isRed(getParent(current)) ? Color.RED : Color.BLACK);
                    setNodeColor(getParent(current), Color.BLACK);
                    setNodeColor(getLeft(sibling), Color.BLACK);
                    rotateRight(getParent(current));
                    current = root;
                }
            }
        }
        setNodeColor(current, Color.BLACK);
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
     *
     * @return Возвращает наименьший элемент в дереве
     * @throws NoSuchElementException если дерево пустое
     */
    @Override
    public E first() {
        if (root == null) throw new NoSuchElementException("first");

        Node curr = root;
        while (curr.left != null) {
            curr = curr.left;
        }
        return curr.value;
    }

    /**
     * Ищет наибольший элемент в дереве
     *
     * @return Возвращает наибольший элемент в дереве
     * @throws NoSuchElementException если дерево пустое
     */
    @Override
    public E last() {
        if (root == null) throw new NoSuchElementException("last");

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

    private void setNodeColor(Node curr, Color clr) {
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

    private void rotateLeft(Node current) {
        Node rightNode = current.right;
        current.right = rightNode.left;
        if (current.right != null) {
            current.right.parent = current;
        }
        if (current.parent == null) {
            rightNode.parent = null;
            root = rightNode;
        } else if (current.parent.left == current) {
            current.parent.left = rightNode;
            current.parent.left.parent = current.parent;
        } else {
            current.parent.right = rightNode;
            current.parent.right.parent = current.parent;
        }
        rightNode.left = current;
        rightNode.left.parent = rightNode;
    }

    private void rotateRight(Node current) {
        Node leftNode = current.left;
        current.left = leftNode.right;
        if (current.left != null) {
            current.left.parent = current;
        }
        if (current.parent == null) {
            leftNode.parent = null;
            root = leftNode;
        } else if (current.parent.left == current) {
            current.parent.left = leftNode;
            current.parent.left.parent = current.parent;
        } else {
            current.parent.right = leftNode;
            current.parent.right.parent = current.parent;
        }
        leftNode.right = current;
        leftNode.right.parent = leftNode;
    }

    private Node getPredecessor(Node current) {
        Node node = current.left;
        if (node != null) {
            while (node.right != null) {
                node = node.right;
            }
        }
        return node;
    }

    final class Node {
        E value;
        Node left;
        Node right;
        Node parent;
        Color color = Color.BLACK;

        Node(E value) {
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
