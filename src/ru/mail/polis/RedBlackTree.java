package ru.mail.polis;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;

import static ru.mail.polis.RedBlackTree.Color.BLACK;
import static ru.mail.polis.RedBlackTree.Color.RED;

@interface Nullable {
}

@interface NotNull {
}

public class RedBlackTree<E extends Comparable<E>> extends AbstractSet<E> implements BalancedSortedSet<E> {

    private final Comparator<E> comparator;
    private Node<E> root;
    private int size;
    private Node<E> nil;

    public RedBlackTree() {
        this(null);
    }

    public RedBlackTree(Comparator<E> comparator) {
        this.comparator = comparator;
        nil = new Node<>(null, BLACK);
        nil.left = nil.right = nil;
        root = nil;
    }

    private void rotateLeft(Node<E> node) {
        Node<E> curr = node.right;
        node.right = curr.left;
        if (curr.left != nil)
            curr.left.parent = node;
        curr.parent = node.parent;
        if (node.parent == null) {
            root = curr;
        } else if (node == node.parent.left) {
            node.parent.left = curr;
        } else {
            node.parent.right = curr;
        }
        curr.left = node;
        node.parent = curr;
    }

    private void rotateRight(Node<E> node) {
        Node<E> curr = node.left;
        node.left = curr.right;
        if (curr.right != nil)
            curr.right.parent = node;
        curr.parent = node.parent;
        if (node.parent == null) {
            root = curr;
        } else if (node == node.parent.right) {
            node.parent.right = curr;
        } else {
            node.parent.left = curr;
        }
        curr.right = node;
        node.parent = curr;
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
        Node<E> parent = null;
        int comp = 0;
        // спуск вниз для поиска места вставки или элемента
        while (curr != nil) {
            parent = curr;
            comp = compare(curr.value, value);
            if (comp == 0) return false;
            curr = (comp < 0) ? curr.right : curr.left;
        }
        assert parent != nil;
        Node<E> temp = new Node<>(value, RED, parent);
        temp.left = temp.right = nil;
        if (parent == null) root = temp;
        else if (comp < 0) parent.right = temp;
        else parent.left = temp;
        ++size;
        insertFix(temp);
        return true;
    }

    /**
     * Перебалансировка после вставки
     * @param node - с кого начинать
     */
    private void insertFix(@NotNull Node<E> node) {
        while (node != root && node.parent.color == RED) {
            @NotNull Node<E> parent = node.parent;
            if (bro(parent).color == RED) {
                @NotNull Node<E> y = bro(parent);
                node.parent.color = BLACK;
                y.color = BLACK;
                node.parent.parent.color = RED;
                node = node.parent.parent;
            } else if (parent == node.parent.parent.right) {
                if (node == parent.left) {
                    node = parent;
                    rotateRight(node);
                }
                node.parent.color = BLACK;
                node.parent.parent.color = RED;
                rotateLeft(node.parent.parent);
            } else if (parent == node.parent.parent.left) {
                if (node == parent.right) {
                    node = parent;
                    rotateLeft(node);
                }
                node.parent.color = BLACK;
                node.parent.parent.color = RED;
                rotateRight(node.parent.parent);
            }
        }
        root.color = BLACK;
    }

    /**
     * Осуществляет поиск узла с значением value
     * @param value - значение для поиска
     * @return - nil, если не найдено, иначе узел
     */
    private Node<E> findNode(E value) {
        Node<E> curr = root;
        // спуск вниз для поиска элемента
        while (curr != nil) {
            int comp = compare(curr.value, value);
            if (comp == 0) return curr;
            curr = (comp < 0) ? curr.right : curr.left;
        }
        return curr;
    }

    private Node<E> successor(Node<E> node) {
        Node<E> found = node.right;
        if (found != nil) {
            while (found.left != nil)
                found = found.left;
            return found;
        }
        found = node.parent;
        while (found != nil && node == found.right) {
            node = found;
            found = found.parent;
        }
        return found;
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
        Node<E> removeNode = findNode((E) object);
        if (removeNode == nil) return false;
        size--;
        remove(removeNode);
        return true;
    }

    private void remove(Node<E> node) {
        Node<E> x, y;
        y = (node.left == nil || node.right == nil) ? node : successor(node);
        x = (y.left != nil) ? y.left : y.right;
        x.parent = y.parent;
        if (y.parent == null)
            root = x;
        else if (y == y.parent.left)
            y.parent.left = x;
        else
            y.parent.right = x;
        if (node != y) node.value = y.value;
        if (y.color == BLACK) removeFix(x);
    }

    /**
     * Перебалансировка после удаления
     *
     * @param node - not null, откуда начинаем балансировку
     */
    private void removeFix(Node<E> node) {
        Node<E> temp;

        while (node != root && node.color == BLACK) {
            temp = bro(node);
            boolean isLeft = (node == node.parent.left);
            if (temp.color == RED) {
                temp.color = BLACK;
                node.parent.color = RED;
                if (isLeft) rotateLeft(node.parent);
                else rotateRight(node.parent);
                temp = bro(node);
            }
            if (temp.left.color == BLACK && temp.right.color == BLACK) {
                temp.color = RED;
                node = node.parent;
            } else {
                if (isLeft && temp.right.color == BLACK) {
                    temp.left.color = BLACK;
                    temp.color = RED;
                    rotateRight(temp);
                    temp = node.parent.right;
                } else if (!isLeft && temp.left.color == BLACK) {
                    temp.right.color = BLACK;
                    temp.color = RED;
                    rotateLeft(temp);
                    temp = node.parent.left;
                }
                temp.color = node.parent.color;
                node.parent.color = BLACK;
                if (isLeft) {
                    temp.right.color = BLACK;
                    rotateLeft(node.parent);
                } else {
                    temp.left.color = BLACK;
                    rotateRight(node.parent);
                }
                node = root;
            }
        }
        node.color = BLACK;
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
        return findNode(value) != nil;
    }

    /**
     * Ищет наименьший элемент в дереве
     *
     * @return Возвращает наименьший элемент в дереве
     * @throws NoSuchElementException если дерево пустое
     */
    @Override
    public E first() {
        if (size == 0) {
            throw new NoSuchElementException("first");
        }
        Node<E> curr = root;
        while ((curr.left) != nil) {
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
        if (size == 0) {
            throw new NoSuchElementException("last");
        }
        Node<E> curr = root;
        while ((curr.right) != nil) {
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
        StringBuilder sb = new StringBuilder("RBTree{");
        sb.append("size=").append(size).append(", tree=");
        inOrderTraverse(root, sb);
        sb.append("}");
        return sb.toString();
    }

    private void inOrderTraverse(Node<E> curr, StringBuilder sb) {
        if (curr == nil) return;
        inOrderTraverse(curr.left, sb);
        sb.append(curr.value).append(",");
        inOrderTraverse(curr.right, sb);
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
        if (root != nil) {
            if (root.color != BLACK) {
                throw new NotBalancedTreeException("Root must be black");
            }
            traverseTreeAndCheckBalanced(root);
        }
    }

    private int traverseTreeAndCheckBalanced(Node node) throws NotBalancedTreeException {
        if (node == nil) {
            return 1;
        }
        int leftBlackHeight = traverseTreeAndCheckBalanced(node.left);
        int rightBlackHeight = traverseTreeAndCheckBalanced(node.right);
        if (leftBlackHeight != rightBlackHeight) {
            throw NotBalancedTreeException.create("Black height must be equal.", leftBlackHeight, rightBlackHeight, node.toString());
        }
        if (node.color == RED) {
            checkRedNodeRule(node);
            return leftBlackHeight;
        }
        return leftBlackHeight + 1;
    }

    private void checkRedNodeRule(Node node) throws NotBalancedTreeException {
        if (node.left != null && node.left.color != BLACK) {
            throw new NotBalancedTreeException("If a node is red, then left child must be black.\n" + node.toString());
        }
        if (node.right != null && node.right.color != BLACK) {
            throw new NotBalancedTreeException("If a node is red, then right child must be black.\n" + node.toString());
        }
    }

    enum Color {
        RED, BLACK
    }

    static final class Node<E> {
        E value;
        Node<E> left, right, parent;
        Color color = BLACK;

        Node(E value, Color color) {
            this.value = value;
            this.color = color;
        }

        Node(E value, Color color, Node<E> parent) {
            this.value = value;
            this.color = color;
            this.parent = parent;
        }

        @Override
        public String toString() {
            assert !(left == null || right == null);
            return "Node{" + "value=" + value +
                    ", left=" + left +
                    ", right=" + right +
                    ", color=" + color + "}";
        }
    }

    private Node<E> bro(@Nullable Node<E> n) {
        return (n == null || n.parent == null) ? null : (n == n
                .parent.left) ? n.parent.right
                : n.parent.left;
    }
}