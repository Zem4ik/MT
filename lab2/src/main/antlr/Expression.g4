grammar Expression;

@header {
    package parser;
}

NL
    : '\n'
    ;
INT
    : [0-9]+
    ;
Variable
    : [a-zA-Z_][a-zA-Z_0-9]*
    ;

POW
    : '^'
    ;
PLUS
    : '+'
    ;
EQUAL
    : '='
    ;
MINUS
    : '-'
    ;
MULT
    : '*'
    ;
DIV
    : '/'
    ;
LPAR
    : '('
    ;
RPAR
    : ')'
    ;

expr
    : setVar ';' NL expr        # ToSetVar
    | EOF                       # End
    ;

setVar
    : Variable EQUAL sumOrSub   # SetVariable
    ;

sumOrSub
    : sumOrSub PLUS mulOrDiv    # Plus
    | sumOrSub MINUS mulOrDiv   # Minus
    | mulOrDiv                  # ToMultOrDiv
    ;

mulOrDiv
    : mulOrDiv MULT pow         # Multiplication
    | mulOrDiv DIV pow          # Division
    | pow                       # ToPow
    ;

pow
    : unaryMinus (POW pow)?     # Power
    ;

unaryMinus
    : MINUS unaryMinus          # ChangeSign
    | atom                      # ToAtom
    ;

atom
    : INT                       # Integer
    | Variable                  # Variable
    | LPAR sumOrSub RPAR        # Braces
    ;

WS
    : [\r\n\t ]+ -> skip
    ;