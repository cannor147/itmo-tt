package parser;

import expression.Application;
import expression.Expression;
import expression.Lambda;
import expression.Variable;

public class Parser {
    private Lexer lexer;

    public Expression parse(String text) {
        this.lexer = new Lexer(text);
        return parseExpression();
    }

    private Expression parseExpression() {
        return parseApplication();
    }

    private Expression parseApplication() {
        Lexer.Token token = lexer.next();
        if (token != null && token.getType() == Lexer.TokenType.LAMBDA) {
            return parseLambda(token);
        }

        Expression firstAtom = parseAtom(token);
        Lexer.State state = lexer.saveState();
        token = lexer.next();
        while (token != null && token.getType() != Lexer.TokenType.CLOSING_BRACKET) {
            if (token.getType() == Lexer.TokenType.LAMBDA) {
                Expression lambda = parseLambda(token);
                return new Application(firstAtom, lambda);
            }

            Expression secondAtom = parseAtom(token);
            firstAtom = new Application(firstAtom, secondAtom);
            state = lexer.saveState();
            token = lexer.next();
        }
        if (token != null && token.getType() == Lexer.TokenType.CLOSING_BRACKET) {
            lexer.applyState(state);
        }

        return firstAtom;
    }

    private Expression parseLambda(Lexer.Token token) {
        ensureTokenType(token, Lexer.TokenType.LAMBDA);

        token = lexer.next();
        ensureTokenType(token, Lexer.TokenType.WORD);
        Variable variable = new Variable(token.getText());

        token = lexer.next();
        ensureTokenType(token, Lexer.TokenType.DOT);

        Expression expression = parseExpression();
        return new Lambda(variable, expression);
    }

    private Expression parseAtom(Lexer.Token token) {
        if (token != null && token.getType() == Lexer.TokenType.OPENING_BRACKET) {
            Expression expression = parseExpression();
            token = lexer.next();
            ensureTokenType(token, Lexer.TokenType.CLOSING_BRACKET);
            return expression;
        } else {
            ensureTokenType(token, Lexer.TokenType.WORD);
            return new Variable(token.getText());
        }
    }

    private static void ensureTokenType(Lexer.Token token, Lexer.TokenType type) {
        if (token == null || token.getType() != type) {
            throw new IllegalStateException("Lol kek");
        }
    }
}
