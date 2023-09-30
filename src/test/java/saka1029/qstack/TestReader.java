package saka1029.qstack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class TestReader {
    
    @Test
    public void testReadElement() {
        Context c = Context.of(10);
        Reader reader = Reader.of(c, "  1 -2   drop ");
        assertEquals(Int.ONE, reader.read());
        assertEquals(Int.of(-2), reader.read());
        assertEquals(Symbol.of("drop"), reader.read());
        assertNull(reader.read());
    }
    
    @Test
    public void testReadList() {
        Context c = Context.of(10);
        Reader reader = Reader.of(c, "  (1 -2   drop) ");
        assertEquals(List.of(Int.ONE, Int.of(-2), Symbol.of("drop")), reader.read());
        assertNull(reader.read());
    }
    
    @Test
    public void testReadPair() {
        Context c = Context.of(10);
        Reader reader = Reader.of(c, "  (1 . -2) ");
        assertEquals(Pair.of(Int.ONE, Int.of(-2)), reader.read());
        assertNull(reader.read());
    }
    
    @Test
    public void testReadQuote() {
        Context c = Context.of(10);
        Reader reader = Reader.of(c, "  '1 ");
        assertEquals(Quote.of(Int.ONE), reader.read());
        assertNull(reader.read());
    }
    
    @Test
    public void testReadQuoteList() {
        Context c = Context.of(10);
        Reader reader = Reader.of(c, "  ' (1 2  ) ");
        assertEquals(Quote.of(List.of(Int.ONE, Int.TWO)), reader.read());
        assertNull(reader.read());
    }

}
