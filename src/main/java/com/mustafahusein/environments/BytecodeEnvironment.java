package com.mustafahusein.environments;

import java.util.HashMap;
import java.util.Map;

public class BytecodeEnvironment {
    private int nextVarIndex = 0;
    private Map<String, Variable> variables = new HashMap<>();

    public void addVariable(String name, String descriptor) {
        variables.put(name, new Variable(nextVarIndex++, descriptor));
    }

    public int getID(String varName) {
        return variables.get(varName).getID();
    }

    public String getDescriptor(String varName) {
        return variables.get(varName).getDescriptor();
    }

    private static class Variable {
        private final int ID;
        private final String descriptor;

        public Variable(int ID, String descriptor) {
            this.ID = ID;
            this.descriptor = descriptor;
        }

        public int getID() {
            return ID;
        }

        public String getDescriptor() {
            return descriptor;
        }
    }
}
