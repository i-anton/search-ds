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
        if (root == null) {
            root = new Node(value);
            size++;
            return true;
        } else {
            try {
                root = add(root,value);
                return true;
            } catch (RuntimeException e) {
                return false;
            }
        }
    }

    /**
     * Вставляет элемент в поддерево с корнем node
     * @param node корень поддерева, в которое необходимо вставить элемент
     * @param value элемент который необходимо вставить
     * @return корень полученного дерева
     * @throws RuntimeException если вставляемый элемент уже существует в дереве
     */
    private Node add(Node node, E value) throws RuntimeException {
        if (node == null) {
            node = new Node(value);
            size++;
            return node;
        } else {
            int cmp = compare(node.value,value);

            if (cmp > 0) {
                node.left = add(node.left,value);
            } else if (cmp < 0) {
                node.right = add(node.right,value);
            } else {
                throw new RuntimeException("Node already exists");
            }

            return balance(node);
        }
    }

    /**
     * Удаляет элемент с таким же значением из дерева.
     * Инвариант: на вход всегда приходит NotNull объект, который имеет корректный тип
     *
     * @param object элемент который необходимо удалить
     * @return true, если элемент содержался в дереве
     */
    @Override
    public boolean remove(Object object) {
        @SuppressWarnings("unchecked")
        E value = (E) object;

        try {
            root = remove(root,value);
            size--;
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Удаляет элемент с таким же значением из поддерева с корнем node.
     * @param node корень поддерева, в котором нужно удалить заданный элемент
     * @param value элемент который необходимо удалить
     * @return корень полученного поддерева
     * @throws NoSuchElementException если в поддереве нет элемента, который необходимо удалить
     */
    private Node remove(Node node, E value) throws NoSuchElementException {
        if (node == null) {
            throw new NoSuchElementException();
        } else {
            int cmp = compare(node.value,value);

            if (cmp < 0) {
                node.right = remove(node.right,value);
            } else if (cmp > 0) {
                node.left = remove(node.left,value);
            } else {
                Node leftNode = node.left;
                Node rightNode = node.right;
                node = null;
                if (rightNode == null) {
                    return leftNode;
                }
                Node minNode = findMin(rightNode);
                minNode.right = removeMin(rightNode);
                minNode.left = leftNode;
                return balance(minNode);
            }

            return balance(node);
        }
    }

    /**
     * Удаляет наименьший элемент из заданного дерева
     * @param node корень заданного поддерева
     * @return возвращает правый узел наименьшего элемента
     */
    private Node removeMin(Node node) {
        if (node.left == null) {
            return  node.right;
        } else {
            node.left = removeMin(node.left);
            return balance(node);
        }
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
            Node curr = root;
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
            Node min = root;
            while (min.left != null) {
                min = min.left;
            }
            return  min.value;
        }
    }

    /**
     * Ищет узел с наименьшем элементом в поддереве с корнем node
     * @param node корень поддерева, в котором ищем узел с наименьшим элементом
     * @return узел с наименьшим элементом
     */
    private Node findMin(Node node) {
        return node.left != null ? findMin(node.left) : node;
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
            Node max = root;
            while (max.right != null) {
                max = max.right;
            }
            return max.value;
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

    /**
     * Выполняет балансировку дерева
     * @param node узел, который мы балансируем
     * @return возвращает корень полученного дерева
     */
    private Node balance(Node node) {
        fixHeight(node);

        if (balanceFactor(node) == 2) {
            if (balanceFactor(node.right) < 0) {
                node.right = rotateRight(node.right);
            }
            return rotateLeft(node);
        }

        if (balanceFactor(node) == -2) {
            if (balanceFactor(node.left) > 0) {
                node.left = rotateLeft(node.left);
            }
            return rotateRight(node);
        }

        return node; //Если балансировка не нужна
    }

    /**
     * реализует малый левый поворот вокруг заданного узла.
     * @param node узел, относительно которого происходит поворот
     * @return возвращает корень полученного дерева
     */
    private Node rotateLeft(Node node) {
        Node t =  node.right;
        node.right = t.left;
        t.left = node;
        fixHeight(node);
        fixHeight(t);
        return t;
    }

    /**
     * реализует малый правый поворот вокруг заданного узла.
     * @param node узел, относительно которого происходит поворот
     * @return возвращает корень полученного поддерева
     */
    private Node rotateRight(Node node) {
        Node t =  node.left;
        node.left = t.right;
        t.right = node;
        fixHeight(node);
        fixHeight(t);
        return t;
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

    /**
     * Обертка для поля height.
     * @param node узел, у которого нужно узнать высоту
     * @return если узел пустой возвращает 0, иначе высоту этого узла
     */
    private int height(Node node) {
        return node == null ? 0 : node.height;
    }

    /**
     * Находит разницу высот правого и левого поддеревьев (balance factor)
     * @param node узел, у которого необходимо найти balance factor
     * @return возвращает разницу высот правого и левого поддеревьев
     */
    private int balanceFactor(Node node) {
        return height(node.right)-height(node.left);
    }

    /**
     * Восстанавливает корректное значение поля height заданного узла (при условии,
     * что значения этого поля в правом и левом дочерних узлах являются корректными
     * @param node узел, у которого нужно восстановить корректное значение поля height
     */
    private void fixHeight(Node node) {
        int leftHeight = height(node.left);
        int rightHeight = height(node.right);
        node.height = Math.max(leftHeight,rightHeight)+1;
    }

    private class Node {
        E value;
        Node left;
        Node right;
        int height;
        Node(E value) {
            this.value = value;
            this.height = 1;
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




