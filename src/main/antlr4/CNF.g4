grammar CNF;

// Parser Rules

tptp_file returns [ArrayList<Pair<Boolean, ArrayList<FOL.Literal>>> formulas]
    : {$formulas = new ArrayList<Pair<Boolean, ArrayList<FOL.Literal>>>();} (fs=cnf_formula {$formulas.add($fs.cnf);} | comment)* EOF
    ;

cnf_formula returns [Pair<Boolean, ArrayList<FOL.Literal>> cnf]
    : 'cnf' '(' name ',' r=role ',' f=formula ')' '.' {$cnf = Pair.of($r.text.equals("negated_conjecture"), $f.literals);}
    ;

name
    : LOWER_WORD
    ;

role
    : LOWER_WORD
    ;

formula returns [ArrayList<FOL.Literal> literals]
    : l=literal {$literals = new ArrayList<FOL.Literal>(); $literals.add($l.lit);} ('|' l2=literal {$literals.add($l2.lit);})*
    | '(' l=literal {$literals = new ArrayList<FOL.Literal>(); $literals.add($l.lit);} ('|' l2=literal {$literals.add($l2.lit);})+ ')'
    | '$false' { $literals = new ArrayList<FOL.Literal>(); }
    ;

literal returns [FOL.Literal lit]
    : a=atom {$lit = new FOL.Literal($a.pred, true);}
    | '~' a=atom {$lit = new FOL.Literal($a.pred, false);}
    ;

atom returns [FOL.Predicate pred]
    : p=predicate l=optional_term_list {$pred = new FOL.Predicate($p.predName, $l.args);}
    ;

predicate returns [String predName]
    : P=LOWER_WORD {$predName = $P.getText();}
    ;

term_list returns [ArrayList<FOL.Term> args]
    : t=term {$args = new ArrayList<FOL.Term>(); $args.add($t.t);} (',' t2=term {$args.add($t2.t);})*
    ;

term returns [FOL.Term t]
    : X=UPPER_WORD  {$t = new FOL.Variable($X.getText());}
    | f=LOWER_WORD  l=optional_term_list {$t = new FOL.Function($f.getText(), $l.args);}
    ;

optional_term_list returns [ArrayList<FOL.Term> args]
    : '(' l=term_list ')' {$args = $l.args;}
    | {$args = new ArrayList<FOL.Term>();}
    ;

comment
    : COMMENT
    ;

// Lexer Rules

LOWER_WORD
    : LOWER_LETTER+ (LOWER_LETTER | UPPER_LETTER | DIGIT | '_')*
    ;

UPPER_WORD
    : UPPER_LETTER+ (UPPER_LETTER | LOWER_LETTER | DIGIT | '_')*
    ;

COMMENT
    : '%' ~[\r\n]* -> skip
    ;

// Character classes
fragment LOWER_LETTER : [a-z] ;
fragment UPPER_LETTER : [A-Z] ;
fragment DIGIT : [0-9] ;

WS : [ \t\r\n]+ -> skip ;
