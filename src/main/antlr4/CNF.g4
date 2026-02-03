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
    : literal
    | '(' literal ('|' literal)+ ')'
    ;

literal
    : atom
    | '~' atom
    ;

atom returns [Predicate pred]
    : p=predicate '(' l=term_list ')'
        {
            $pred = new Predicate($p.predName, $l.args);
            System.out.println("Predicate: " + $pred.getName() + ", " + "Args: " + $pred.getArgs());
        }
    ;

predicate returns [String predName]
    : P=LOWER_WORD {$predName = $P.getText();}
    ;

term_list returns [ArrayList<Term> args]
    : t=term {$args = new ArrayList<Term>(); $args.add($t.t);} (',' t2=term {$args.add($t2.t);})*
    ;

term returns [Term t]
    : X=UPPER_WORD  {$t = new Variable($X.getText()); System.out.println("Variable: " + $t.getName());}
    | f=LOWER_WORD  l=optional_term_list
        {
            $t = new Function($f.getText(), $l.args);
            System.out.println("Function: " + $t.getName() + ", " + "Args: " + $t.getArgs().toString());
        }
    ;

optional_term_list returns [ArrayList<Term> args]
    : '(' l=term_list ')' {$args = $l.args;}
    | {$args = new ArrayList<Term>();}
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

WS : [ \t\r\n]+ -> skip ;