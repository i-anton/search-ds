package ru.mail.polis;

import org.omg.CORBA.Object;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by Nechaev Mikhail
 * Since 13/12/2017.
 */
public interface OpenHashTableEntity{

    /**
     * Вычисляет значение хеша объекта (другими словами значение индекса ячейки в хеш-таблице)
     * на основании её размера и номера пробы,
     * где номер пробы равен попытке найти свободную ячейку.
     *
     * Если методу поочередно дать на вход все значения probIdx, от 0 до tableSize - 1, при фиксированном tableSize,
     * то на выходе должна получиться последовательности из всех возможных индексов ячеек хеш-таблицы
     *
     * @param tableSize — текущий размер хеш-таблицы
     * @param probId — номер пробы. Значение от 0 до tableSize - 1
     * @return значение вычисленного хеша
     * @throws IllegalArgumentException если probIdx < 0 или probIdx >= tableSize
     */
    boolean isDeleted();

    void setDeleted();

    int hashCode(int tableSize, int probId) throws IllegalArgumentException;

}
