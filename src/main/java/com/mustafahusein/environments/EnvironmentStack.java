package com.mustafahusein.environments;

import java.util.Stack;

public class EnvironmentStack<T> {
    private Stack<Environment<T>> environments = new Stack<>();

    public Environment<T> peekEnvironment() {
        return environments.peek();
    }

    public void pushNewEnvironment() {
        environments.push(new Environment<>());
    }

    public void popEnvironment() {
        environments.pop();
    }
}
