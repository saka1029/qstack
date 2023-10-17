package saka1029.qstack;

public interface Value extends Traceable {

    default void execute(Context c) {
        c.push(this);
    }
}
