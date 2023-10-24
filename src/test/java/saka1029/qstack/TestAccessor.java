package saka1029.qstack;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestAccessor {

    @Test
    public void testStore() {
        Context c = Context.of(10);
        c.add("set", k -> ((Accessor)k.globals.get((Symbol)k.pop())).store(k));
        c.add("L-3", Accessor.of(0, -3));
        c.add("L-2", Accessor.of(0, -2));
        c.add("L-1", Accessor.of(0, -1));
        c.execute(Int.of(1));  // argument a
        c.execute(Int.of(2));  // argument b
        c.execute(Int.of(3));  // argument c
        c.execute(Int.of(c.fp)); // old fp
        c.fp = c.sp - 1;       // new fp
        c.run("10 'L-3 set 20 'L-2 set 30 'L-1 set L-3");
        assertEquals(Int.of(10), c.pop()); // argument a
        assertEquals(Int.of(0), c.pop()); // old fp
        assertEquals(Int.of(30), c.pop()); // argument c
        assertEquals(Int.of(20), c.pop()); // argument b
        assertEquals(Int.of(10), c.pop()); // argument a
        assertEquals(0, c.sp);
    }
}
