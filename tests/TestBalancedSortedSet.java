import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;

import ru.mail.polis.AVLTree;
import ru.mail.polis.BalancedSortedSet;
import ru.mail.polis.NotBalancedTreeException;
import ru.mail.polis.RedBlackTree;

/**
 * Created by Nechaev Mikhail
 * Since 12/12/2017.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(value = Parameterized.class)
public class TestBalancedSortedSet extends AbstractSetTest {

    static protected class NamedComparator<E> {
        String name;
        Comparator<E> comparator;

        NamedComparator(String name, Comparator<E> comparator) {
            this.name = name;
            this.comparator = comparator;
        }
    }

    //todo: Закомментируйте или модифицируйте параметры если что-то ещё не реализовано и тестируйте

    private static final Class<?>[] testClasses = (Class<?>[]) new Class<?>[]{
            AVLTree.class,
           // RedBlackTree.class,
    };

    @SuppressWarnings("unchecked")
    private static final TestBalancedSortedSet.NamedComparator<Integer>[] comparators = (TestBalancedSortedSet.NamedComparator<Integer>[]) new TestBalancedSortedSet.NamedComparator[]{
            new TestBalancedSortedSet.NamedComparator<Integer>("NULL", null),
            new TestBalancedSortedSet.NamedComparator<Integer>("REVERSE_ORDER", Comparator.reverseOrder()),
            new TestBalancedSortedSet.NamedComparator<Integer>("NATURAL_ORDER", Comparator.naturalOrder()),
            new TestBalancedSortedSet.NamedComparator<Integer>("EVEN_FIRST", Comparator.comparingInt((Integer v) -> v % 2).thenComparingInt(v -> v)),
            new TestBalancedSortedSet.NamedComparator<Integer>("ALL_EQUALS", (v1, v2) -> 0),
    };

    @Parameterized.Parameter()
    public Class<?> testClass;
    @Parameterized.Parameter(1)
    public Comparator<Integer> comparator;
    @Parameterized.Parameter(2)
    public String comparatorName;

    @Parameterized.Parameters(name = "{0} + {2}")
    public static Collection<Object[]> data() {
        Object[][] objects = new Object[testClasses.length * comparators.length][3];
        int index = 0;
        for (Class<?> testClazz : testClasses) {
            for (NamedComparator<Integer> namedComparator : comparators) {
                objects[index++] = new Object[]{
                        testClazz,
                        namedComparator.comparator,
                        namedComparator.name
                };
            }
        }
        return Arrays.asList(objects);
    }

    private SortedSet<Integer> validSortedSet;
    private BalancedSortedSet<Integer> testSortedSet;

    @Before //Запускается перед запуском каждого теста
    public void createSortedSets() {
        validSortedSet = create(TreeSet.class);
        testSortedSet = createTestSortedSet(testClass);
    }

    @Test
    public void test01_emptyAndNull() {
        checkFirstAndLast(validSortedSet, testSortedSet);
        checkSizeAndContains(validSortedSet, testSortedSet, 0);
    }

    @Test
    public void test02_add() {
        check(validSortedSet, testSortedSet, 5, TransformOperation.ADD);
    }

    @Test
    public void test02_someAdd() {
        for (int value = 0; value < 10; value++) {
            check(validSortedSet, testSortedSet, value, TransformOperation.ADD);
        }
    }

    @Test
    public void test03_AddWithRemove() {
        check(validSortedSet, testSortedSet, 5, TransformOperation.ADD);
        check(validSortedSet, testSortedSet, 5, TransformOperation.REMOVE);
    }

    @Test
    public void test04_someAddWithRemove() {
        for (int value = 0; value < 10; value++) {
            check(validSortedSet, testSortedSet, value, TransformOperation.ADD);
        }
        for (int value = 0; value < 10; value++) {
            check(validSortedSet, testSortedSet, value, TransformOperation.REMOVE);
        }
    }

    @Test
    public void test05_small() {
        for (int value = 0; value < 10; value++) {
            check(validSortedSet, testSortedSet, value, TransformOperation.ADD);
        }
        for (int value = 10; value >= 0; value--) {
            check(validSortedSet, testSortedSet, value, TransformOperation.REMOVE);
        }
        for (int value = 0; value >= -10; value--) {
            check(validSortedSet, testSortedSet, value, TransformOperation.REMOVE);
        }
        for (int value = -10; value < 0; value++) {
            check(validSortedSet, testSortedSet, value, TransformOperation.ADD);
        }
    }

    @Test
    public void test06_middle() {
        for (int value = 0; value < 15; value++) {
            check(validSortedSet, testSortedSet, value, TransformOperation.ADD);
        }
        for (int value = 15; value < 30; value++) {
            check(validSortedSet, testSortedSet, value, TransformOperation.ADD);
            if (value % 2 == 0) {
                check(validSortedSet, testSortedSet, value, TransformOperation.REMOVE);
            }
        }
        for (int value = 100; value >= 0; value--) {
            check(validSortedSet, testSortedSet, value, TransformOperation.REMOVE);
        }
    }

    @Test
    public void test07_bigRandom() {
        for (int i = 0; i < 1000; i++) {
            check(validSortedSet, testSortedSet, RANDOM.nextInt(1000), TransformOperation.ADD);
        }
        for (int i = 0; i < 1000; i++) {
            check(validSortedSet, testSortedSet, RANDOM.nextInt(1000), TransformOperation.REMOVE);
        }
    }

    private <E> void check(SortedSet<E> validSortedSet, BalancedSortedSet<E> testSortedSet, E value, TransformOperation transformOperation) {
        checkFirstAndLast(validSortedSet, testSortedSet);
        checkTransformOperation(validSortedSet, testSortedSet, value, transformOperation);
        checkBalanced(testSortedSet);
        checkSizeAndContains(validSortedSet, testSortedSet, value);
        checkTransformOperation(validSortedSet, testSortedSet, value, transformOperation);
        checkBalanced(testSortedSet);
        checkSizeAndContains(validSortedSet, testSortedSet, value);
        checkFirstAndLast(validSortedSet, testSortedSet);
    }

    private <E> void checkBalanced(BalancedSortedSet<E> balancedSortedSet) {
        try {
            testSortedSet.checkBalanced();
        } catch (NotBalancedTreeException e) {
            Assert.fail(e.getMessage());
        }
    }

    private <E> void checkFirstAndLast(SortedSet<E> validSortedSet, SortedSet<E> testSortedSet) {
        if (validSortedSet.isEmpty()) {
            try {
                testSortedSet.first();
                Assert.fail("NoSuchElementException - first");
            } catch (NoSuchElementException e) {
                /* empty */
            }
            try {
                testSortedSet.last();
                Assert.fail("NoSuchElementException - last");
            } catch (NoSuchElementException e) {
                /* empty */
            }
        } else {
            Assert.assertEquals("first", validSortedSet.first(), testSortedSet.first());
            Assert.assertEquals("last", validSortedSet.last(), testSortedSet.last());
        }
    }

    @SuppressWarnings("unchecked")
    private SortedSet<Integer> create(Class<?> clazz) {
        try {
            return (SortedSet<Integer>) clazz.getConstructor(Comparator.class).newInstance(comparator);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new AssertionError(e);
        }
    }

    @SuppressWarnings("unchecked")
    private BalancedSortedSet<Integer> createTestSortedSet(Class<?> clazz) {
        try {
            return (BalancedSortedSet<Integer>) clazz.getConstructor(Comparator.class).newInstance(comparator);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new AssertionError(e);
        }
    }
}
