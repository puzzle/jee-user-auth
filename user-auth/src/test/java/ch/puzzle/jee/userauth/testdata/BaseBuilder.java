package ch.puzzle.jee.userauth.testdata;


public abstract class BaseBuilder<C, B> {

    public abstract C build();

    public abstract B defaults();

    public abstract B id();

}
