package saka1029.qstack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Map;

import org.junit.Test;

public class TestBind {

    @Test
    public void testGet() {
        Symbol x = Symbol.of("x");
        Symbol y = Symbol.of("y");
        Symbol z = Symbol.of("z");
        Bind b = Bind.of(null, java.util.List.of(x, y, z));
        Accessor az = b.get(z, 0);
        assertEquals(-1, az.offset);
        assertEquals(0, az.nest);
        Accessor ax = b.get(x, 0);
        assertEquals(-3, ax.offset);
        assertEquals(0, ax.nest);
        assertNull(b.get(Symbol.of("not"), 0));
    }

    @Test
    public void testGetNest() {
        Symbol p = Symbol.of("p");
        Symbol q = Symbol.of("q");
        Symbol r = Symbol.of("r");
        Bind b1 = Bind.of(null, java.util.List.of(p, q));
        Bind b = Bind.of(b1, java.util.List.of(q, r));
        assertEquals(Map.of(q, -2, r, -1), b.bind);
        assertEquals(Map.of(p, -2, q, -1), b.previous.bind);
        assertNull(b.previous.previous);
        Accessor ar = b.get(r, 0);
        assertEquals(-1, ar.offset);
        assertEquals(0, ar.nest);
        Accessor aq = b.get(q, 0);
        assertEquals(-2, aq.offset);
        assertEquals(0, aq.nest);
        Accessor ap = b.get(p, 0);
        assertEquals(-2, ap.offset);
        assertEquals(1, ap.nest);
    }

}
