package refactored;

import org.junit.Test;

import static org.junit.Assert.*;

public class InfinityArrayTest {

    @Test
    public void add() {
        InfinityArray testArray = new InfinityArray("testArray");
        long index = testArray.add("tests");
        testArray.set(index, "bests");
        String results = testArray.getString(index);
        assertEquals("bests", results);
    }

    @Test
    public void testAddToGarbage() throws Exception {
        InfinityArray arr = new InfinityArray("testArrayGarbage");
        long index = arr.add("test");
        arr.set(index, "testss");
        assertEquals("testss", arr.getString(index));

        InfinityConstArray garbage4 = arr.garbageCollector.get(4L);

        long garbage4Size = garbage4.getLong(0);
        long lastValue = garbage4.getLong(garbage4Size);
        assertEquals(index, lastValue);

        long ssIndex = arr.add("ss");
        arr.set(ssIndex, "ssdd");
        InfinityConstArray garbage2 = arr.garbageCollector.get(2L);
        long garbage2Size = garbage2.getLong(0);
        lastValue = garbage2.getLong(garbage2Size);
        assertEquals(ssIndex, lastValue);

        long garbage4NewSize = garbage4.getLong(0);
        assertEquals(garbage4Size - 1, garbage4NewSize);
    }
}