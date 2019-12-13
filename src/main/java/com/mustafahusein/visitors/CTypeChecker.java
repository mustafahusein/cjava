package com.mustafahusein.visitors;

import com.mustafahusein.CBaseVisitor;
import com.mustafahusein.CParser;
import com.mustafahusein.environments.TypeEnvironment;
import com.mustafahusein.errors.exceptions.InterpretationError;
import com.mustafahusein.errors.exceptions.TypeCheckError;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mustafahusein.utility.CVisitorUtility.*;

public class CTypeChecker extends CBaseVisitor<CTypeChecker.Type> {
    public enum Type {INT, BOOL, VOID}

    private Map<String, Type> functionReturnTypes = new HashMap<>();
    private Map<String, List<Type>> functionArgsTypes = new HashMap<>();
    private TypeEnvironment environment;

    private Type toType(String type) {
        return Type.valueOf(type.toUpperCase());
    }

    private List<Type> extractFunctionArgsTypes(CParser.DeclarationContext declaration) {
        return declaration.formalList().formalListElement().stream()
                .map(d -> toType(d.Type().getText()))
                .collect(Collectors.toList());
    }

    private void typeCheckBinaryExpressions(Type left, Type right, Type expected, Token operator,
                                            ParserRuleContext leftContext, ParserRuleContext rightContext) {
        if (left.equals(right) && !left.equals(expected))
            throw new TypeCheckError(operator);
        else if (!left.equals(expected))
            throw new TypeCheckError(leftContext);
        else if (!right.equals(expected))
            throw new TypeCheckError(rightContext);
    }

    @Override
    public Type visitProgram(CParser.ProgramContext ctx) {
        for (CParser.DeclarationContext declaration : ctx.declaration()) {
            String funcID = declaration.Identifier().getText();
            functionReturnTypes.put(funcID, toType(declaration.returnType.getText()));
            functionArgsTypes.put(funcID, extractFunctionArgsTypes(declaration));
        }

        for (CParser.DeclarationContext declaration : ctx.declaration()) {
            visitDeclaration(declaration);
        }

        return null;
    }

    @Override
    public Type visitDeclaration(CParser.DeclarationContext ctx) {
        String funcID = ctx.Identifier().getText();
        environment = new TypeEnvironment(funcID);
        environment.enterScope();

        visitFormalList(ctx.formalList());
        visitStatements(ctx.statement(), this);

        if (!functionReturnTypes.get(funcID).equals(Type.VOID) &&
                !environment.isReturns()) {
            throw new TypeCheckError(ctx);
        }

        environment.leaveScope();
        return null;
    }

    @Override
    public Type visitFormalList(CParser.FormalListContext ctx) {
        for (CParser.FormalListElementContext arg : ctx.formalListElement()) {
            visitFormalListElement(arg);
        }
        return null;
    }

    @Override
    public Type visitFormalListElement(CParser.FormalListElementContext ctx) {
        String argID = ctx.Identifier().getText();
        environment.addVariable(argID);
        environment.setVariable(argID, toType(ctx.Type().getText()));
        return null;
    }

    @Override
    public Type visitCompoundStatement(CParser.CompoundStatementContext ctx) {
        environment.enterScope();
        visitStatements(ctx.statement(), this);
        environment.leaveScope();
        return null;
    }

    @Override
    public Type visitIfStatement(CParser.IfStatementContext ctx) {
        if (!visitExpression(ctx.condition).equals(Type.BOOL)) {
            throw new TypeCheckError(ctx.condition);
        }

        visitStatement(ctx.statement(0), this);
        if (ctx.statement(1) != null) {
            visitStatement(ctx.statement(1), this);
        }

        return null;
    }

    @Override
    public Type visitWhileStatement(CParser.WhileStatementContext ctx) {
        if (!visitExpression(ctx.condition).equals(Type.BOOL)) {
            throw new TypeCheckError(ctx.condition);
        }

        visitStatement(ctx.statement(), this);
        return null;
    }

    @Override
    public Type visitReturnStatement(CParser.ReturnStatementContext ctx) {
        Type expected = functionReturnTypes.get(environment.getParentFuncID());
        Type value = ctx.value == null ? Type.VOID : visitExpression(ctx.value);

        if (!value.equals(expected)) {
            throw new TypeCheckError(ctx);
        }

        environment.setReturns(true);
        return null;
    }

    @Override
    public Type visitExpressionStatement(CParser.ExpressionStatementContext ctx) {
        visitExpression(ctx.expression());
        return null;
    }

    @Override
    public Type visitVariableDeclarationStatement(CParser.VariableDeclarationStatementContext ctx) {
        try {
            String varID = ctx.Identifier().getText();
            environment.addVariable(varID);
            environment.setVariable(varID, toType(ctx.Type().getText()));
            return null;
        } catch (InterpretationError error) {
            throw new TypeCheckError(ctx);
        }
    }

    @Override
    public Type visitExpression(CParser.ExpressionContext ctx) {
        return ctx.assignmentExpression() != null ? visitAssignmentExpression(ctx.assignmentExpression()) :
                visitOrExpression(ctx.orExpression());
    }

    @Override
    public Type visitAssignmentExpression(CParser.AssignmentExpressionContext ctx) {
        Type value = visitExpression(ctx.expression());
        try {
            Type expected = environment.lookupVariable(ctx.Identifier().getText());
            if (!value.equals(expected)) {
                throw new TypeCheckError(ctx.expression());
            }
        } catch (InterpretationError error) {
            throw new TypeCheckError(ctx.expression());
        }
        return value;
    }

    @Override
    public Type visitOrExpression(CParser.OrExpressionContext ctx) {
        if (ctx.left == null) {
            return visitAndExpression(ctx.andExpression());
        }

        Type left = visitOrExpression(ctx.left);
        Type right = visitAndExpression(ctx.right);

        typeCheckBinaryExpressions(left, right, Type.BOOL,
                ctx.operator, ctx.left, ctx.right);

        return Type.BOOL;
    }

    @Override
    public Type visitAndExpression(CParser.AndExpressionContext ctx) {
        if (ctx.left == null) {
            return visitEqualityExpression(ctx.equalityExpression());
        }

        Type left = visitAndExpression(ctx.left);
        Type right = visitEqualityExpression(ctx.right);

        typeCheckBinaryExpressions(left, right, Type.BOOL,
                ctx.operator, ctx.left, ctx.right);

        return Type.BOOL;
    }

    @Override
    public Type visitEqualityExpression(CParser.EqualityExpressionContext ctx) {
        if (ctx.left == null) {
            return visitRelationalExpression(ctx.relationalExpression());
        }

        Type left = visitEqualityExpression(ctx.left);
        Type right = visitRelationalExpression(ctx.right);

        if (!right.equals(left))
            throw new TypeCheckError(ctx.right);

        return Type.BOOL;
    }

    @Override
    public Type visitRelationalExpression(CParser.RelationalExpressionContext ctx) {
        if (ctx.left == null) {
            return visitAdditiveExpression(ctx.additiveExpression());
        }

        Type left = visitRelationalExpression(ctx.left);
        Type right = visitAdditiveExpression(ctx.right);

        typeCheckBinaryExpressions(left, right, Type.INT,
                ctx.operator, ctx.left, ctx.right);

        return Type.BOOL;
    }

    @Override
    public Type visitAdditiveExpression(CParser.AdditiveExpressionContext ctx) {
        if (ctx.left == null) {
            return visitMultiplicativeExpression(ctx.multiplicativeExpression());
        }

        Type left = visitAdditiveExpression(ctx.left);
        Type right = visitMultiplicativeExpression(ctx.right);

        typeCheckBinaryExpressions(left, right, Type.INT,
                ctx.operator, ctx.left, ctx.right);

        return Type.INT;
    }

    @Override
    public Type visitMultiplicativeExpression(CParser.MultiplicativeExpressionContext ctx) {
        if (ctx.left == null) {
            return visitUnaryOperatorExpression(ctx.unaryOperatorExpression());
        }

        Type left = visitMultiplicativeExpression(ctx.left);
        Type right = visitUnaryOperatorExpression(ctx.right);

        typeCheckBinaryExpressions(left, right, Type.INT,
                ctx.operator, ctx.left, ctx.right);

        return Type.INT;
    }

    @Override
    public Type visitUnaryOperatorExpression(CParser.UnaryOperatorExpressionContext ctx) {
        if (ctx.subExpression() != null) {
            return visitSubExpression(ctx.subExpression(), this);
        }

        Type operandType = visitUnaryOperatorExpression(ctx.unaryOperatorExpression());
        String operator = ctx.operator.getText();

        if ((!operandType.equals(Type.INT) && operator.equals("-")) ||
                (!operandType.equals(Type.BOOL) && operator.equals("!"))) {
            throw new TypeCheckError(ctx);
        }

        return operandType;
    }

    @Override
    public Type visitNumberExpression(CParser.NumberExpressionContext ctx) {
        return Type.INT;
    }

    @Override
    public Type visitIdentifierExpression(CParser.IdentifierExpressionContext ctx) {
        try {
            return environment.lookupVariable(ctx.Identifier().getText());
        } catch (InterpretationError error) {
            throw new TypeCheckError(ctx);
        }
    }

    @Override
    public Type visitBooleanLiteralExpression(CParser.BooleanLiteralExpressionContext ctx) {
        return Type.BOOL;
    }

    @Override
    public Type visitFunctionCallExpression(CParser.FunctionCallExpressionContext ctx) {
        String funcID = ctx.Identifier().getText();
        List<CParser.ExpressionContext> argsTypes = ctx.expressionList().expression();

        for (CParser.ExpressionContext arg : argsTypes) {
            if (visitExpression(arg).equals(Type.VOID)) {
                throw new TypeCheckError(arg);
            }
        }

        if (funcID.equals("print")) {
            return Type.VOID;
        }

        if (!functionReturnTypes.containsKey(funcID)) {
            throw new TypeCheckError(ctx);
        }

        List<Type> argsExpectedTypes = functionArgsTypes.get(funcID);
        if (argsTypes.size() != argsExpectedTypes.size()) {
            throw new TypeCheckError(ctx);
        }

        for (int i = 0; i < argsTypes.size(); i++) {
            Type argType = visitExpression(argsTypes.get(i));
            if (!argType.equals(argsExpectedTypes.get(i))) {
                throw new TypeCheckError(ctx);
            }
        }

        return functionReturnTypes.get(funcID);
    }

    @Override
    public Type visitParenthesizedExpression(CParser.ParenthesizedExpressionContext ctx) {
        return visitExpression(ctx.expression());
    }

    @Override
    public Type visitExpressionList(CParser.ExpressionListContext ctx) {
        return super.visitExpressionList(ctx);
    }
}