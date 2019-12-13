package com.mustafahusein.visitors;

import com.mustafahusein.CBaseVisitor;
import com.mustafahusein.CParser;
import com.mustafahusein.environments.EnvironmentStack;
import com.mustafahusein.errors.exceptions.InterpretationError;
import com.mustafahusein.utility.CVisitorUtility;

import java.util.*;

public class CInterpreter extends CBaseVisitor<Object> {
    private static final String FUNC_ARG_ERR_MSG = "function [%s] expects %d arguments";
    private static final String FUNC_RET_ERR_MSG = "function [%s] does not return value of type [%s]";
    private static final String UN_OP_ERR_MSG = "[%s] cannot be applied to operand [%s]";
    private static final String BIN_OP_ERR_MSG = "[%s] cannot be applied to operands [%s, %s]";
    private EnvironmentStack<Object> environmentStack = new EnvironmentStack<>();
    private Map<String, CParser.DeclarationContext> functions = new HashMap<>();

    private Object callFunction(CParser.DeclarationContext ctx, List<Object> argValues) {
        environmentStack.pushNewEnvironment();
        environmentStack.peekEnvironment().enterScope();
        List<String> argNames = (List<String>) visitFormalList(ctx.formalList());

        int valuesSize = (argValues == null) ? 0 : argValues.size();
        if (argNames.size() != valuesSize)
            throw new InterpretationError(String.format(FUNC_ARG_ERR_MSG, ctx.Identifier().getText(), argNames.size()));


        for (int i = 0; i < argNames.size(); i++) {
            environmentStack.peekEnvironment().addVariable(argNames.get(i));
            if (argValues != null) environmentStack.peekEnvironment().setVariable(argNames.get(i), argValues.get(i));
        }

        Object val = CVisitorUtility.visitStatements(ctx.statement(), this);
        environmentStack.peekEnvironment().leaveScope();
        environmentStack.popEnvironment();
        return val;
    }

    private String getTypeString(Object val) {
        if (val instanceof Integer) return "int";
        else if (val instanceof Boolean) return "bool";
        return "void";
    }

    @Override
    public Object visitProgram(CParser.ProgramContext ctx) {
        for (CParser.DeclarationContext declaration : ctx.declaration()) {
            functions.put(declaration.Identifier().getText(), declaration);
        }
        return callFunction(functions.get("main"), null);
    }

    @Override
    public Object visitDeclaration(CParser.DeclarationContext ctx) {
        return super.visitDeclaration(ctx);
    }

    @Override
    public Object visitFormalList(CParser.FormalListContext ctx) {
        List<String> argNames = new ArrayList<>();
        for (CParser.FormalListElementContext elem : ctx.formalListElement()) {
            argNames.add((String) visitFormalListElement(elem));
        }
        return argNames;
    }

    @Override
    public Object visitFormalListElement(CParser.FormalListElementContext ctx) {
        return ctx.Identifier().getText();
    }

    @Override
    public Object visitCompoundStatement(CParser.CompoundStatementContext ctx) {
        environmentStack.peekEnvironment().enterScope();
        Object val = CVisitorUtility.visitStatements(ctx.statement(), this);
        environmentStack.peekEnvironment().leaveScope();
        return val;
    }

    @Override
    public Object visitIfStatement(CParser.IfStatementContext ctx) {
        Object val = null;
        if ((Boolean) visitExpression(ctx.condition)) val = CVisitorUtility.visitStatement(ctx.statement(0), this);
        else if (ctx.statement(1) != null) val = CVisitorUtility.visitStatement(ctx.statement(1), this);
        return val;
    }

    @Override
    public Object visitWhileStatement(CParser.WhileStatementContext ctx) {
        Object val = null;
        while (val == null && (Boolean) visitExpression(ctx.condition)) val = CVisitorUtility.visitStatement(ctx.statement(), this);
        return val;
    }

    @Override
    public Object visitReturnStatement(CParser.ReturnStatementContext ctx) {
        return ctx.value == null ? null : visitExpression(ctx.value);
    }

    @Override
    public Object visitExpressionStatement(CParser.ExpressionStatementContext ctx) {
        visitExpression(ctx.expression());
        return null;
    }

    @Override
    public Object visitVariableDeclarationStatement(CParser.VariableDeclarationStatementContext ctx) {
        environmentStack.peekEnvironment().addVariable(ctx.Identifier().getText());
        return null;
    }

    @Override
    public Object visitExpression(CParser.ExpressionContext ctx) {
        return ctx.assignmentExpression() != null ? visitAssignmentExpression(ctx.assignmentExpression()) :
                visitOrExpression(ctx.orExpression());
    }

    @Override
    public Object visitAssignmentExpression(CParser.AssignmentExpressionContext ctx) {
        Object value = visitExpression(ctx.expression());
        environmentStack.peekEnvironment().setVariable(ctx.Identifier().getText(), value);
        return value;
    }

    @Override
    public Object visitOrExpression(CParser.OrExpressionContext ctx) {
        if (ctx.left == null) {
            return visitAndExpression(ctx.andExpression());
        }

        Object left = visitOrExpression(ctx.left);
        Object right = visitAndExpression(ctx.right);

        try {
            return (Boolean) left || (Boolean) right;
        } catch (ClassCastException ex) {
            throw new InterpretationError(String.format(BIN_OP_ERR_MSG, "||", left, right));
        }
    }

    @Override
    public Object visitAndExpression(CParser.AndExpressionContext ctx) {
        if (ctx.left == null) {
            return visitEqualityExpression(ctx.equalityExpression());
        }

        Object left = visitAndExpression(ctx.left);
        Object right = visitEqualityExpression(ctx.right);


        try {
            return (Boolean) left && (Boolean) right;
        } catch (ClassCastException ex) {
            throw new InterpretationError(String.format(BIN_OP_ERR_MSG, "&&", left, right));
        }
    }

    @Override
    public Object visitEqualityExpression(CParser.EqualityExpressionContext ctx) {
        if (ctx.left == null) {
            return visitRelationalExpression(ctx.relationalExpression());
        }

        Object left = visitEqualityExpression(ctx.left);
        Object right = visitRelationalExpression(ctx.right);

        if (!left.getClass().equals(right.getClass())) {
            throw new InterpretationError(String.format(BIN_OP_ERR_MSG, ctx.operator.getText(), left, right));
        }

        switch (ctx.operator.getText()) {
            case "==":
                return left.equals(right);
            case "!=":
                return !left.equals(right);
        }

        return null;
    }

    @Override
    public Object visitRelationalExpression(CParser.RelationalExpressionContext ctx) {
        if (ctx.left == null) {
            return visitAdditiveExpression(ctx.additiveExpression());
        }


        Object left = visitRelationalExpression(ctx.left);
        Object right = visitAdditiveExpression(ctx.right);

        try {
            switch (ctx.operator.getText()) {
                case "<":
                    return (Integer) left < (Integer) right;
                case ">":
                    return (Integer) left > (Integer) right;
                case "<=":
                    return (Integer) left <= (Integer) right;
                case ">=":
                    return (Integer) left >= (Integer) right;
            }
        } catch (ClassCastException ex) {
            throw new InterpretationError(String.format(BIN_OP_ERR_MSG, ctx.operator.getText(), left, right));
        }

        return null;
    }

    @Override
    public Object visitAdditiveExpression(CParser.AdditiveExpressionContext ctx) {
        if (ctx.left == null) {
            return visitMultiplicativeExpression(ctx.multiplicativeExpression());
        }

        Object left = visitAdditiveExpression(ctx.left);
        Object right = visitMultiplicativeExpression(ctx.right);

        try {
            switch (ctx.operator.getText()) {
                case "+":
                    return (Integer) left + (Integer) right;
                case "-":
                    return (Integer) left - (Integer) right;
            }
        } catch (ClassCastException ex) {
            throw new InterpretationError(String.format(BIN_OP_ERR_MSG, ctx.operator.getText(), left, right));
        }

        return null;
    }

    @Override
    public Object visitMultiplicativeExpression(CParser.MultiplicativeExpressionContext ctx) {
        if (ctx.left == null) {
            return visitUnaryOperatorExpression(ctx.unaryOperatorExpression());
        }

        Object left = visitMultiplicativeExpression(ctx.left);
        Object right = visitUnaryOperatorExpression(ctx.right);

        try {
            switch (ctx.operator.getText()) {
                case "*":
                    return (Integer) left * (Integer) right;
                case "/":
                    return (Integer) left / (Integer) right;
            }
        } catch (ClassCastException ex) {
            throw new InterpretationError(String.format(BIN_OP_ERR_MSG, ctx.operator.getText(), left, right));
        }

        return null;
    }

    @Override
    public Object visitUnaryOperatorExpression(CParser.UnaryOperatorExpressionContext ctx) {
        if (ctx.subExpression() != null) {
            return CVisitorUtility.visitSubExpression(ctx.subExpression(), this);
        }

        Object operand = visitUnaryOperatorExpression(ctx.unaryOperatorExpression());

        try {
            switch (ctx.operator.getText()) {
                case "-":
                    return -((Integer) operand);
                case "!":
                    return !((Boolean) operand);
            }
        } catch (ClassCastException ex) {
            throw new InterpretationError(String.format(UN_OP_ERR_MSG, ctx.operator.getText(), operand));
        }

        return null;
    }

    @Override
    public Object visitNumberExpression(CParser.NumberExpressionContext ctx) {
        return Integer.parseInt(ctx.Number().getText());
    }

    @Override
    public Object visitIdentifierExpression(CParser.IdentifierExpressionContext ctx) {
        return environmentStack.peekEnvironment().lookupVariable(ctx.Identifier().getText());
    }

    @Override
    public Object visitBooleanLiteralExpression(CParser.BooleanLiteralExpressionContext ctx) {
        return Boolean.valueOf(ctx.BooleanLiteral().toString());
    }

    @Override
    public Object visitFunctionCallExpression(CParser.FunctionCallExpressionContext ctx) {
        if (ctx.Identifier().getText().equals("print")) {
            for (CParser.ExpressionContext expr : ctx.expressionList().expression()) {
                System.out.print(visitExpression(expr) + " ");
            }
            System.out.println();
            return null;
        }

        List<Object> argValues = (List<Object>) visitExpressionList(ctx.expressionList());
        CParser.DeclarationContext declaration = functions.get(ctx.Identifier().getText());
        Object val = callFunction(declaration, argValues);

        if (!Objects.equals(getTypeString(val), declaration.returnType.getText())) {
            throw new InterpretationError(String.format(FUNC_RET_ERR_MSG, declaration.Identifier().getText(),
                    declaration.returnType.getText()));
        }

        return val;
    }

    @Override
    public Object visitParenthesizedExpression(CParser.ParenthesizedExpressionContext ctx) {
        return visitExpression(ctx.expression());
    }

    @Override
    public Object visitExpressionList(CParser.ExpressionListContext ctx) {
        List<Object> argValues = new ArrayList<>();
        for (CParser.ExpressionContext var : ctx.expression()) {
            argValues.add(visitExpression(var));
        }
        return argValues;
    }
}