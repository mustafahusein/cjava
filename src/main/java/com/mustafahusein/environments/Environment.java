package com.mustafahusein.environments;

import com.mustafahusein.errors.exceptions.InterpretationError;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;

public class Environment<T> {
    private Deque<HashMap<String, T>> scopes = new ArrayDeque<>();
    private static final String UNDECLARED_ERR_MSG = "variable [%s] not declared";
    private static final String DECLARED_ERR_MSG = "variable [%s] already declared";

    public void enterScope() {
        scopes.push(new HashMap<>());
    }

    public void leaveScope() {
        scopes.pop();
    }

    public void addVariable(String varName) {
        for (HashMap<String, T> scope : scopes) {
            if (scope.containsKey(varName)) {
                throw new InterpretationError(String.format(DECLARED_ERR_MSG, varName));
            }
        }
        scopes.peek().put(varName, null);
    }

    public void setVariable(String varName, T value) {
        for (HashMap<String, T> scope : scopes) {
            if (scope.containsKey(varName)) {
                scope.replace(varName, value);
                return;
            }
        }
        throw new InterpretationError(String.format(UNDECLARED_ERR_MSG, varName));
    }

    public T lookupVariable(String varName) {
        for (HashMap<String, T> scope : scopes) {
            if (scope.containsKey(varName)) {
                return scope.get(varName);
            }
        }
        throw new InterpretationError(String.format(UNDECLARED_ERR_MSG, varName));
    }
}
