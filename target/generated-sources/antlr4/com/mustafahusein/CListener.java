// Generated from com\mustafahusein\C.g4 by ANTLR 4.7
package com.mustafahusein;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link CParser}.
 */
public interface CListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link CParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(CParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(CParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#declaration}.
	 * @param ctx the parse tree
	 */
	void enterDeclaration(CParser.DeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#declaration}.
	 * @param ctx the parse tree
	 */
	void exitDeclaration(CParser.DeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#formalList}.
	 * @param ctx the parse tree
	 */
	void enterFormalList(CParser.FormalListContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#formalList}.
	 * @param ctx the parse tree
	 */
	void exitFormalList(CParser.FormalListContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#formalListElement}.
	 * @param ctx the parse tree
	 */
	void enterFormalListElement(CParser.FormalListElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#formalListElement}.
	 * @param ctx the parse tree
	 */
	void exitFormalListElement(CParser.FormalListElementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code compoundStatement}
	 * labeled alternative in {@link CParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterCompoundStatement(CParser.CompoundStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code compoundStatement}
	 * labeled alternative in {@link CParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitCompoundStatement(CParser.CompoundStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ifStatement}
	 * labeled alternative in {@link CParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterIfStatement(CParser.IfStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ifStatement}
	 * labeled alternative in {@link CParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitIfStatement(CParser.IfStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code whileStatement}
	 * labeled alternative in {@link CParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterWhileStatement(CParser.WhileStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code whileStatement}
	 * labeled alternative in {@link CParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitWhileStatement(CParser.WhileStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code returnStatement}
	 * labeled alternative in {@link CParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterReturnStatement(CParser.ReturnStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code returnStatement}
	 * labeled alternative in {@link CParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitReturnStatement(CParser.ReturnStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expressionStatement}
	 * labeled alternative in {@link CParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterExpressionStatement(CParser.ExpressionStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expressionStatement}
	 * labeled alternative in {@link CParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitExpressionStatement(CParser.ExpressionStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code variableDeclarationStatement}
	 * labeled alternative in {@link CParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclarationStatement(CParser.VariableDeclarationStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code variableDeclarationStatement}
	 * labeled alternative in {@link CParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclarationStatement(CParser.VariableDeclarationStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(CParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(CParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#assignmentExpression}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentExpression(CParser.AssignmentExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#assignmentExpression}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentExpression(CParser.AssignmentExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#orExpression}.
	 * @param ctx the parse tree
	 */
	void enterOrExpression(CParser.OrExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#orExpression}.
	 * @param ctx the parse tree
	 */
	void exitOrExpression(CParser.OrExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#andExpression}.
	 * @param ctx the parse tree
	 */
	void enterAndExpression(CParser.AndExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#andExpression}.
	 * @param ctx the parse tree
	 */
	void exitAndExpression(CParser.AndExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#equalityExpression}.
	 * @param ctx the parse tree
	 */
	void enterEqualityExpression(CParser.EqualityExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#equalityExpression}.
	 * @param ctx the parse tree
	 */
	void exitEqualityExpression(CParser.EqualityExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#relationalExpression}.
	 * @param ctx the parse tree
	 */
	void enterRelationalExpression(CParser.RelationalExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#relationalExpression}.
	 * @param ctx the parse tree
	 */
	void exitRelationalExpression(CParser.RelationalExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#additiveExpression}.
	 * @param ctx the parse tree
	 */
	void enterAdditiveExpression(CParser.AdditiveExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#additiveExpression}.
	 * @param ctx the parse tree
	 */
	void exitAdditiveExpression(CParser.AdditiveExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#multiplicativeExpression}.
	 * @param ctx the parse tree
	 */
	void enterMultiplicativeExpression(CParser.MultiplicativeExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#multiplicativeExpression}.
	 * @param ctx the parse tree
	 */
	void exitMultiplicativeExpression(CParser.MultiplicativeExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#unaryOperatorExpression}.
	 * @param ctx the parse tree
	 */
	void enterUnaryOperatorExpression(CParser.UnaryOperatorExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#unaryOperatorExpression}.
	 * @param ctx the parse tree
	 */
	void exitUnaryOperatorExpression(CParser.UnaryOperatorExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code numberExpression}
	 * labeled alternative in {@link CParser#subExpression}.
	 * @param ctx the parse tree
	 */
	void enterNumberExpression(CParser.NumberExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code numberExpression}
	 * labeled alternative in {@link CParser#subExpression}.
	 * @param ctx the parse tree
	 */
	void exitNumberExpression(CParser.NumberExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code identifierExpression}
	 * labeled alternative in {@link CParser#subExpression}.
	 * @param ctx the parse tree
	 */
	void enterIdentifierExpression(CParser.IdentifierExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code identifierExpression}
	 * labeled alternative in {@link CParser#subExpression}.
	 * @param ctx the parse tree
	 */
	void exitIdentifierExpression(CParser.IdentifierExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code booleanLiteralExpression}
	 * labeled alternative in {@link CParser#subExpression}.
	 * @param ctx the parse tree
	 */
	void enterBooleanLiteralExpression(CParser.BooleanLiteralExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code booleanLiteralExpression}
	 * labeled alternative in {@link CParser#subExpression}.
	 * @param ctx the parse tree
	 */
	void exitBooleanLiteralExpression(CParser.BooleanLiteralExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code functionCallExpression}
	 * labeled alternative in {@link CParser#subExpression}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCallExpression(CParser.FunctionCallExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code functionCallExpression}
	 * labeled alternative in {@link CParser#subExpression}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCallExpression(CParser.FunctionCallExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code parenthesizedExpression}
	 * labeled alternative in {@link CParser#subExpression}.
	 * @param ctx the parse tree
	 */
	void enterParenthesizedExpression(CParser.ParenthesizedExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code parenthesizedExpression}
	 * labeled alternative in {@link CParser#subExpression}.
	 * @param ctx the parse tree
	 */
	void exitParenthesizedExpression(CParser.ParenthesizedExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#expressionList}.
	 * @param ctx the parse tree
	 */
	void enterExpressionList(CParser.ExpressionListContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#expressionList}.
	 * @param ctx the parse tree
	 */
	void exitExpressionList(CParser.ExpressionListContext ctx);
}