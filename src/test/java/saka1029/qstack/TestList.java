package saka1029.qstack;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestList {

    @Test
    public void testAt() {
        List list = List.of(Int.of(1), Int.of(2), Int.of(3));
        assertEquals(Int.of(1), list.at(1));
        Context c = Context.of(10);
        assertEquals(c.eval("2"), c.eval("2 '(1 2 3) at"));
    }

    @Test
    public void testSize() {
        List list = List.of(Int.of(1), Int.of(2), Int.of(3));
        assertEquals(3, list.size());
        Context c = Context.of(10);
        assertEquals(c.eval("3"), c.eval("'(1 2 3) size"));
    }

}
