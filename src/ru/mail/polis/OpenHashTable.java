package ru.mail.polis;

import java.util.*;


public class OpenHashTable<E extends OpenHashTableEntity> extends AbstractSet<E> implements Set<E> {

    private static final int INITIAL_CAPACITY = 8;
    private int size; //количество элементов в хеш-таблице
    private E[] table;
    private boolean[] deleted;

    public OpenHashTable() {
        this(INITIAL_CAPACITY);
    }
    @SuppressWarnings("unchecked")
    private OpenHashTable(int capacity) {
        table = (E[]) new OpenHashTableEntity[capacity];
        deleted = new boolean[capacity];
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
        for (int i = 1; i < table.length; i++) {
            if (value.equals(table[idx]) && !deleted[idx]) return false;
            if (table[idx] == null || deleted[idx]) break;
            idx = value.hashCode(table.length, i);
        }
        table[idx] = value;
        deleted[idx] = false;
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
        for (int i = 1; i < table.length; i++) {
            if (table[idx] != null) {
                if (table[idx].equals(value) && !deleted[idx]) {
                    deleted[idx] = true;
                    size--;
                    return true;
                }
            } else return false;
            idx = value.hashCode(table.length, i);
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
        for (int i = 1; i < table.length; i++) {
            if (table[idx] != null) {
                if (table[idx].equals(value) && !deleted[idx]) {
                    return true;
                }
            } else return false;
            idx = value.hashCode(table.length, i);
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

    private void resize() {
        float loadFactor = (float) size / table.length;
        if (loadFactor < 0.5f) return;
        OpenHashTable<E> newTable = new OpenHashTable<>(table.length * 2);
        ArrayList<E> objects = new ArrayList<>(size);
        for (E entry : table)
            if (entry != null)
                objects.add(entry);

        newTable.addAll(objects);
        table = newTable.table;
        deleted = newTable.deleted;
    }
}
