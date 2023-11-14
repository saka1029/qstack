package saka1029.qstack;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestRange {

    @Test
    public void testRangeASC() {
        Context c = Context.of(10);
        assertEquals(Int.of(10), c.eval("0 1 4 1 range '+ for"));
    }

    @Test
    public void testRangeDESC() {
        Context c = Context.of(10);
        assertEquals(Int.of(-10), c.eval("0 -1 -4 -1 range '+ for"));
    }
}
