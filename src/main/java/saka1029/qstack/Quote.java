package saka1029.qstack;

public class Quote implements Value {
    
    public final Element value;
    
    Quote(Element value) {
        this.value = value;
    }
    
    public static Quote of(Element value) {
        return new Quote(value);
    }
    
    @Override
    public void execute(Context c) {
        c.push(value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Quote q && q.value.equals(value);
    }

    @Override
    public String toString() {
        return "'" + value;
    }

}
