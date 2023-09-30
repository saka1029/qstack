package saka1029.qstack;

public interface Value extends Element {

    default void execute(Context c) {
        c.push(this);
    }
}
