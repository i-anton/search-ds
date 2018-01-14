import java.util.Random;
import java.util.Set;

import org.junit.Assert;

/**
 * Created by Nechaev Mikhail
 * Since 12/12/2017.
 */
public class AbstractSetTest {

    protected enum TransformOperation {
        ADD, REMOVE
    }

    protected static final Random RANDOM = new Random();

    //todo: Закомментируйте или модифицируйте параметры если что-то ещё не реализовано и тестируйте
    protected static final boolean ENABLED_REMOVE = true;

    protected  <E> void checkTransformOperation(Set<E> validSet, Set<E> testSet, E value, TransformOperation transformOperation) {
        if (TransformOperation.ADD == transformOperation) {
            Assert.assertTrue("add", validSet.add(value) == testSet.add(value));
        } else if (ENABLED_REMOVE) {
            Assert.assertTrue("remove", validSet.remove(value) == testSet.remove(value));
        }
    }

    protected  <E> void checkSizeAndContains(Set<E> validSet, Set<E> testSet, E value) {
        Assert.assertTrue("size", validSet.size() == testSet.size());
        Assert.assertTrue("contains", validSet.contains(value) == testSet.contains(value));
    }
}
