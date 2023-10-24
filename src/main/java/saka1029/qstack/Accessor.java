package saka1029.qstack;

public class Accessor implements Traceable {
    
    final int nest, offset;
    
    Accessor(int nest, int offset) {
        this.nest = nest;
        this.offset = offset;
    }
    
    public static Accessor of(int nest, int offset) {
        return new Accessor(nest, offset);
    }

    @Override
    public void execute(Context c) {
        c.load(nest, offset);
    }
    
    public void store(Context c) {
        c.store(nest, offset);
    }
    
    @Override
    public String toString() {
        return (offset < 0 ? "A" + -offset : "L" + offset)  + (nest == 0 ? "" : nest);
    }

}
