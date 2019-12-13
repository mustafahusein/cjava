package com.mustafahusein.environments;

import com.mustafahusein.visitors.CTypeChecker;

public class TypeEnvironment extends Environment<CTypeChecker.Type> {
    private final String parentFuncID;
    private boolean returns = false;

    public TypeEnvironment(String parentFuncID) {
        this.parentFuncID = parentFuncID;
    }

    public String getParentFuncID() {
        return parentFuncID;
    }

    public boolean isReturns() {
        return returns;
    }

    public void setReturns(boolean returns) {
        this.returns = returns;
    }
}
