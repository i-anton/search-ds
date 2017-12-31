package ru.mail.polis;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

public class OpenHashTable<E extends OpenHashTableEntity> extends AbstractSet<E> implements Set<E> {

    private static final int DEFAULT_SIZE = 8;
    private static final int DEFAULT_INCREASE_MULTIPLIER = 2;
    private E[] array;
    private int size; //количество элементов в хеш-таблице
    private int tableSize;//размер хещ-таблицы todo: измените на array.length

    public OpenHashTable() {
        //todo
        array = (E[]) new OpenHashTableEntity[DEFAULT_SIZE];
        size = 0;
        tableSize = array.length;
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
        //todo: следует реализовать
        //Используйте value.hashCode(tableSize, probId) для вычисления хеша

        if ((2 * tableSize / 3) == size){
            increaseTableSize();
        }
        for(int i = 0; i < tableSize; i++){
            int hash = value.hashCode(tableSize, i);
            if ((array[hash] == null) || (array[hash].isDeleted())){
                array[hash] = value;
                size++;
                return true;
            }
            else if (!array[hash].isDeleted()){
                if (array[hash].equals(value)) return false;
            }
        }
        return false;
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
        //todo: следует реализовать
        //Используйте value.hashCode(tableSize, probId) для вычисления хеша
        for(int i = 0; i < tableSize; i++){
            int hash = value.hashCode(tableSize, i);
            if (array[hash] == null) break;
            else if(array[hash].equals(value) && !array[hash].isDeleted()){
                array[hash].setDeleted();
                size--;
                return true;
            };
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
        //todo: следует реализовать
        //Используйте value.hashCode(tableSize, probId) для вычисления хеша
        for(int i = 0; i < tableSize; i++){
            int hash = value.hashCode(tableSize, i);
            if (array[hash] == null) break;
            else if (array[hash].isDeleted()) continue;
            else if (array[hash].equals(value)) {
                return true;
            }
        }
        return false;
    }

    private void increaseTableSize(){
        tableSize *= DEFAULT_INCREASE_MULTIPLIER;
        E[] temp = (E[]) new OpenHashTableEntity[tableSize];
        for(int i = 0; i < array.length; i++){
            if ((array[i] == null) || (array[i].isDeleted())) continue;
            for(int j = 0; j < tableSize; j++) {
                int hash = array[i].hashCode(tableSize, j);
                if ((temp[hash] == null) || (temp[hash].isDeleted())){
                    temp[hash] = array[i];
                    break;
                }
            }

        }
        array = temp;
    }

    public void println(){
        System.out.println();
        for(int i = 0; i < tableSize; i++){
            System.out.println(array[i] + " " + "\nHASH = " + i);
        }
        System.out.println();
    }

    @Override
    public int size() {
        return size;
    }

    public int getTableSize() {
        return tableSize;
    }

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }

}
