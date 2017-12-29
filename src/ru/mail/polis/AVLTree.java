package ru.mail.polis;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;

public class AVLTree<E extends Comparable<E>> extends AbstractSet<E> implements BalancedSortedSet<E> {

    private final Comparator<E> comparator;

    private Node root; //todo: Создайте новый класс если нужно. Добавьте новые поля, если нужно.
    private int size;
    //todo: добавьте дополнительные переменные и/или методы если нужно

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
        if (root == null) {
            root = new Node(value);
            size++;
            return true;
        }
        try{
            root = add(root, value);
            return true;
        } catch (NodeAlreadyExistsException e)
        {
            return false;
        }
    }

    private Node add(Node v,E value) throws NodeAlreadyExistsException{
        if (v == null) {
            v = new Node(value);
            size++;
            return v;
        }
        if (compare(v.value, value) == 0) throw new NodeAlreadyExistsException();
        if (compare(value, v.value) < 0) {
            v.left = add(v.left, value);
        }
        else {
            v.right = add(v.right, value);
        }
        return balance(v);
    }



    private Node findMin(Node v)
    {
        Node curr = v;
        while (curr.left != null)
        {
            curr = curr.left;
        }
        return curr;
    }

    private Node removeMin(Node v)
    {
        if (v.left == null)
            return v.right;
        v.left = removeMin(v.left);
        return balance(v);
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
        try
        {
            root = remove(root, value);
            size--;
            return true;
        } catch (NoSuchElementException e)
        {
            return false;
        }
    }

    private Node remove(Node v, E value) throws NoSuchElementException
    {
        if (v == null)
            throw new NoSuchElementException();
        if (compare(value, v.value) < 0)
        {
            v.left = remove(v.left, value);
        }  else if(compare(value, v.value) > 0)
        {
            v.right = remove(v.right, value);
        } else {
            Node left = v.left;
            Node right = v.right;
            v = null;
            if (right == null) return left;
            Node min = findMin(right);
            min.right = removeMin(right);
            min.left = left;
            return balance(min);
        }
        return balance(v);
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
        Node curr = root;
        while (curr != null)
        {
            if (compare(curr.value, value)== 0)
                return true;
            if (compare(value, curr.value) < 0)
                curr = curr.left;
            else
                curr = curr.right;
        }
        return false;
    }


    /**
     * Ищет наименьший элемент в дереве
     *
     * @return Возвращает наименьший элемент в дереве
     * @throws NoSuchElementException если дерево пустое
     */
    @Override
    public E first() {
        if (root == null) {
            throw new NoSuchElementException("first");
        }
        return findMin(root).value;
    }

    /**
     * Ищет наибольший элемент в дереве
     *
     * @return Возвращает наибольший элемент в дереве
     * @throws NoSuchElementException если дерево пустое
     */
    @Override
    public E last() {
        if (root == null) {
            throw new NoSuchElementException("first");
        }
        Node curr = root;
        while (curr.right != null)
            curr = curr.right;
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

    /**
     * Правый поворот вокруг v
     * @param v
     */
    private Node rightRotate(Node v)
    {
        if (v == null) return v;
        Node x = v.left;
        v.left = x.right;
        x.right = v;
        fixBalanceFactor(v);
        fixBalanceFactor(x);
        return x;
    }

    /**
     * Левый поворот вокруг v
     * @param v
     */
    private Node leftRotate(Node v)
    {
        if (v == null) return  v;
        Node x = v.right;
        v.right = x.left;
        x.left = v;
        fixBalanceFactor(v);
        fixBalanceFactor(x);
        return x;
    }

    private int balanceFactor(Node curr)
    {
        if (curr == null) return 0;
        int leftHeight = curr.left == null ? 0 : curr.left.height;
        int rightHeight = curr.right == null ? 0 : curr.right.height;
        return rightHeight - leftHeight;
    }

    private void fixBalanceFactor(Node curr)
    {
        int leftHeight = curr.left == null ? 0 : curr.left.height;
        int rightHeight = curr.right == null ? 0 : curr.right.height;
        curr.height = (leftHeight > rightHeight ? leftHeight : rightHeight) + 1;
    }

    public void preOrder(){
        preOrder(root);
    }

    private void preOrder(Node v)
    {
        if (v == null) {
            System.out.println("null ");
        } else
        {
            System.out.println(v.value + " ");
            preOrder(v.left);
            preOrder(v.right);
        }

    }

    /**
     * Балансировка узла v
     * @param v
     */
    private Node balance(Node v)
    {
        fixBalanceFactor(v);
        if (balanceFactor(v) == 2)
        {
            if (balanceFactor(v.right) < 0)
                v.right = rightRotate(v.right);
            return leftRotate(v);

        }
        if (balanceFactor(v) == -2)
        {
            if (balanceFactor(v.left) > 0)
               v.left = leftRotate(v.left);
           return rightRotate(v);

        }
        return v;
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
        Node left;
        Node right;
        int height; //balance factor
        Node(E value) {
            this.value = value;
            height = 1;
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
