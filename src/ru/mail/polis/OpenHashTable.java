package ru.mail.polis;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

public class OpenHashTable<E extends OpenHashTableEntity> extends AbstractSet<E> implements Set<E> {

    private int INIT_CAPACITY = 8;
    private final double GROW_POWER = 0.5;
    @SuppressWarnings("unchecked")
    private final E DELETED = (E) (OpenHashTableEntity) (tableSize, probId) -> 0;

    private E[] array;
    private int size; //количество элементов в хеш-таблице

    @SuppressWarnings("unchecked")
    public OpenHashTable() {
        array = (E[]) new OpenHashTableEntity[INIT_CAPACITY];
    }
    @SuppressWarnings("unchecked")
    private OpenHashTable(int capacity) {
        INIT_CAPACITY = capacity;
        array = (E[]) new OpenHashTableEntity[INIT_CAPACITY];
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
        if (((double)size()/getTableSize()) >= GROW_POWER) {
            grow();
        }
        int probId = 0;
        int hash = value.hashCode(getTableSize(), probId);
        if (value.equals(array[hash])) {
            return false;
        }
        while ((array[hash] != null) || DELETED.equals(array[hash])) {
            probId++;
            hash = value.hashCode(getTableSize(), probId);
            if (value.equals(array[hash])) {
                return false;
            }
        }
        array[hash] = value;
        size++;
        return true;
    }

    private void grow() {
        OpenHashTable<E> newTable = new OpenHashTable<>(getTableSize() * 2);
        for (E anArray : array) {
            if (anArray != null) {
                newTable.add(anArray);
            }
        }
        array = newTable.array;
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
        int ind = find(value);
        if (ind == -1) {
            return false;
        }
        array[ind] = DELETED;
        size--;
        return true;
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
        return find(value) != -1;
    }

    private int find(E value) {
        int probId = 0;
        int hash = value.hashCode(getTableSize(), probId);
        while (!value.equals(array[hash])) {
            probId++;
            if (probId == getTableSize()) {
                return -1;
            }
            hash = value.hashCode(getTableSize(), probId);
        }
        return hash;
    }


    @Override
    public int size() {
        return size;
    }

    public int getTableSize() {
        return array.length;
    }

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }
}
