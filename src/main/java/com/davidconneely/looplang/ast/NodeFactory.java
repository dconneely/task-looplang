package com.davidconneely.looplang.ast;

import java.util.Set;

public final class NodeFactory {
    private NodeFactory() {
    }

    /**
     * :: variable1 `:=` number::0
     */
    public static Node newAssignNumber() {
        return new AssignNumberNode();
    }

    /**
     * :: variable1 `:=` variable1 `+` number::1
     */
    public static Node newAssignPlus() {
        return new AssignPlusNode();
    }

    /**
     * :: variable0 `:=` program_name `(` variable1 `,` .. variablen `)`
     */
    public static Node newAssignCall(Set<String> programs) {
        return new AssignCallNode(programs);
    }

    /**
     * :: `INPUT` (string1 | variable1) ',' .. (stringn | variablen) [`,`]
     */
    public static Node newInput() {
        return new InputNode();
    }

    /**
     * :: `OUTPUT` (string1 | variable1) ',' .. (stringn | variablen) [`,`]
     */
    public static Node newPrint() {
        return new PrintNode();
    }

    /**
     * :: `LOOP` variable1 `DO` statement1 ';' .. statementn `END`
     */
    public static Node newLoop(Set<String> programs) {
        return new LoopNode(programs);
    }

    /**
     * :: `PROGRAM` program_name `(` variable1 `,` .. variablen `)` `DO` statement1 ';' .. statementn `END`
     */
    public static Node newDefinition(Set<String> programs) {
        return new DefinitionNode(programs);
    }
}
