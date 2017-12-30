package ru.mail.polis;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;

import com.sun.org.apache.regexp.internal.RE;

public class RedBlackTree<E extends Comparable<E>> extends AbstractSet<E> implements BalancedSortedSet<E> {

    private final Comparator<E> comparator;
    private Node root; //todo: Создайте новый класс если нужно. Добавьте новые поля, если нужно.
    private int size;
    //todo: добавьте дополнительные переменные и/или методы если нужно
    private Node<E> emptyNode = new Node<>();
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
        Node<E> node, parent = emptyNode;
        for (node = root; node != emptyNode && node != null; )
        {
            parent=node;
            if (compare(value, node.value) < 0)
                node = node.left;
            else if (compare(value, node.value) > 0)
                node = node.right;
            else
                return false;
        }
        node = new Node<>(value, Color.RED);
        node.parent = parent;
        node.left = node.right = emptyNode;
        if (parent != emptyNode)
        {
            if (compare(value, parent.value) < 0)
                parent.left = node;
            else
                parent.right = node;

        } else
        {
            root = node;
        }
        balance(node);
        size++;
        return true;
    }

    private void fixRemove(Node node)
    {
        Node temp;
        while(node != root && node.color == Color.BLACK) {
            if(node == node.parent.left) {
                temp = node.parent.right;

                if(temp.color == Color.RED) {
                    temp.color = Color.BLACK;
                    node.parent.color = Color.RED;
                    leftRotate(node.parent);
                    temp = node.parent.right;
                }
                if(temp.left.color == Color.BLACK && temp.right.color == Color.BLACK) {
                    temp.color = Color.RED;
                    node = node.parent;
                }
                else {
                    if(temp.right.color == Color.BLACK) {
                        temp.left.color = Color.BLACK;
                        temp.color = Color.RED;
                        rightRotate(temp);
                        temp = node.parent.right;
                    }
                    temp.color = node.parent.color;
                    node.parent.color = Color.BLACK;
                    temp.right.color = Color.BLACK;
                    leftRotate(node.parent);
                    node = root;
                }
            }
            else {
                temp = node.parent.left;
                if(temp.color == Color.RED) {
                    temp.color = Color.BLACK;
                    node.parent.color = Color.RED;
                    rightRotate(node.parent);
                    temp = node.parent.left;
                }
                if(temp.left.color == Color.BLACK && temp.right.color == Color.BLACK) {
                    temp.color = Color.RED;
                    node = node.parent;
                }
                else {
                    if( temp.left.color == Color.BLACK) {
                        temp.right.color = Color.BLACK;
                        temp.color = Color.RED;;
                        leftRotate(temp);
                        temp = node.parent.left;
                    }
                    temp.color = node.parent.color;
                    node.parent.color = Color.BLACK;
                    temp.left.color = Color.BLACK;

                    rightRotate(node.parent);
                    node = root;
                }
            }
        }
        node.color = Color.BLACK;
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
        Node<E> curr = root;
        while ( curr != emptyNode && curr != null)
        {
            if (compare(value, curr.value) < 0)
                curr = curr.left; else
            if (compare(value, curr.value) > 0)
                curr = curr.right; else
            if (compare(value, curr.value) == 0)
                break;
        }
        if (curr != emptyNode && compare(value, curr.value) == 0) {
            Node temp = emptyNode, successor = emptyNode;
            if (curr == null || curr == emptyNode)
                return false;
            if (curr.isLeftFree() || curr.isRightFree())
                successor = curr;
            else
                successor = curr.getSuccessor();

            if (!successor.isLeftFree())
                temp = successor.left;
            else
                temp = successor.right;

            temp.parent = successor.parent;
            if (successor.isParentFree())
                root = temp;
            else if (successor == successor.parent.left)
                successor.parent.left = temp;
            else
                successor.parent.right = temp;
            if (successor != curr)
                curr.value = (E) successor.value;

            if (successor.color == Color.BLACK)
            {
                fixRemove(temp);
            }
            size--;
            return true;

        }
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
        Node<E> curr = root;
        while (curr != emptyNode && curr != null)
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
     * @return Возвращает наименьший элемент в дереве
     * @throws NoSuchElementException если дерево пустое
     */
    @Override
    public E first() {
        if (root == null || root == emptyNode) {
            throw new NoSuchElementException("first");
        }
        Node curr = root;
        while (!curr.isLeftFree())
        {
            curr = curr.left;
        }
        return (E) curr.value;
    }

    /**
     * Ищет наибольший элемент в дереве
     * @return Возвращает наибольший элемент в дереве
     * @throws NoSuchElementException если дерево пустое
     */
    @Override
    public E last() {
        if (root == null || root == emptyNode) {
            throw new NoSuchElementException("first");
        }
        Node curr = root;
        while (!curr.isRightFree())
        {
            curr = curr.right;
        }
        return (E) curr.value;
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

    private Node leftRotate(Node<E> node)
    {
        Node right = node.right;
        node.right = right.left;

        if (right.left != emptyNode)
            right.left.parent = node;

        if (right != emptyNode)
            right.parent = node.parent;

        if (node.parent != emptyNode)
        {
            if (node == node.parent.left)
                node.parent.left = right;
            else
                node.parent.right = right;

        } else
            root = right;
        right.left = node;
        if (node != emptyNode)
            node.parent = right;
        return root;
    }


    private Node rightRotate(Node<E> node)
    {
        Node left = node.left;
        node.left = left.right;
        if (left.right != emptyNode)
            left.right.parent = node;
        if (left != emptyNode)
            left.parent = node.parent;
        if (node.parent != emptyNode)
        {
            if (node == node.parent.right)
                node.parent.right = left;
            else
                node.parent.left = left;

        } else
            root = left;
        left.right = node;
        if (node != emptyNode)
            node.parent = left;
        return root;
    }

    public void preOrder(){
        preOrder(root);
    }

    private void preOrder(Node v)
    {
        if (v == emptyNode || v == null) {
            System.out.println("emptyNode ");
        } else
        {
            System.out.println(v.value + " ");
            preOrder(v.left);
            preOrder(v.right);
        }

    }

    private void balance(Node node)
    {
        Node uncle;
        while (node != root && node.parent.color == Color.RED)
        {
            if (node.parent == node.parent.parent.left)
            {
                uncle = node.parent.parent.right;
                if (uncle.color == Color.RED)
                {
                    node.parent.color = Color.BLACK;
                    uncle.color = Color.BLACK;
                    node.parent.parent.color = Color.RED;
                    node = node.parent.parent;
                } else {
                    if (node == node.parent.right)
                    {
                        node = node.parent;
                        root = leftRotate(node);
                    }
                    node.parent.color = Color.BLACK;
                    node.parent.parent.color = Color.RED;
                    root = rightRotate(node.parent.parent);
                }
            } else
            {
                uncle = node.parent.parent.left;
                if (uncle.color == Color.RED)
                {
                    node.parent.color = Color.BLACK;
                    uncle.color = Color.BLACK;
                    node.parent.parent.color = Color.RED;
                    node = node.parent.parent;
                } else {
                    if (node == node.parent.left)
                    {
                        node = node.parent;
                        root = rightRotate(node);
                    }
                    node.parent.color = Color.BLACK;
                    node.parent.parent.color = Color.RED;
                    root = leftRotate(node.parent.parent);
                }
            }
        }
        root.color = Color.BLACK;
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

   public class Node<E> {
        E value;
        Node<E> left;
        Node<E> right;
        Node<E> parent;
        Color color = Color.BLACK;

        public Node(){}
        public Node(E value, Color color)
        {
            this.value = value;
            this.color = color;
        }


        public boolean isFree() {
            return value == null || value == emptyNode;
        }

        public boolean isLeftFree() {
            return left == null || left == emptyNode;
        }

        public boolean isRightFree() {
            return right == null || right == emptyNode;
        }

        public boolean isParentFree() {
            return parent == null || parent == emptyNode;
        }

       public Node getSuccessor()
       {
           Node temp = null;
           Node node = this;
           if(!node.isRightFree()) {
               temp = node.right;
               while(!temp.isLeftFree())
                   temp = temp.left;
               return temp;
           }
           temp = node.parent;
           while(temp != emptyNode && node == temp.right) {
               node = temp;
               temp = temp.parent;
           }
           return temp;
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
