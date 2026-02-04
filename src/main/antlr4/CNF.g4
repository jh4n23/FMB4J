grammar CNF;

// Parser Rules

tptp_file returns [ArrayList<ArrayList<Literal>> formulas]
    : {$formulas = new ArrayList<ArrayList<Literal>>();} (fs=cnf_formula {$formulas.add($fs.literals);} | comment)* EOF
    ;

cnf_formula returns [ArrayList<Literal> literals]
    : 'cnf' '(' name ',' role ',' f=formula ')' '.' {$literals = $f.literals;}
    ;

name
    : LOWER_WORD
    ;

role
    : LOWER_WORD
    ;

formula returns [ArrayList<Literal> literals]
    : l=literal {$literals = new ArrayList<Literal>(); $literals.add($l.lit);}
    | '(' l=literal {$literals = new ArrayList<Literal>(); $literals.add($l.lit);} ('|' l2=literal {$literals.add($l2.lit);})+ ')'
    ;

literal returns [Literal lit]
    : a=atom {$lit = new Literal($a.pred, true);}
    | '~' a=atom {$lit = new Literal($a.pred, false);}
    ;

atom returns [Predicate pred]
    : p=predicate '(' l=term_list ')' {$pred = new Predicate($p.predName, $l.args);}
    ;

predicate returns [String predName]
    : P=LOWER_WORD {$predName = $P.getText();}
    ;

term_list returns [ArrayList<Term> args]
    : t=term {$args = new ArrayList<Term>(); $args.add($t.t);} (',' t2=term {$args.add($t2.t);})*
    ;

term returns [Term t]
    : X=UPPER_WORD  {$t = new Variable($X.getText());}
    | f=LOWER_WORD  l=optional_term_list {$t = new Function($f.getText(), $l.args);}
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