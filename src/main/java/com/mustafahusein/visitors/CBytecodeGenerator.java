package com.mustafahusein.visitors;

import com.mustafahusein.CBaseVisitor;
import com.mustafahusein.CParser;
import com.mustafahusein.environments.BytecodeEnvironment;
import com.mustafahusein.utility.CVisitorUtility;
import org.antlr.v4.runtime.tree.ParseTree;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mustafahusein.utility.CVisitorUtility.*;
import static javax.lang.model.SourceVersion.isName;
import static org.objectweb.asm.Opcodes.*;

public class CBytecodeGenerator extends CBaseVisitor<Void> {
    private ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
    private MethodVisitor methodVisitor;
    private BytecodeEnvironment environment;
    private Map<String, String> functionSignatures = new HashMap<>();
    private Label startFunctionLabel;
    private Label endFunctionLabel;
    private byte[] bytecode;

    private void setBytecode(byte[] bytecode) {
        this.bytecode = bytecode;
    }

    public byte[] getBytecode() {
        return bytecode;
    }

    @Override
    public Void visitProgram(CParser.ProgramContext ctx) {
        classWriter.visit(V1_8, ACC_PUBLIC, "Main", null, "java/lang/Object", null);

        for (CParser.DeclarationContext declaration : ctx.declaration()) {
            String funcID = declaration.Identifier().getText();
            String funcSignature = String.format("(%s)%s",
                    declaration.formalList().formalListElement().stream().map(e -> toDescriptor(e.Type().getText())).collect(Collectors.joining("")),
                    toDescriptor(declaration.returnType.getText()));
            functionSignatures.put(funcID, funcSignature);
        }

        for (CParser.DeclarationContext declaration : ctx.declaration()) {
            visitDeclaration(declaration);
        }

        classWriter.visitEnd();
        setBytecode(classWriter.toByteArray());
        return null;
    }

    @Override
    public Void visitDeclaration(CParser.DeclarationContext ctx) {
        String funcID = ctx.Identifier().getText();
        String funcSignature = functionSignatures.get(funcID);
        startFunctionLabel = new Label();
        endFunctionLabel = new Label();

        methodVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_STATIC,
                funcID,
                funcSignature,
                null,
                null);

        environment = new BytecodeEnvironment();
        methodVisitor.visitLabel(startFunctionLabel);

        for (CParser.FormalListElementContext param : ctx.formalList().formalListElement()) {
            environment.addVariable(param.Identifier().getText(), toDescriptor(param.Type().getText()));
        }

        visitStatements(ctx.statement(), this);
        methodVisitor.visitInsn(RETURN);

        methodVisitor.visitLabel(endFunctionLabel);
        if (!funcID.equals("main")) visitFormalList(ctx.formalList());
        methodVisitor.visitMaxs(-1, -1);
        methodVisitor.visitEnd();
        return null;
    }

    @Override
    public Void visitFormalList(CParser.FormalListContext ctx) {
        for (CParser.FormalListElementContext param : ctx.formalListElement()) {
            visitFormalListElement(param);
        }
        return null;
    }

    @Override
    public Void visitFormalListElement(CParser.FormalListElementContext ctx) {
        String paramName = ctx.Identifier().getText();
        methodVisitor.visitLocalVariable(paramName,
                environment.getDescriptor(paramName),
                null,
                startFunctionLabel,
                endFunctionLabel,
                environment.getID(ctx.Identifier().getText()));
        return null;
    }

    @Override
    public Void visitCompoundStatement(CParser.CompoundStatementContext ctx) {
        for (CParser.StatementContext statement : ctx.statement()) {
            visitStatement(statement, this);
        }
        return null;
    }

    @Override
    public Void visitIfStatement(CParser.IfStatementContext ctx) {
        boolean hasElse = ctx.statement(1) != null;
        Label end = new Label();
        Label elseLabel = new Label();

        visitExpression(ctx.condition);
        methodVisitor.visitJumpInsn(IFEQ, hasElse ? elseLabel : end);
        visitStatement(ctx.statement(0), this);
        methodVisitor.visitJumpInsn(GOTO, end);

        if (hasElse) {
            methodVisitor.visitLabel(elseLabel);
            visitStatement(ctx.statement(1), this);
        }

        methodVisitor.visitLabel(end);
        return null;
    }

    @Override
    public Void visitWhileStatement(CParser.WhileStatementContext ctx) {
        Label start = new Label();
        Label end = new Label();

        methodVisitor.visitLabel(start);
        visitExpression(ctx.condition);
        methodVisitor.visitJumpInsn(IFEQ, end);

        visitStatement(ctx.statement(), this);
        methodVisitor.visitJumpInsn(GOTO, start);
        methodVisitor.visitLabel(end);

        return null;
    }

    @Override
    public Void visitReturnStatement(CParser.ReturnStatementContext ctx) {
        boolean returnValue = ctx.value != null;

        if (returnValue) {
            visitExpression(ctx.value);
            methodVisitor.visitInsn(IRETURN);
        } else {
            methodVisitor.visitInsn(RETURN);
        }

        return null;
    }

    @Override
    public Void visitExpressionStatement(CParser.ExpressionStatementContext ctx) {
        visitExpression(ctx.expression());
        ParseTree actualExpression = extractExpression(ctx.expression());

        boolean ignoreResult = true;
        if (actualExpression instanceof CParser.AssignmentExpressionContext) ignoreResult = false;
        else if (actualExpression instanceof CParser.FunctionCallExpressionContext) {
            String funcID = ((CParser.FunctionCallExpressionContext) actualExpression).Identifier().getText();
            if (funcID.equals("print") || functionSignatures.get(funcID).contains("V")) {
                ignoreResult = false;
            }
        }

        if (ignoreResult) methodVisitor.visitInsn(POP);
        return null;
    }

    @Override
    public Void visitVariableDeclarationStatement(CParser.VariableDeclarationStatementContext ctx) {
        environment.addVariable(ctx.Identifier().getText(), toDescriptor(ctx.Type().getText()));
        return null;
    }

    @Override
    public Void visitExpression(CParser.ExpressionContext ctx) {
        return ctx.assignmentExpression() != null ? visitAssignmentExpression(ctx.assignmentExpression()) :
                visitOrExpression(ctx.orExpression());
    }

    @Override
    public Void visitAssignmentExpression(CParser.AssignmentExpressionContext ctx) {
        visitExpression(ctx.expression());

        boolean chained = ctx.getParent().getParent() instanceof CParser.AssignmentExpressionContext;
        if (chained) methodVisitor.visitInsn(DUP);

        methodVisitor.visitVarInsn(ISTORE, environment.getID(ctx.Identifier().getText()));
        return null;
    }

    @Override
    public Void visitOrExpression(CParser.OrExpressionContext ctx) {
        if (ctx.left == null) {
            return visitAndExpression(ctx.andExpression());
        }

        Label trueLabel = new Label();
        Label falseLabel = new Label();
        Label end = new Label();

        visitOrExpression(ctx.left);
        methodVisitor.visitJumpInsn(IFNE, trueLabel);
        visitAndExpression(ctx.right);
        methodVisitor.visitJumpInsn(IFEQ, falseLabel);

        methodVisitor.visitLabel(trueLabel);
        methodVisitor.visitInsn(ICONST_1);
        methodVisitor.visitJumpInsn(GOTO, end);

        methodVisitor.visitLabel(falseLabel);
        methodVisitor.visitInsn(ICONST_0);

        methodVisitor.visitLabel(end);
        return null;
    }

    @Override
    public Void visitAndExpression(CParser.AndExpressionContext ctx) {
        if (ctx.left == null) {
            return visitEqualityExpression(ctx.equalityExpression());
        }

        Label falseLabel = new Label();
        Label end = new Label();

        visitAndExpression(ctx.left);
        methodVisitor.visitJumpInsn(IFEQ, falseLabel);
        visitEqualityExpression(ctx.right);
        methodVisitor.visitJumpInsn(IFEQ, falseLabel);
        methodVisitor.visitInsn(ICONST_1);
        methodVisitor.visitJumpInsn(GOTO, end);

        methodVisitor.visitLabel(falseLabel);
        methodVisitor.visitInsn(ICONST_0);

        methodVisitor.visitLabel(end);
        return null;
    }

    @Override
    public Void visitEqualityExpression(CParser.EqualityExpressionContext ctx) {
        if (ctx.left == null) {
            return visitRelationalExpression(ctx.relationalExpression());
        }

        visitEqualityExpression(ctx.left);
        visitRelationalExpression(ctx.right);

        generateComparisionInstruction(methodVisitor, ctx.operator.getText());
        return null;
    }

    @Override
    public Void visitRelationalExpression(CParser.RelationalExpressionContext ctx) {
        if (ctx.left == null) {
            return visitAdditiveExpression(ctx.additiveExpression());
        }

        visitRelationalExpression(ctx.left);
        visitAdditiveExpression(ctx.right);

        generateComparisionInstruction(methodVisitor, ctx.operator.getText());
        return null;
    }

    @Override
    public Void visitAdditiveExpression(CParser.AdditiveExpressionContext ctx) {
        if (ctx.left == null) {
            return visitMultiplicativeExpression(ctx.multiplicativeExpression());
        }

        visitAdditiveExpression(ctx.left);
        visitMultiplicativeExpression(ctx.right);

        generateArithmeticInstruction(methodVisitor, ctx.operator.getText());
        return null;
    }

    @Override
    public Void visitMultiplicativeExpression(CParser.MultiplicativeExpressionContext ctx) {
        if (ctx.left == null) {
            return visitUnaryOperatorExpression(ctx.unaryOperatorExpression());
        }

        visitMultiplicativeExpression(ctx.left);
        visitUnaryOperatorExpression(ctx.right);

        generateArithmeticInstruction(methodVisitor, ctx.operator.getText());
        return null;
    }

    @Override
    public Void visitUnaryOperatorExpression(CParser.UnaryOperatorExpressionContext ctx) {
        if (ctx.subExpression() != null) {
            return CVisitorUtility.visitSubExpression(ctx.subExpression(), this);
        }

        switch (ctx.operator.getText()) {
            case "-":
                visitUnaryOperatorExpression(ctx.unaryOperatorExpression());
                methodVisitor.visitInsn(INEG);
                break;
            case "!":
                methodVisitor.visitInsn(ICONST_1);
                visitUnaryOperatorExpression(ctx.unaryOperatorExpression());
                methodVisitor.visitInsn(IXOR);
                break;
        }

        return null;
    }

    @Override
    public Void visitNumberExpression(CParser.NumberExpressionContext ctx) {
        int value = Integer.parseInt(ctx.Number().getText());
        if (value >= -1 && value <= 5) {
            methodVisitor.visitInsn(ICONST_0 + value);
        } else if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
            methodVisitor.visitIntInsn(BIPUSH, value);
        } else if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
            methodVisitor.visitIntInsn(SIPUSH, value);
        } else {
            methodVisitor.visitLdcInsn(value);
        }
        return null;
    }

    @Override
    public Void visitIdentifierExpression(CParser.IdentifierExpressionContext ctx) {
        methodVisitor.visitVarInsn(ILOAD, environment.getID(ctx.Identifier().toString()));
        return null;
    }

    @Override
    public Void visitBooleanLiteralExpression(CParser.BooleanLiteralExpressionContext ctx) {
        boolean value = Boolean.parseBoolean(ctx.BooleanLiteral().getText());
        methodVisitor.visitInsn(value ? ICONST_1 : ICONST_0);
        return null;
    }

    @Override
    public Void visitFunctionCallExpression(CParser.FunctionCallExpressionContext ctx) {
        String funcID = ctx.Identifier().getText();
        if (funcID.equals("print")) {
            generatePrintStatement(ctx.expressionList().expression());
        } else {
            visitExpressionList(ctx.expressionList());
            methodVisitor.visitMethodInsn(INVOKESTATIC, "Main", funcID, functionSignatures.get(funcID), false);
        }
        return null;
    }

    @Override
    public Void visitParenthesizedExpression(CParser.ParenthesizedExpressionContext ctx) {
        return visitExpression(ctx.expression());
    }

    @Override
    public Void visitExpressionList(CParser.ExpressionListContext ctx) {
        for (CParser.ExpressionContext expression : ctx.expression()) {
            visitExpression(expression);
        }
        return null;
    }

    private String toDescriptor(String type) {
        switch (type) {
            case "int":
                return Type.getDescriptor(int.class);
            case "bool":
                return Type.getDescriptor(boolean.class);
            case "void":
                return Type.getDescriptor(void.class);
        }
        return null;
    }

    private int toOpcode(String operator) {
        switch (operator) {
            case "==":
                return IF_ICMPEQ;
            case "!=":
                return IF_ICMPNE;
            case "<":
                return IF_ICMPLT;
            case "<=":
                return IF_ICMPLE;
            case ">":
                return IF_ICMPGT;
            case ">=":
                return IF_ICMPGE;
            case "+":
                return IADD;
            case "-":
                return ISUB;
            case "*":
                return IMUL;
            case "/":
                return IDIV;
        }
        return -1;
    }

    private String determineDescriptor(String expression) {
        if (isName(expression)) return environment.getDescriptor(expression);
        else if (isFunctionCall(expression)) {
            String funcSignature = functionSignatures.get(expression.substring(0, expression.indexOf("(")));
            char descriptor = funcSignature.charAt(funcSignature.length() - 1);
            return Character.toString(descriptor);
        } else if (!expression.matches("^.*?(true|false|&&|\\||==|!=|<|<=|>|>=|!).*$"))
            return Type.getDescriptor(int.class);
        else return Type.getDescriptor(boolean.class);
    }

    private boolean isFunctionCall(String expression) {
        if (!Character.isLetter(expression.charAt(0)) || expression.length() == 1) return false;
        for (int i = 1; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c == '(') return true;
            if (!Character.isLetterOrDigit(c)) return false;
        }
        return false;
    }

    private void generateComparisionInstruction(MethodVisitor mv, String compareSign) {
        Label trueLabel = new Label();
        Label end = new Label();
        mv.visitJumpInsn(toOpcode(compareSign), trueLabel);
        mv.visitInsn(ICONST_0);
        mv.visitJumpInsn(GOTO, end);

        mv.visitLabel(trueLabel);
        mv.visitInsn(ICONST_1);

        mv.visitLabel(end);
    }

    private void generateArithmeticInstruction(MethodVisitor mv, String operator) {
        mv.visitInsn(toOpcode(operator));
    }

    private void generatePrintStatement(List<CParser.ExpressionContext> args) {
        methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        methodVisitor.visitTypeInsn(NEW, "java/lang/StringBuilder");
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);

        for (CParser.ExpressionContext arg : args) {
            visitExpression(arg);
            String stringBuilderDescriptor = String.format("(%s)Ljava/lang/StringBuilder;", determineDescriptor(arg.getText()));
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", stringBuilderDescriptor, false);
            methodVisitor.visitLdcInsn(" ");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        }

        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }
}