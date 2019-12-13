grammar C;

program : declaration* EOF;

declaration : returnType = (Type | 'void') Identifier '(' formalList ')' '{' code=statement* '}';

formalList
    : formalListElement (',' formalListElement)*
    |
    ;

formalListElement : Type Identifier;

statement
    : '{' statement* '}' #compoundStatement
    | 'if' '(' condition=expression ')' statement ('else' statement)? #ifStatement
    | 'while' '(' condition=expression ')' statement #whileStatement
    | 'return' value=expression? ';' #returnStatement
    | expression ';' #expressionStatement
    | Type Identifier ';' #variableDeclarationStatement
    ;

expression
    : assignmentExpression
    | orExpression
    ;

assignmentExpression : Identifier operator='=' expression;

orExpression
    : andExpression
    | left=orExpression operator='||' right=andExpression
    ;

andExpression
    : equalityExpression
    | left=andExpression operator='&&' right=equalityExpression
    ;

equalityExpression
    : relationalExpression
    | left=equalityExpression operator=('=='|'!=') right=relationalExpression
    ;

relationalExpression
    : additiveExpression
    | left=relationalExpression operator=('<'|'>'|'<='|'>=') right=additiveExpression
    ;

additiveExpression
    : multiplicativeExpression
    | left=additiveExpression operator=('+'|'-') right=multiplicativeExpression
    ;

multiplicativeExpression
    : unaryOperatorExpression
    | left=multiplicativeExpression operator=('*'|'/') right=unaryOperatorExpression
    ;

unaryOperatorExpression
    : subExpression
    | operator=('-'|'!') unaryOperatorExpression
    ;

subExpression
    : Number #numberExpression
    | Identifier #identifierExpression
    | BooleanLiteral #booleanLiteralExpression
    | Identifier '(' expressionList ')' #functionCallExpression
    | '(' expression ')' #parenthesizedExpression
    ;

expressionList
    : expression (',' expression)*
    |
    ;

Type
    : 'int'
    | 'bool'
    ;

BooleanLiteral
	:	'true'
	|	'false'
	;

fragment Alpha : [a-zA-Z];
fragment Digit : [0-9];
fragment AlphaNumeric : Alpha | Digit;

Identifier : Alpha AlphaNumeric*;
Number : Digit+;

WS  :  [ \t\r\n\u000C]+ -> skip
    ;

COMMENT
    :   '/*' .*? '*/' -> skip
    ;

LINE_COMMENT
    :   '//' ~[\r\n]* -> skip
    ;