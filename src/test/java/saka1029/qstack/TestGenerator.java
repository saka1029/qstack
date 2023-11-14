package saka1029.qstack;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestGenerator {

    @Test
    public void testGenerator() {
        Context c = Context.of(10);
        assertEquals(Int.of(6), c.eval("0 '(1 yield 2 yield 3 yield) generator '+ foreach"));
    }
}
