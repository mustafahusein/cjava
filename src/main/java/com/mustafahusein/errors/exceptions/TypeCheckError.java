package com.mustafahusein.errors.exceptions;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class TypeCheckError extends RuntimeException {
    public TypeCheckError(ParserRuleContext offendingContext) {
        super(String.format("fail %d %d", offendingContext.start.getLine(), offendingContext.start.getCharPositionInLine() + 1));
    }
    public TypeCheckError(Token offendingToken) {
        super(String.format("fail %d %d", offendingToken.getLine(), offendingToken.getCharPositionInLine() + 1));
    }
}
