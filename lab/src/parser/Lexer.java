package parser;

public class Lexer {
    private final String text;
    private int position;

    public Lexer(String text) {
        this.text = text.trim();
        this.position = 0;
    }

    public Token next() {
        if (position + 1 > text.length()) {
            return null;
        }

        char symbol = text.charAt(position);
        Token token;
        if (symbol == '\\') {
            token = new Token(TokenType.LAMBDA);
            position++;
        } else if (symbol == '.') {
            token = new Token(TokenType.DOT);
            position++;
        } else if (symbol == '(') {
            token = new Token(TokenType.OPENING_BRACKET);
            position++;
        } else if (symbol == ')') {
            token = new Token(TokenType.CLOSING_BRACKET);
            position++;
        } else if (isWordSymbol(position)) {
            int lastPosition = position;
            while (isWordSymbol(lastPosition + 1)) {
                lastPosition++;
            }
            token = new Token(text.substring(position, lastPosition + 1));
            position = lastPosition + 1;
        } else {
            throw new IllegalStateException("Lol kek");
        }

        while (isWhiteSpaceSymbol(position)) {
            position++;
        }
        return token;
    }

    public State saveState() {
        return new State(position);
    }

    public void applyState(State state) {
        position = state.position;
    }

    private boolean isWhiteSpaceSymbol(int position) {
        if (position + 1 > text.length()) {
            return false;
        }
        char symbol = text.charAt(position);
        return Character.isWhitespace(symbol);
    }

    private boolean isWordSymbol(int position) {
        if (position + 1 > text.length()) {
            return false;
        }
        char symbol = text.charAt(position);
        return Character.isLetterOrDigit(symbol) || symbol == '\'';
    }

    public static class Token {
        private final TokenType type;
        private final String text;

        public Token(TokenType type) {
            this.type = type;
            this.text = null;
        }

        public Token(String text) {
            this.type = TokenType.WORD;
            this.text = text;
        }

        public TokenType getType() {
            return type;
        }

        public String getText() {
            return text;
        }
    }

    public enum TokenType {
        LAMBDA,
        DOT,
        OPENING_BRACKET,
        CLOSING_BRACKET,
        WORD
    }

    public static class State {
        private final int position;

        State(int position) {
            this.position = position;
        }
    }
}
