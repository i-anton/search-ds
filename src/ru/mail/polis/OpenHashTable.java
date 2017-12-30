package ru.mail.polis;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

public class OpenHashTable<E extends OpenHashTableEntity> extends AbstractSet<E> implements Set<E> {
    private final int INITIAL_CAPACITY = 8;
    private int size; //количество элементов в хеш-таблице
    private E[] table;
    private boolean[] deleted;

    public OpenHashTable() {
        //todo
        size = 0;
        table =(E[]) new OpenHashTableEntity[INITIAL_CAPACITY];
        deleted = new boolean[INITIAL_CAPACITY];
    }


    private void resize() {
        if (size * 2 < table.length) {
            return;
        }
        E[] old = this.table;
        size = 0;
        int newLen = table.length << 1;
        table =(E[]) new OpenHashTableEntity[newLen];
        deleted = new boolean[newLen];
        for (int i = 0; i < old.length; i++) {
            E node =  old[i];
            if (node != null) {
                old[i] = null;
                add(node);
            }
        }
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
        int hash = value.hashCode(table.length, 0);
        if (table[hash] == null) {
            table[hash] = value;
        } else
        {
            int probe =  1;
            while (table[hash] != null && !value.equals(table[hash]) && !deleted[hash]) {
                hash = value.hashCode(table.length, probe++);
            }
            if (value.equals(table[hash]))
            {
                return false;
            }
            deleted[hash] = false;
            table[hash] = value;
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
        int hash = value.hashCode(table.length, 0);
        if (value.equals(table[hash]))
        {
            deleted[hash] = true;
            table[hash] = null;
            size--;
            return true;
        }
        int probe = 1;
        while (table[hash] != null && !value.equals(table[hash]) || deleted[hash]) {
            hash = value.hashCode(table.length, probe++);
        }
        if (value.equals(table[hash]))
        {
            table[hash] = null;
            deleted[hash] = true;
            size--;
            return true;
        }
        resize();
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
        int hash = value.hashCode(table.length, 0);
        if (value.equals(table[hash]))
            return true;
        int probe = 1;
        while (table[hash] != null && !value.equals(table[hash]) || deleted[hash]) {
            hash = value.hashCode(table.length, probe++);
        }
        if (table[hash] != null && value.equals(table[hash]))
            return true;
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

}
