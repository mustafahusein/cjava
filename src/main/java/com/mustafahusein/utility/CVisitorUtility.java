package com.mustafahusein.utility;

import com.mustafahusein.CParser;
import com.mustafahusein.CVisitor;
import com.mustafahusein.visitors.CInterpreter;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.List;

public final class CVisitorUtility {
    private static final CInterpreter evaluator = new CInterpreter();

    private CVisitorUtility() {

    }

    public static <T> T visitStatement(CParser.StatementContext ctx, CVisitor<T> visitor) {
        if (ctx instanceof CParser.CompoundStatementContext) {
            return visitor.visitCompoundStatement((CParser.CompoundStatementContext) ctx);
        } else if (ctx instanceof CParser.IfStatementContext) {
            return visitor.visitIfStatement((CParser.IfStatementContext) ctx);
        } else if (ctx instanceof CParser.WhileStatementContext) {
            return visitor.visitWhileStatement((CParser.WhileStatementContext) ctx);
        } else if (ctx instanceof CParser.ReturnStatementContext) {
            return visitor.visitReturnStatement((CParser.ReturnStatementContext) ctx);
        } else if (ctx instanceof CParser.ExpressionStatementContext) {
            return visitor.visitExpressionStatement((CParser.ExpressionStatementContext) ctx);
        } else {
            return visitor.visitVariableDeclarationStatement((CParser.VariableDeclarationStatementContext) ctx);
        }
    }

    public static  <T> T visitStatements(List<CParser.StatementContext> statements, CVisitor<T> visitor) {
        for (CParser.StatementContext statement : statements) {
            T val = visitStatement(statement, visitor);
            if (val != null) return val;
        }
        return null;
    }

    public static <T> T visitSubExpression(CParser.SubExpressionContext ctx, CVisitor<T> visitor) {
        if (ctx instanceof CParser.NumberExpressionContext) {
            return visitor.visitNumberExpression((CParser.NumberExpressionContext) ctx);
        } else if (ctx instanceof CParser.IdentifierExpressionContext) {
            return visitor.visitIdentifierExpression((CParser.IdentifierExpressionContext) ctx);
        } else if (ctx instanceof CParser.BooleanLiteralExpressionContext) {
            return visitor.visitBooleanLiteralExpression((CParser.BooleanLiteralExpressionContext) ctx);
        } else if (ctx instanceof CParser.FunctionCallExpressionContext) {
            return visitor.visitFunctionCallExpression((CParser.FunctionCallExpressionContext) ctx);
        } else {
            return visitor.visitParenthesizedExpression((CParser.ParenthesizedExpressionContext) ctx);
        }
    }

    public static ParseTree extractExpression(CParser.ExpressionContext parent) {
        if(parent.assignmentExpression() != null) return parent.assignmentExpression();

        ParseTree child = parent.orExpression();
        while (child.getChildCount() == 1) {
            child = child.getChild(0);
        }

        return child;
    }
}
