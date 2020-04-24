module Main where
import Lexer
import Parser
import Grammar

main :: IO ()
main = do
    input <- getContents
    putStrLn $ show $ parser $ alexScanTokens input