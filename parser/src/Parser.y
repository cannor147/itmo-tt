{
module Parser where
import Lexer
import Grammar
}

%name parser
%tokentype { Token }
%error { parseError }

%token
    opening_bracket                               { TSym '('  }
    closing_bracket                               { TSym ')'  }
    lambda                                        { TSym '\\' }
    dot                                           { TSym '.'  }
    variable                                      { TVar $$   }

%%

Expression:
    Application lambda variable dot Expression    { Expression3 $1 $3 $5 }
    |   lambda variable dot Expression            { Expression2 $2 $4 }
    |   Application                               { Expression1 $1 }

Application:
    Application Atom                              { Application2 $1 $2 }
    |   Atom                                      { Application1 $1 }

Atom:
    opening_bracket Expression closing_bracket    { Atom2 $2 }
    |   variable                                  { Atom1 $1 }

{
parseError = error "Parse error"
}
