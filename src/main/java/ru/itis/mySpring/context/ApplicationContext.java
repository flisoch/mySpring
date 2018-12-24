package ru.itis.mySpring.context;

public interface ApplicationContext {
    <T> T getComponent(Class<T> componentClass);
}
