package saka1029.qstack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class TestReader {
    
    @Test
    public void testReadElement() {
        Reader reader = Reader.of("  1 -2   drop ");
        assertEquals(Int.ONE, reader.read());
        assertEquals(Int.of(-2), reader.read());
        assertEquals(Symbol.of("drop"), reader.read());
        assertNull(reader.read());
    }
    
    @Test
    public void testReadList() {
        Reader reader = Reader.of("  (1 -2   drop) ");
        assertEquals(List.of(Int.ONE, Int.of(-2), Symbol.of("drop")), reader.read());
        assertNull(reader.read());
    }
    
    @Test
    public void testReadPair() {
        Reader reader = Reader.of("  (1 . -2) ");
        assertEquals(Cons.of(Int.ONE, Int.of(-2)), reader.read());
        assertNull(reader.read());
    }
    
    @Test
    public void testReadQuote() {
        Reader reader = Reader.of("  '1 ");
        assertEquals(Quote.of(Int.ONE), reader.read());
        assertNull(reader.read());
    }
    
    @Test
    public void testReadQuoteList() {
        Reader reader = Reader.of("  ' (1 2  ) ");
        assertEquals(Quote.of(List.of(Int.ONE, Int.TWO)), reader.read());
        assertNull(reader.read());
    }
    
    @Test
    public void testStr() {
        Reader reader = Reader.of("  \"abc\" \"\\r\\n\" ");
        assertEquals(Str.of("abc"), reader.read());
        assertEquals(Str.of("\r\n"), reader.read());
        assertNull(reader.read());
    }

}
