package com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.parameters;

/**
 * @author Mike Safonov
 */
public abstract class ParameterValue<T> {
    private String _class;
    private String name;
    private T value;

    public String get_class() {
        return _class;
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }
}
