grammar Grammar;

@header {
    package grammar;
}

// The main entry point for parsing a grammar.
grammarSpec
    : grammarName action* ruleSpec* EOF
    ;

grammarName
    : 'grammar' LexerName Semi
    ;

// Match stuff like @parser::members {int i;}
action
   : At actionId ActionBlock
   ;

ActionBlock
   : '{{' .+? '}}'
   ;

// Rules blocks
ruleSpec
   : parserRuleSpec
   | lexerRuleSpec
   ;

// -----------------------------------
// Parser rules
parserRuleSpec
   : ParserName ArgBlock? ruleReturns? localsSpec? Colon ruleBlock Semi
   ;

// Arguments block
ArgBlock
    : LBrack .+? RBrack
    ;

// Returns block
ruleReturns
    : 'returns' ArgBlock
    ;

// Locals block
localsSpec
    : 'locals' ArgBlock
    ;

// Rules block
ruleBlock
    : altList
    ;

// Rule Alts
altList
   : alternative (Or alternative)*
   ;

alternative
   : element+
   |
   // explicitly allow empty alts
   ;

element
    : terminal (RuleOperator)?
    | ruleref (RuleOperator)?
    | block (RuleOperator)?
    | ActionBlock
    // | notSet
    ;

terminal
    : LexerName
    | StringLiteral
    ;

// Parser rule ref
ruleref
   : ParserName ArgBlock?
   ;

// Grammar Block
block
   : LParen altList RParen
   ;

// -----------------------------------
// Lexer rules
lexerRuleSpec
   : LexerName Colon lexerRuleBlock Semi
   ;

// Lexer rule blockColon
lexerRuleBlock
    : lexerAltList
    ;

lexerAltList
   : lexerAlt (Or lexerAlt)*
   ;

lexerAlt
   : lexerElements
   |
   // explicitly allow empty alts
   ;

lexerElements
   : lexerElement+
   ;

lexerElement
   : lexerAtom (RuleOperator)?
   | lexerBlock (RuleOperator)?
   | ActionBlock
   ;

lexerAtom
    : terminal
    // | notSet
    ;

lexerBlock
   : LParen lexerAltList RParen
   ;

// -----------------------------------
// Action rules
actionId
    : Header
    | Members
    ;

LexerName
    : UppercaseLetter Letter*
    ;

ParserName
    : LowercaseLetter Letter*
    ;

RuleOperator
    : [?+*]
    ;

fragment BlockContent
    : .
    ;

fragment ArgumentContent
    : .
    ;

// -----------------------------------
// Actions
Header
    : 'header'
    ;
Members
    : 'members'
    ;

// -----------------------------------
// Comments

DOC_COMMENT         : '/**' .*? ('*/' | EOF)    -> skip;
BLOCK_COMMENT       : '/*' .*? ('*/' | EOF)     -> skip;
LINE_COMMENT        : '//' ~ [\r\n]*            -> skip;

// -----------------------------------
// Numerals
fragment DecimalNumeral
   : '0' | [1-9] DecDigit*
   ;

// -----------------------------------
// Digit
fragment DecDigit
   : [0-9]
   ;

// Literals
fragment BoolLiteral
   : 'true' | 'false'
   ;
fragment CharLiteral
   : SQuote (~ ['\r\n\\]) SQuote
   ;
StringLiteral
    : SQuote (~ ['\r\n\\])* SQuote
    ;

// Character ranges
fragment Letter
    : UppercaseLetter
    | LowercaseLetter
    ;
UppercaseLetter
    : [A-Z]
    ;
LowercaseLetter
    : [a-z]
    ;

// -----------------------------------
// Symbols

Colon
   : ':'
   ;
fragment SQuote
   : '\''
   ;
LParen
   : '('
   ;
RParen
   : ')'
   ;
fragment LBrace
   : '{'
   ;
fragment RBrace
   : '}'
   ;
fragment LBrack
   : '['
   ;
fragment RBrack
   : ']'
   ;
fragment Underscore
   : '_'
   ;
Semi
   : ';'
   ;
At
   : '@'
   ;
Or
    : '|'
    ;

Whitespace
    : [ \t]+ -> skip
    ;

Newline
    : ('\r\n' | '\r' | '\n') -> skip
    ;