package com.davidconneely.looplang.token;

import com.davidconneely.looplang.lexer.LexerException;

final class DefaultToken implements Token {
    private final Kind kind;
    private final String value;

    DefaultToken(final Kind kind, final String value) {
        this.kind = kind;
        this.value = value;
    }

    @Override
    public Kind kind() {
        return kind;
    }

    @Override
    public String textValue() {
        return value;
    }

    @Override
    public int intValue() {
        if (kind != Kind.NUMBER) {
            throw new LexerException("not a numeric literal kind - " + this);
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new LexerException("not a numeric literal value - " + this);
        }
    }

    @Override
    public String toString() {
        return switch (kind) {
            case NEWLINE, STRING -> kind.name() + "[" + Token.escaped(value) + "]";
            case COMMENT, NUMBER, IDENTIFIER, EOF, ASSIGN, PLUS, LPAREN, RPAREN, COMMA, SEMICOLON, KW_PROGRAM, KW_LOOP, KW_DO, KW_END, KW_INPUT, KW_PRINT -> kind.name() + "[" + value + "]";
        };
    }
}
