grammar CNF;

// Parser Rules

tptp_file
    : (cnf_formula | comment)* EOF
    ;

cnf_formula
    : 'cnf' '(' name ',' role ',' formula ')' '.'
    ;

name
    : LOWER_WORD
    ;

role
    : LOWER_WORD
    ;

formula
    : literal                                    # SingleLiteral
    | '(' literal ('|' literal)+ ')'             # Disjunction
    ;

literal
    : atom                                       # PositiveLiteral
    | '~' atom                                   # NegativeLiteral
    ;

atom
    : predicate '(' term_list ')'
    ;

predicate
    : LOWER_WORD
    ;

term_list
    : term (',' term)*
    ;

term
    : UPPER_WORD                                 # Variable
    | LOWER_WORD                                 # Constant
    ;

comment
    : COMMENT
    ;

// Lexer Rules

LOWER_WORD
    : LOWER_LETTER+ (LOWER_LETTER | DIGIT | '_')*
    ;

UPPER_WORD
    : UPPER_LETTER+ (UPPER_LETTER | DIGIT | '_')*
    ;

COMMENT
    : '%' ~[\r\n]* -> skip
    ;

// Character classes
fragment LOWER_LETTER : [a-z] ;
fragment UPPER_LETTER : [A-Z] ;
fragment DIGIT : [0-9] ;

// Whitespace
WS : [ \t\r\n]+ -> skip ;