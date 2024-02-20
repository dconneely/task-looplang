package com.davidconneely.looplang.ast;

import com.davidconneely.looplang.interpreter.Context;
import com.davidconneely.looplang.interpreter.InterpreterException;
import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.token.Token;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.davidconneely.looplang.ast.NodeUtils.nextTokenWithKind;

final class PrintNode implements Node {
    private List<Token> printTokens;

    @Override
    public void parse(final Lexer lexer) throws IOException {
        nextTokenWithKind(lexer, Token.Kind.KW_PRINT, "in print");
        printTokens = nextPrintTokens(lexer);
    }

    private static List<Token> nextPrintTokens(final Lexer lexer) throws IOException {
        List<Token> tokens = new ArrayList<>();
        Token token = lexer.next();
        while (isPrintTokenKind(token.kind())) {
            tokens.add(token);
            token = lexer.next();
            if (token.kind() != Token.Kind.COMMA) {
                break;
            }
            token = lexer.next();
        }
        lexer.pushback(token);
        return tokens;
    }

    private static boolean isPrintTokenKind(Token.Kind kind) {
        return kind == Token.Kind.STRING || kind == Token.Kind.NUMBER || kind == Token.Kind.IDENTIFIER;
    }

    @Override
    public void interpret(final Context context) {
        if (printTokens == null) {
            throw new InterpreterException("uninitialized print");
        }
        StringBuilder sb = new StringBuilder();
        boolean lastString = true;
        for (Token token : printTokens) {
            switch (token.kind()) {
                case STRING:
                    sb.append(token.textValue());
                    lastString = true;
                    break;
                case NUMBER:
                    if (!lastString) {
                        sb.append(' ');
                    }
                    sb.append(token.intValue());
                    lastString = false;
                case IDENTIFIER:
                    if (!lastString) {
                        sb.append(' ');
                    }
                    try {
                        sb.append(context.getVariable(token.textValue()));
                    } catch (InterpreterException e) {
                        sb.append("undefined");
                    }
                    lastString = false;
                    break;
                default:
                    sb.append(token.textValue());
                    lastString = false;
                    break;
            }
        }
        String str = sb.toString();
        System.out.println(str);
    }

    @Override
    public String toString() {
        if (printTokens == null) {
            return "<uninitialized print>";
        }
        return "PRINT " + printTokens.stream().map(token -> switch (token.kind()) {
            case STRING -> Token.escaped(token.textValue());
            case NUMBER -> Integer.toString(token.intValue());
            case IDENTIFIER -> token.textValue().toLowerCase(Locale.ROOT);
            default -> token.textValue();
        }).collect(Collectors.joining(", "));
    }
}
