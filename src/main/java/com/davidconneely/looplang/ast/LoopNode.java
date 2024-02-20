package com.davidconneely.looplang.ast;

import com.davidconneely.looplang.interpreter.Context;
import com.davidconneely.looplang.interpreter.Interpreter;
import com.davidconneely.looplang.interpreter.InterpreterException;
import com.davidconneely.looplang.interpreter.InterpreterFactory;
import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.parser.Parser;
import com.davidconneely.looplang.parser.ParserFactory;
import com.davidconneely.looplang.token.Token;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.davidconneely.looplang.ast.NodeUtils.nextTokenWithKind;

final class LoopNode implements Node {
    private String variable;
    private List<Node> body;
    private final Set<String> programs;

    LoopNode(final Set<String> programs) {
        this.programs = programs;
    }

    @Override
    public void parse(final Lexer lexer) throws IOException {
        nextTokenWithKind(lexer, Token.Kind.KW_LOOP, "in loop");
        variable = nextTokenWithKind(lexer, Token.Kind.IDENTIFIER, "as count variable in loop").textValue();
        Token token = lexer.next();
        if (token.kind() != Token.Kind.KW_DO) {
            lexer.pushback(token); // `DO` is optional.
        }
        body = parseBody(lexer);
    }

    private List<Node> parseBody(final Lexer lexer) throws IOException {
        List<Node> body = new ArrayList<>();
        final Parser parser = ParserFactory.newParser(lexer, Token.Kind.KW_END, programs);
        Node node = parser.next();
        while (node != null) {
            body.add(node);
            node = parser.next();
        }
        return body;
    }

    @Override
    public void interpret(final Context context) {
        if (variable == null || body == null) {
            throw new InterpreterException("uninitialized loop");
        }
        final int count;
        try {
            count = context.getVariable(variable);
        } catch (InterpreterException e) {
            throw new InterpreterException("loop: expected defined variable name; got " + variable, e);
        }
        final Interpreter interpreter = InterpreterFactory.newInterpreter(context);
        for (int i = 0; i < count; ++i) {
            for (Node node : body) {
                interpreter.interpret(node);
            }
        }
    }

    @Override
    public String toString() {
        if (variable == null || body == null) {
            return "<uninitialized loop>";
        }
        final List<String> lines = new ArrayList<>();
        lines.add("LOOP " + variable + " DO");
        body.forEach(node -> lines.add(node.toString().indent(2).stripTrailing()));
        lines.add("END");
        return String.join("\n", lines);
    }
}
