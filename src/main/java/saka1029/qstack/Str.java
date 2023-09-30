package saka1029.qstack;

public class Str implements Value {
    
    public final String value;
    
    Str(String value) {
        this.value = value;
    }
    
    public static Str of(String value) {
        return new Str(value);
    }
    
    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Str i && i.value.equals(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
