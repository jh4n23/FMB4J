grammar FOL;

formula
    : atom
    | NOT formula
    | formula AND formula
    | formula OR formula
    | formula IMPL formula
    | formula DBLIMPL formula
    | '(' formula ')'
    | FORALL VARIABLE '.' formula
    | EXISTS VARIABLE '.' formula
    ;

atom
    : PREDICATE '(' params ')'
    ;

term
    : CONSTANT
    | VARIABLE
    | FUNCTION '(' params ')'
    ;

params
    : term (',' term)*
    ;

CONSTANT    : [a-e] INDEX?;
VARIABLE    : [u-z] INDEX?;
FUNCTION    : 'f' INDEX?;
PREDICATE   : [P-R] INDEX?;
INDEX       : [0-9]+;

FORALL  : 'forall';
EXISTS  : 'exists';
NOT     : '~';
AND     : '&';
OR      : '|';
IMPL    : '->';
DBLIMPL : '<->';

WS : [ \t\r\n]+ -> skip;