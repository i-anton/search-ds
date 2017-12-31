package ru.mail.polis;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;

public class RedBlackTree<E extends Comparable<E>> extends AbstractSet<E> implements BalancedSortedSet<E> {

    private final Comparator<E> comparator;
    private Node<E> root;
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
        Node<E> curr = root;

        if (curr == null) {
            curr = new Node<>(value,null);
            root = curr;
        } else {
            while (true) {
                int cmp = compare(curr.value, value);
                if (cmp == 0) {
                    return false;
                } else if (cmp < 0) {
                    if (curr.right != null) {
                        curr = curr.right;
                    } else {
                        curr.right = new Node<>(value,curr);
                        curr = curr.right;
                        break;
                    }
                } else {
                    if (curr.left != null) {
                        curr = curr.left;
                    } else {
                        curr.left = new Node<>(value,curr);
                        curr = curr.left;
                        break;
                    }
                }
            }
        }
        size++;

        insertCase1(curr);
        return true;
    }

    private void insertCase1(Node<E> node) {
        if (node.parent == null) {
            node.color = Color.BLACK;
        } else {
            insertCase2(node);
        }
    }

    private void insertCase2(Node<E> node) {
        if (getParent(node).color == Color.BLACK) {
            return;
        } else {
            insertCase3(node);
        }
    }

    private void insertCase3(Node<E> node) {
        Node<E> uncle = getUncle(node);
        Node<E> granddad;

        if ((uncle != null) && (uncle.color == Color.RED)) {
            (node.parent).color = Color.BLACK;
            uncle.color = Color.BLACK;
            granddad = getGranddad(node);
            granddad.color = Color.RED;
            insertCase1(granddad);
        } else {
            insertCase4(node);
        }
    }

    private void insertCase4(Node<E> node) {
        Node<E> granddad = getGranddad(node);

        if ((node == getRight(getParent(node))) && (getParent(node) == getLeft(granddad))) {
            rotateLeft(getParent(node));
            node = getLeft(node);
        } else if ((node == getLeft(getParent(node))) && (getParent(node) == getRight(granddad))) {
            rotateRight(getParent(node));
            node = getRight(node);
        }
        insertCase5(node);
    }

    private void insertCase5(Node<E> node) {
        Node<E> granddad = getGranddad(node);

        (node.parent).color = Color.BLACK;
        granddad.color = Color.RED;
        if ((node == getLeft(getParent(node))) && (getParent(node) == getLeft(granddad))) {
            rotateRight(granddad);
        } else {
            rotateLeft(granddad);
        }
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
        return false;
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
        if (root != null) {
            Node<E> curr = root;
            while (curr != null) {
                int cmp = compare(curr.value, value);
                if (cmp == 0) {
                    return true;
                } else if (cmp < 0) {
                    curr = curr.right;
                } else {
                    curr = curr.left;
                }
            }
        }
        return false;
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
        } else {
            Node<E> min = root;
            while (min.left != null) {
                min = min.left;
            }
            return  min.value;
        }
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
        } else {
            Node<E> max = root;
            while (max.right != null) {
                max = max.right;
            }
            return max.value;
        }
    }

    private void rotateLeft(Node<E> node) {
        Node<E> pivot = getRight(node);
        if (getParent(node) != null) {
            if (getLeft(getParent(node)) == node) {
                (node.parent).left = pivot;
            } else {
                (node.parent).right = pivot;
            }
        }

        node.right = getLeft(pivot);
        if (getLeft(pivot) != null) {
            (pivot.left).parent = node;
        }

        pivot.parent = node.parent;
        pivot.left = node;
        node.parent = pivot;
        if (getParent(pivot) == null) {
            root = pivot;
        }
    }

    private void rotateRight(Node<E> node) {
        Node<E> pivot = getLeft(node);
        if (getParent(node) != null) {
            if (getLeft(getParent(node)) == node) {
                (node.parent).left = pivot;
            } else {
                (node.parent).right = pivot;
            }
        }

        node.left = getRight(pivot);
        if (getRight(pivot) != null) {
            (pivot.right).parent = node;
        }

        pivot.parent = node.parent;
        node.parent = pivot;
        pivot.right = node;
        if (getParent(pivot) == null) {
            root = pivot;
        }
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

    enum Color {
        RED, BLACK
    }

    private Node<E> getParent(Node<E> node) {
        return node == null ? null : node.parent;
    }

    private Node<E> getLeft(Node<E> node) {
        return node == null ? null : node.left;
    }

    private Node<E> getRight(Node<E> node) {
        return node == null ? null : node.right;
    }

    private Node<E> getBrother(Node<E> node) {
        if (node == getLeft(getParent(node))) {
            return getRight(getParent(node));
        } else {
            return getLeft(getParent(node));
        }
    }

    private Node<E> getGranddad(Node<E> node) {
        return getParent(getParent(node));
    }

    private Node<E> getUncle(Node<E> node) {
        Node<E> granddad = getGranddad(node);
        if (granddad == null) {
            return null;
        } else if (node.parent == granddad.left) {
            return granddad.right;
        } else {
            return granddad.left;
        }
    }

    static final class Node<E> {
        E value;
        Node<E> left;
        Node<E> right;
        Node<E> parent;
        Color color = Color.BLACK;

        public Node(E value,Node<E> parent) {
            this.value = value;
            this.parent = parent;
            this.color = Color.RED;
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
