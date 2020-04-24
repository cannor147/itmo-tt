{
module Lexer where
}

%wrapper "basic"

$digit = [0-9]
$alpha = [a-z]

tokens :-

    $white                            ;
    [\\\(\)\.]                        { \s -> TSym (head s)}
    $alpha [$alpha $digit \']*        { \s -> TVar s }
{
data Token = TSym Char | TVar String deriving (Eq, Show)
}