package saka1029.qstack;

public class Reference implements Element {
	
	public final String name;
	
	Reference(String name) {
		this.name = name;
	}
	
	public static Reference of(String name) {
	    return new Reference(name);
	}
	
	@Override
	public void execute(Context c) {
		c.execute(c.globals.get(name));
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Reference r && r.name.equals(name);
	}

	@Override
	public String toString() {
		return name;
	}
}
