module Grammar where

data Atom = Atom1 String | Atom2 Expression
data Application = Application1 Atom | Application2 Application Atom
data Expression = Expression1 Application | Expression2 String Expression | Expression3 Application String Expression

instance Show Atom where
    show (Atom1 var) = var
    show (Atom2 expr) = show expr

instance Show Application where
    show (Application1 atom) = show atom
    show (Application2 application atom) = "(" ++ (show application) ++ " " ++ (show atom) ++ ")"

instance Show Expression where
    show (Expression1 application) = show application
    show (Expression2 var expr) = "(\\" ++ var ++ "." ++ (show expr) ++ ")"
    show (Expression3 application var expr) = "(" ++ (show application) ++ " (\\" ++ var ++ "." ++ (show expr) ++ "))"
