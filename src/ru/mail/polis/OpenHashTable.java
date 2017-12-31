package ru.mail.polis;

import java.util.*;
import java.util.stream.Collectors;

public class OpenHashTable<E extends OpenHashTableEntity> extends AbstractSet<E> implements Set<E> {

    private final int INITIAL_CAPACITY = 8;
    private int size; //количество элементов в хеш-таблице
    private E[] table;
    private final E DELETED = (E) (OpenHashTableEntity) (tableSize, probId) -> 0;

    public OpenHashTable() {
        table = (E[]) new OpenHashTableEntity[INITIAL_CAPACITY];
    }

    private OpenHashTable(int capacity) {
        table = (E[]) new OpenHashTableEntity[capacity];
    }

    /**
     * Вставляет элемент в хеш-таблицу.
     * Инвариант: на вход всегда приходит NotNull объект, который имеет корректный тип
     *
     * @param value элемент который необходимо вставить
     * @return true, если элемент в хеш-таблице отсутствовал
     */
    @Override
    public boolean add(E value) {
        int idx = value.hashCode(table.length, 0);
        if (empty(idx)) {
            table[idx] = value;
        } else {
            if (value.equals(table[idx])) return false;
            for (int i = 1; i < table.length && !empty(idx); i++) {
                idx = value.hashCode(table.length, i);
                if (value.equals(table[idx])) {
                    return false;
                }
            }
            table[idx] = value;
        }
        size++;
        resize();
        return true;
    }

    /**
     * Удаляет элемент с таким же значением из хеш-таблицы.
     * Инвариант: на вход всегда приходит NotNull объект, который имеет корректный тип
     *
     * @param object элемент который необходимо вставить
     * @return true, если элемент содержался в хеш-таблице
     */
    @Override
    public boolean remove(Object object) {
        @SuppressWarnings("unchecked")
        E value = (E) object;
        int idx = value.hashCode(table.length, 0);
        if (!empty(idx)) {
            if (value.equals(table[idx])) {
                table[idx] = DELETED;
                size--;
                return true;
            }
        }
        for (int i = 1; i < table.length; i++) {
            idx = value.hashCode(table.length, i);
            if (value.equals(table[idx])) {
                table[idx] = DELETED;
                size--;
                return true;
            }
        }
        return false;
    }

    /**
     * Ищет элемент с таким же значением в хеш-таблице.
     * Инвариант: на вход всегда приходит NotNull объект, который имеет корректный тип
     *
     * @param object элемент который необходимо поискать
     * @return true, если такой элемент содержится в хеш-таблице
     */
    @Override
    public boolean contains(Object object) {
        @SuppressWarnings("unchecked")
        E value = (E) object;
        int idx = value.hashCode(table.length, 0);
        if (!empty(idx)) {
            if (value.equals(table[idx])) {
                return true;
            }
        }
        for (int i = 1; i < table.length; i++) {
            idx = value.hashCode(table.length, i);
            if (value.equals(table[idx])) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    public int getTableSize() {
        return table.length;
    }

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }

    private boolean empty(int idx) {
        return table[idx] == null || table[idx] == DELETED;
    }

    private void resize() {
        float loadFactor = (float) size / table.length;
        if (loadFactor >= 0.5f) {
            OpenHashTable<E> newTable = new OpenHashTable<>(table.length * 2);
            newTable.addAll(Arrays.stream(table).filter(Objects::nonNull).collect(Collectors.toList()));
            table = newTable.table;
        }
    }

    public static void main(String[] args) {
        OpenHashTable<Student> table = new OpenHashTable<>();
        Student[] students = new Student[20];
        for (int i = 0; i < students.length; i++) {
            students[i] = SimpleStudentGenerator.getInstance().generate();
            table.add(students[i]);
        }
        System.out.println(table.size());
        System.out.println("5 contains = " + table.contains(students[5]));
        System.out.println(table.remove(students[5]));
        System.out.println("5 contains = " + table.contains(students[5]));
        System.out.println(table.remove(students[5]));
        System.out.println("3 add = " + table.add(students[3]));
        for (int i = 0; i < students.length; i++) {
            System.out.println(table.contains(students[i]));
        }
    }
}
