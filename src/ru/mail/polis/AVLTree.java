package ru.mail.polis;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;

public class AVLTree<E extends Comparable<E>> extends AbstractSet<E> implements BalancedSortedSet<E> {

    private final Comparator<E> comparator;

    private Node root;
    private int size;

    public AVLTree() {
        this(null);
    }

    public AVLTree(Comparator<E> comparator) {
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
        if (root == null){
            root = new Node(value);
            ++size;
            return true;
        }
        Node curr = root;
        Node parent = curr;
        int comp = 0;
        // спуск вниз для поиска элемента
        while(curr != null){
            parent = curr;
            comp = compare(curr.value, value);
            if (comp == 0) return false;
            curr = (comp < 0)? curr.right : curr.left;
        }
        // вставка
        if (comp < 0){
            parent.right = new Node(value,parent);
        } else {
            parent.left = new Node(value,parent);
        }
        ++size;
        balance(parent);
        return true;
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
        if (root == null) return false;
        Node child = root;
        // спускаемся для поиска элемента
        while (child!=null) {
            Node curr = child;
            int comp = compare(curr.value, value);
            if (comp == 0){
                //элемент найден
                remove(curr);
                --size;
                return true;
            } else {
                child = (comp < 0)? curr.right : curr.left;
            }
        }
        return false;
    }

    /**
     * Рекурсивное удаление элемента
     * @param node - элемент
     */
    private void remove(Node node) {
        // если лист
        if (node.left == null && node.right == null) {
            if (node.parent == null) {
                root = null;
            } else {
                Node parent = node.parent;
                // удаляем связь у родителя
                if (parent.left != node) {
                    parent.right = null;
                } else {
                    parent.left = null;
                }
                balance(parent);
            }
            return;
        }
        Node child;
        if (node.left != null) {
            child = node.left;
            while (child.right != null) child = child.right;
        } else {
            child = node.right;
            while (child.left != null) child = child.left;
        }
        node.value = child.value;
        remove(child);
    }

    private void balance(Node node) {
        setDiff(node);
        if (node.diff == -2) {
            if (getHeight(node.left.left) < getHeight(node.left.right))
                node.left = rotateLeft(node.left);
            node = rotateRight(node);
        } else if (node.diff == 2) {
            if (getHeight(node.right.right) < getHeight(node.right.left))
                node.right = rotateRight(node.right);
            node = rotateLeft(node);
        }
        if (node.parent != null) {
            balance(node.parent);
        } else {
            root = node;
        }
    }
    private Node rotateLeft(Node node) {
        Node right = node.right;
        right.parent = node.parent;
        node.right = right.left;
        right.left = node;
        node.parent = right;
        if (node.right != null)
            node.right.parent = node;
        if (right.parent != null) {
            if (right.parent.right == node) {
                right.parent.right = right;
            } else {
                right.parent.left = right;
            }
        }
        setDiff(node);
        setDiff(right);
        return right;
    }

    private Node rotateRight(Node node) {
        Node left = node.left;
        left.parent = node.parent;
        node.left = left.right;
        left.right = node;
        node.parent = left;
        if (node.left != null)
            node.left.parent = node;
        if (left.parent != null) {
            if (left.parent.right == node) {
                left.parent.right = left;
            } else {
                left.parent.left = left;
            }
        }
        setDiff(node);
        setDiff(left);
        return left;
    }

    /**
     * Получение высоты элемента
     * @param node - элемент
     * @return высота
     */
    private int getHeight(Node node) {
        return (node != null) ? node.height : -1;
    }

    /**
     * Установка разницы высот и высоты
     * @param node - элемент
     */
    private void setDiff(Node node) {
        node.height = 1+Math.max(getHeight(node.left), getHeight(node.right));
        node.diff = getHeight(node.right) - getHeight(node.left);
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
        if (root == null) return false;
        Node curr = root;
        while (curr != null){
            int comp = compare(curr.value, value);
            if (comp == 0) return true;
            curr = (comp < 0)? curr.right : curr.left;
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
        if (size == 0) {
            throw new NoSuchElementException("first");
        }
        Node curr = root;
        while ((curr.left)!= null){
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
        if (size == 0) {
            throw new NoSuchElementException("last");
        }
        Node curr = root;
        while ((curr.right)!= null){
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
        return "AVLTree{" +
                "tree=" + root +
                "size=" + size + ", " +
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
     * Обходит дерево и проверяет что высоты двух поддеревьев
     * различны по высоте не более чем на 1
     *
     * @throws NotBalancedTreeException если высоты отличаются более чем на один
     */
    @Override
    public void checkBalanced() throws NotBalancedTreeException {
        traverseTreeAndCheckBalanced(root);
    }

    private int traverseTreeAndCheckBalanced(Node curr) throws NotBalancedTreeException {
        if (curr == null) {
            return 1;
        }
        int leftHeight = traverseTreeAndCheckBalanced(curr.left);
        int rightHeight = traverseTreeAndCheckBalanced(curr.right);
        if (Math.abs(leftHeight - rightHeight) > 1) {
            throw NotBalancedTreeException.create("The heights of the two child subtrees of any node must be differ by at most one",
                    leftHeight, rightHeight, curr.toString());
        }
        return Math.max(leftHeight, rightHeight) + 1;
    }

    class Node {
        E value;
        Node left, right, parent;
        int height, diff;
        Node(E value) {
            this.value = value;
        }
        Node(E value, Node parent) {
            this.value = value;
            this.parent = parent;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("N{");
            sb.append("d=").append(value);
            if (left != null) {
                sb.append(", l=").append(left);
            }
            if (right != null) {
                sb.append(", r=").append(right);
            }
            sb.append('}');
            return sb.toString();
        }
    }
}
