package com.mustafahusein.visitors;

import com.mustafahusein.CBaseVisitor;
import com.mustafahusein.CParser;
import com.mustafahusein.utility.CVisitorUtility;

import java.util.List;
import java.util.stream.Collectors;

public class CPrettyPrinter extends CBaseVisitor<String> {
    private StringBuilder builder = new StringBuilder();
    private int indent = 0;

    private static final String NEWLINE = "\n";
    private static final String SPACE = " ";
    private static final String LEFT_PARAN = "(";
    private static final String RIGHT_PARAN = ")";
    private static final String LEFT_BRACE = "{";
    private static final String RIGHT_BRACE = "}";
    private static final int INDENT_INCREMENT_VALUE = 2;

    private String newLine(int indentIncrementValue) {
        String newLine = NEWLINE;
        indent += indentIncrementValue;
        for (int i = 0; i < indent; i++) {
            newLine += SPACE;
        }
        return newLine;
    }

    private String generateBinaryOperatorString(String left, String operator, String right) {
        return String.format("%s %s %s", left, operator, right);
    }

    private String generateUnaryOperatorString(String operator, String operand) {
        return String.format("%s%s", operator, operand);
    }

    private String visitCompoundStatement(List<CParser.StatementContext> statements) {
        StringBuilder result = new StringBuilder();
        result.append(LEFT_BRACE);
        indent += INDENT_INCREMENT_VALUE;

        for (CParser.StatementContext stmt : statements) {
            result.append(newLine(0))
                    .append(CVisitorUtility.visitStatement(stmt, this));
        }
        result.append(newLine(-INDENT_INCREMENT_VALUE))
                .append(RIGHT_BRACE);
        return result.toString();
    }

    @Override
    public String toString() {
        return builder.toString();
    }

    @Override
    public String visitProgram(CParser.ProgramContext ctx) {
        for (CParser.DeclarationContext decl : ctx.declaration()) {
            builder.append(visitDeclaration(decl));
        }
        return builder.toString();
    }

    @Override
    public String visitDeclaration(CParser.DeclarationContext ctx) {
        StringBuilder result = new StringBuilder();

        result.append(ctx.returnType.getText())
                .append(SPACE)
                .append(ctx.Identifier())
                .append(LEFT_PARAN)
                .append(visitFormalList(ctx.formalList()))
                .append(RIGHT_PARAN)
                .append(SPACE)
                .append(newLine(0))
                .append(visitCompoundStatement(ctx.statement()));

        return result.toString();
    }

    @Override
    public String visitFormalList(CParser.FormalListContext ctx) {
        return ctx.formalListElement().stream()
                .map(this::visitFormalListElement)
                .collect(Collectors.joining(", "));
    }

    @Override
    public String visitFormalListElement(CParser.FormalListElementContext ctx) {
        return String.format("%s %s", ctx.Type(), ctx.Identifier());
    }

    @Override
    public String visitCompoundStatement(CParser.CompoundStatementContext ctx) {
        return visitCompoundStatement(ctx.statement());
    }

    @Override
    public String visitIfStatement(CParser.IfStatementContext ctx) {
        StringBuilder result = new StringBuilder();
        result.append("if (")
                .append(visitExpression(ctx.condition))
                .append(RIGHT_PARAN)
                .append(newLine(INDENT_INCREMENT_VALUE))
                .append(CVisitorUtility.visitStatement(ctx.statement(0), this));

        indent -= INDENT_INCREMENT_VALUE;

        if (ctx.statement(1) != null) {
            result.append(newLine(0))
                    .append("else")
                    .append(newLine(INDENT_INCREMENT_VALUE))
                    .append(CVisitorUtility.visitStatement(ctx.statement(1), this));

            indent -= INDENT_INCREMENT_VALUE;
        }

        return result.toString();
    }

    @Override
    public String visitWhileStatement(CParser.WhileStatementContext ctx) {
        StringBuilder result = new StringBuilder();

        result.append("while (")
                .append(visitExpression(ctx.condition))
                .append(RIGHT_PARAN)
                .append(newLine(INDENT_INCREMENT_VALUE))
                .append(CVisitorUtility.visitStatement(ctx.statement(), this));

        indent -= INDENT_INCREMENT_VALUE;
        return result.toString();
    }

    @Override
    public String visitReturnStatement(CParser.ReturnStatementContext ctx) {
        return String.format("return%s;",
                ctx.value != null ? SPACE + visitExpression(ctx.value) : "");
    }

    @Override
    public String visitExpressionStatement(CParser.ExpressionStatementContext ctx) {
        return String.format("%s;", visitExpression(ctx.expression()));
    }

    @Override
    public String visitVariableDeclarationStatement(CParser.VariableDeclarationStatementContext ctx) {
        return String.format("%s %s;", ctx.Type(), ctx.Identifier());
    }

    @Override
    public String visitExpression(CParser.ExpressionContext ctx) {
        return ctx.assignmentExpression() != null ?
                visitAssignmentExpression(ctx.assignmentExpression()) :
                visitOrExpression(ctx.orExpression());
    }

    @Override
    public String visitAssignmentExpression(CParser.AssignmentExpressionContext ctx) {
        return String.format("%s = %s",
                ctx.Identifier(),
                visitExpression(ctx.expression()));
    }

    @Override
    public String visitOrExpression(CParser.OrExpressionContext ctx) {
        if (ctx.left == null) {
            return visitAndExpression(ctx.andExpression());
        }

        return generateBinaryOperatorString(
                visitOrExpression(ctx.left),
                "||",
                visitAndExpression(ctx.right)
        );
    }

    @Override
    public String visitAndExpression(CParser.AndExpressionContext ctx) {
        if (ctx.left == null) {
            return visitEqualityExpression(ctx.equalityExpression());
        }

        return generateBinaryOperatorString(
                visitAndExpression(ctx.left),
                "&&",
                visitEqualityExpression(ctx.right)
        );
    }

    @Override
    public String visitEqualityExpression(CParser.EqualityExpressionContext ctx) {
        if (ctx.left == null) {
            return visitRelationalExpression(ctx.relationalExpression());
        }

        return generateBinaryOperatorString(
                visitEqualityExpression(ctx.left),
                ctx.operator.getText(),
                visitRelationalExpression(ctx.right)
        );
    }

    @Override
    public String visitRelationalExpression(CParser.RelationalExpressionContext ctx) {
        if (ctx.left == null) {
            return visitAdditiveExpression(ctx.additiveExpression());
        }

        return generateBinaryOperatorString(
                visitRelationalExpression(ctx.left),
                ctx.operator.getText(),
                visitAdditiveExpression(ctx.right)
        );
    }

    @Override
    public String visitAdditiveExpression(CParser.AdditiveExpressionContext ctx) {
        if (ctx.left == null) {
            return visitMultiplicativeExpression(ctx.multiplicativeExpression());
        }

        return generateBinaryOperatorString(
                visitAdditiveExpression(ctx.left),
                ctx.operator.getText(),
                visitMultiplicativeExpression(ctx.right)
        );
    }

    @Override
    public String visitMultiplicativeExpression(CParser.MultiplicativeExpressionContext ctx) {
        if (ctx.left == null) {
            return visitUnaryOperatorExpression(ctx.unaryOperatorExpression());
        }

        return generateBinaryOperatorString(
                visitMultiplicativeExpression(ctx.left),
                ctx.operator.getText(),
                visitUnaryOperatorExpression(ctx.right)
        );
    }

    @Override
    public String visitUnaryOperatorExpression(CParser.UnaryOperatorExpressionContext ctx) {
        if (ctx.subExpression() != null) {
            return CVisitorUtility.visitSubExpression(ctx.subExpression(), this);
        }

        return generateUnaryOperatorString(
                ctx.operator.getText(),
                visitUnaryOperatorExpression(ctx.unaryOperatorExpression())
        );
    }

    @Override
    public String visitNumberExpression(CParser.NumberExpressionContext ctx) {
        return ctx.Number().getText();
    }

    @Override
    public String visitIdentifierExpression(CParser.IdentifierExpressionContext ctx) {
        return ctx.Identifier().getText();
    }

    @Override
    public String visitBooleanLiteralExpression(CParser.BooleanLiteralExpressionContext ctx) {
        return ctx.BooleanLiteral().getText();
    }

    @Override
    public String visitFunctionCallExpression(CParser.FunctionCallExpressionContext ctx) {
        return String.format("%s(%s)",
                ctx.Identifier(),
                visitExpressionList(ctx.expressionList()));
    }

    @Override
    public String visitParenthesizedExpression(CParser.ParenthesizedExpressionContext ctx) {
        return String.format("(%s)", visitExpression(ctx.expression()));
    }

    @Override
    public String visitExpressionList(CParser.ExpressionListContext ctx) {
        return ctx.expression().stream()
                .map(this::visitExpression)
                .collect(Collectors.joining(", "));
    }
}