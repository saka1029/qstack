package saka1029.qstack;

public class Int implements Value {
    
    public static final Int ZERO = Int.of(0);
    public static final Int ONE = Int.of(1);
    public static final Int TWO = Int.of(2);
    public static final Int THREE = Int.of(3);

    public final int value;
    
    Int(int value) {
        this.value = value;
    }
    
    public static Int of(int value) {
        return new Int(value);
    }
    
    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Int i && i.value == value;
    }

    @Override
    public String toString() {
        return "" + value;
    }
}
