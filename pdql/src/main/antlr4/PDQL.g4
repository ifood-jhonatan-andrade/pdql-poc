/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 by Bart Kiers
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * Project      : sqlite-parser; an ANTLR4 grammar for SQLite
 *                https://github.com/bkiers/sqlite-parser
 * Developed by : Bart Kiers, bart@big-o.nl
 */
grammar PDQL;

parse
 : ( sql_stmt_list | error )* EOF
 ;

error
 : UNEXPECTED_CHAR
   {
     throw new RuntimeException("UNEXPECTED_CHAR=" + $UNEXPECTED_CHAR.text);
   }
 ;

sql_stmt_list
 : ';'* sql_stmt ( ';'+ sql_stmt )* ';'*
 ;

sql_stmt
 : select_ifood
 ;

/*
    SQLite understands the following binary operators, in order from highest to
    lowest precedence:

    ||
    *    /    %
    +    -
    <<   >>   &    |
    <    <=   >    >=
    =    ==   !=   <>   IS   IS NOT   IN   LIKE   GLOB   MATCH   REGEXP
    AND
    OR
*/


expr_pdql
 : literal_value
 | BIND_PARAMETER
 | column_name
 | expr_pdql ( '<' | '<=' | '>' | '>=' ) expr_pdql
 | expr_pdql ( '=' | '==' | '!=' | '<>' | K_IS | K_IS K_NOT | K_IN | K_LIKE | K_GLOB | K_MATCH | K_REGEXP ) expr_pdql
 | expr_pdql K_AND expr_pdql
 | expr_pdql K_OR expr_pdql
 | '(' expr_pdql ')'
 | expr_pdql K_NOT? ( K_LIKE | K_GLOB | K_REGEXP | K_MATCH ) expr_pdql
 | expr_pdql ( K_ISNULL | K_NOTNULL | K_NOT K_NULL )
 | expr_pdql K_IS K_NOT? expr_pdql
 | expr_pdql K_NOT? K_BETWEEN expr_pdql K_AND expr_pdql
 | expr_pdql (K_NOT? K_IN) ( '(' ( expr_pdql ( ',' expr_pdql )* )? ')' )
 ;

expr_exclude
 : literal_value
 | BIND_PARAMETER
 | ( ( database_name '.' )? table_name '.' )? column_name
 | unary_operator expr_exclude
 | expr_exclude '||' expr_exclude
 | expr_exclude ( '*' | '/' | '%' ) expr_exclude
 | expr_exclude ( '+' | '-' ) expr_exclude
 | expr_exclude ( '<<' | '>>' | '&' | '|' ) expr_exclude
 | expr_exclude ( '<' | '<=' | '>' | '>=' ) expr_exclude
 | expr_exclude ( '=' | '==' | '!=' | '<>' | K_IS | K_IS K_NOT | K_IN | K_LIKE | K_GLOB | K_MATCH | K_REGEXP ) expr_exclude
 | expr_exclude K_OR expr_exclude
 | '(' expr_exclude ')'
 | expr_exclude ( K_ISNULL | K_NOTNULL | K_NOT K_NULL )
 | expr_exclude K_IS K_NOT? expr_exclude
 | expr_exclude K_NOT? K_BETWEEN expr_exclude K_AND expr_exclude
 ;



select_ifood
 : K_IFOOD expr_pdql ( K_IFOOD_EXCLUDE expr_exclude )?
 ;

literal_value
 : NUMERIC_LITERAL
 | STRING_LITERAL
 | BLOB_LITERAL
 | K_NULL
 ;

unary_operator
 : '-'
 | '+'
 | '~'
 | K_NOT
 ;

keyword
 :
 K_AND
 | K_BETWEEN
 | K_IGNORE
 | K_IN
 | K_IS
 | K_ISNULL
 | K_LIKE
 | K_MATCH
 | K_NO
 | K_NOT
 | K_NOTNULL
 | K_NULL
 | K_OR
 | K_REGEXP
 | K_IFOOD
 | K_IFOOD_EXCLUDE
 ;

database_name
 : any_name
 ;

table_name
 : any_name
 ;

column_name
 : any_name
 ;

any_name
 : IDENTIFIER
 | keyword
 | STRING_LITERAL
 | '(' any_name ')'
 ;

SCOL : ';';
DOT : '.';
OPEN_PAR : '(';
CLOSE_PAR : ')';
COMMA : ',';
ASSIGN : '=';
STAR : '*';
PLUS : '+';
MINUS : '-';
TILDE : '~';
PIPE2 : '||';
DIV : '/';
MOD : '%';
LT2 : '<<';
GT2 : '>>';
AMP : '&';
PIPE : '|';
LT : '<';
LT_EQ : '<=';
GT : '>';
GT_EQ : '>=';
EQ : '==';
NOT_EQ1 : '!=';
NOT_EQ2 : '<>';

// http://www.sqlite.org/lang_keywords.html
K_AND : A N D;
K_BETWEEN : B E T W E E N;
K_GLOB : G L O B;
K_IGNORE : I G N O R E;
K_IN : I N;
K_IS : I S;
K_ISNULL : I S N U L L;
K_LIKE : L I K E;
K_MATCH : M A T C H;
K_NO : N O;
K_NOT : N O T;
K_NOTNULL : N O T N U L L;
K_NULL : N U L L;
K_OR : O R;
K_REGEXP : R E G E X P;
K_IFOOD : P D Q L;
K_IFOOD_EXCLUDE : E X C L U D E;

IDENTIFIER
 : '"' (~'"' | '""')* '"'
 | '`' (~'`' | '``')* '`'
 | '[' ~']'* ']'
 | [a-zA-Z_] [a-zA-Z_0-9]* // TODO check: needs more chars in set
 ;

NUMERIC_LITERAL
 : DIGIT+ ( '.' DIGIT* )? ( E [-+]? DIGIT+ )?
 | '.' DIGIT+ ( E [-+]? DIGIT+ )?
 ;

BIND_PARAMETER
 : '?' DIGIT*
 | [:@$] IDENTIFIER
 ;

STRING_LITERAL
 : '\'' ( ~'\'' | '\'\'' )* '\''
 ;

BLOB_LITERAL
 : X STRING_LITERAL
 ;

SINGLE_LINE_COMMENT
 : '--' ~[\r\n]* -> channel(HIDDEN)
 ;

MULTILINE_COMMENT
 : '/*' .*? ( '*/' | EOF ) -> channel(HIDDEN)
 ;

SPACES
 : [ \u000B\t\r\n] -> channel(HIDDEN)
 ;

UNEXPECTED_CHAR
 : .
 ;

fragment DIGIT : [0-9];

fragment A : [aA];
fragment B : [bB];
fragment C : [cC];
fragment D : [dD];
fragment E : [eE];
fragment F : [fF];
fragment G : [gG];
fragment H : [hH];
fragment I : [iI];
fragment J : [jJ];
fragment K : [kK];
fragment L : [lL];
fragment M : [mM];
fragment N : [nN];
fragment O : [oO];
fragment P : [pP];
fragment Q : [qQ];
fragment R : [rR];
fragment S : [sS];
fragment T : [tT];
fragment U : [uU];
fragment V : [vV];
fragment W : [wW];
fragment X : [xX];
fragment Y : [yY];
fragment Z : [zZ];