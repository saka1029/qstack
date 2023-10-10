package saka1029.qstack;

import java.util.Objects;

public class Str implements Ordered {
	
	public final String value;
	
	private Str(String value) {
		this.value = value;
	}
	
	public static Str of(String value) {
	    return new Str(value);
	}
	
	@Override
	public int compareTo(Ordered o) {
	    return value.compareTo(((Str)o).value);
	}

	@Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Str s && value.equals(s.value);
    }

    @Override
	public String toString() {
		return value;
	}
}
