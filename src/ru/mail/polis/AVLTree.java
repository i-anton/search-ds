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
            root = new Node(value, null);
            size++;
            return true;
        }

        Node curr = root;
        while (true) {
            Node parent = curr;
            int cmp = compare(curr.value, value);
            if (cmp == 0) {
                return false;
            }
            curr = (cmp > 0) ? curr.left : curr.right;
            if (curr == null) {
                if (cmp > 0) {
                    parent.left = new Node(value, parent);
                } else  {
                    parent.right = new Node(value, parent);
                }
                balance(parent);
                break;
            }
        }
        size++;
        return true;
    }


    private int height(Node curr) {
        if (curr == null) {
            return 0;
        } else {
            return curr.height;
        }
    }

    private Node rotateLeft(Node curr) {
        Node rightNode = curr.right;
        rightNode.parent = curr.parent;
        curr.right = rightNode.left;
        if (curr.right != null) {
            curr.right.parent = curr;
        }
        rightNode.left = curr;
        rightNode.left.parent = rightNode;

        if (rightNode.parent != null) {
            if (rightNode.parent.right == curr) {
                rightNode.parent.right = rightNode;
            } else {
                rightNode.parent.left = rightNode;
            }
        }

        curr.height = (height(curr.left) > height(curr.right) ? height(curr.left) : height(curr.right)) + 1;
        rightNode.height = (height(rightNode.left) > height(rightNode.right)
                ? height(rightNode.left)
                : height(rightNode.right)) + 1;
        return rightNode;
    }

    private Node rotateRight(Node curr) {
        Node leftNode = curr.left;
        leftNode.parent = curr.parent;
        curr.left = leftNode.right;
        if (curr.left != null) {
            curr.left.parent = curr;
        }
        leftNode.right = curr;
        leftNode.right.parent = leftNode;
        if (leftNode.parent != null) {
            if (leftNode.parent.right == curr) {
                leftNode.parent.right = leftNode;
            } else {
                leftNode.parent.left = leftNode;
            }
        }

        curr.height = (height(curr.left) > height(curr.right) ? height(curr.left) : height(curr.right)) + 1;
        leftNode.height = (height(leftNode.left) > height(leftNode.right)
                ? height(leftNode.left)
                : height(leftNode.right)) + 1;
        return leftNode;
    }

    private Node rotateLeftThenRight(Node curr) {
        curr.left = rotateLeft(curr.left);
        return rotateRight(curr);
    }

    private Node rotateRightThenLeft(Node curr) {
        curr.right = rotateRight(curr.right);
        return rotateLeft(curr);
    }

    private void balance(Node curr) {
        curr.height = (height(curr.left) > height(curr.right) ? height(curr.left) : height(curr.right)) + 1;
        if ((height(curr.right) - height(curr.left)) == 2) {
            if ((height(curr.right.right) - height(curr.right.left)) < 0) {
                curr = rotateRightThenLeft(curr);
            } else {
                curr = rotateLeft(curr);
            }
        } else if ((height(curr.right) - height(curr.left)) == -2) {
            if ((height(curr.left.right) - height(curr.left.left)) > 0) {
                curr = rotateLeftThenRight(curr);
            } else {
                curr = rotateRight(curr);
            }
        }

        if (curr.parent != null) {
            balance(curr.parent);
        } else {
            root = curr;
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
        if (root == null) {
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
        size--;
        remove(curr);
        return true;
    }

    private void remove(Node curr) {
        if (curr.left == null && curr.right == null) {
            if (curr.parent == null) {
                root = null;
            } else {
                Node parent = curr.parent;
                if (parent.left == curr) {
                    parent.left = null;
                } else {
                    parent.right = null;
                }
                balance(parent);
            }
            return;
        }
        if (curr.left != null) {
            Node child = curr.left;
            while (child.right != null) {
                child = child.right;
            }
            curr.value = child.value;
            remove(child);
        } else {
            Node child = curr.right;
            while (child.left != null) {
                child = child.left;
            }
            curr.value = child.value;
            remove(child);
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
     *
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

    private class Node {

        E value;
        int height = 1;
        Node left;
        Node right;
        Node parent;

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
