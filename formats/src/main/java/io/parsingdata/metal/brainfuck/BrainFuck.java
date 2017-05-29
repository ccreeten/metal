package io.parsingdata.metal.brainfuck;

import static io.parsingdata.metal.Shorthand.add;
import static io.parsingdata.metal.Shorthand.and;
import static io.parsingdata.metal.Shorthand.cat;
import static io.parsingdata.metal.Shorthand.cho;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.currentOffset;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eqNum;
import static io.parsingdata.metal.Shorthand.eqStr;
import static io.parsingdata.metal.Shorthand.len;
import static io.parsingdata.metal.Shorthand.mod;
import static io.parsingdata.metal.Shorthand.not;
import static io.parsingdata.metal.Shorthand.pre;
import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.sub;
import static io.parsingdata.metal.Shorthand.tie;
import static io.parsingdata.metal.Shorthand.token;
import static io.parsingdata.metal.data.selection.ByName.getValue;
import static io.parsingdata.metal.expression.value.OptionalValue.wrap;

import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.encoding.Sign;
import io.parsingdata.metal.expression.value.ValueExpression;
import io.parsingdata.metal.token.Token;

/**
 * Assumes:
 *  - 8-bit unsigned cells, extending to the right up to length {@link #MEMORY_SIZE}
 *  - instruction pointer stays below {@link #INSTRUCTION_POINTER_SIZE}
 *  - bugs
 *  - some other things I forgot...
 * 
 * > Move the pointer to the right
 * < Move the pointer to the left
 * + Increment the memory cell under the pointer
 * - Decrement the memory cell under the pointer
 * . Output the character signified by the cell at the pointer
 * , Input a character and store it in the cell at the pointer
 * [ Jump past the matching ] if the cell under the pointer is 0
 * ] Jump back to the matching [ if the cell under the pointer is nonzero
 */
public final class BrainFuck {

    public static final Encoding ENCODING = new Encoding(Sign.UNSIGNED);
    
    public static final int MEMORY_SIZE = 256; // in bytes
    public static final int MEMORY_POINTER_SIZE = 4; // in bytes
    public static final int INSTRUCTION_POINTER_SIZE = 4; // in bytes

    public static final Token MOVE_RIGHT =
        seq(
            def(">", 1, eqStr(con(">"))),
            tie(
                def("memoryPointer", len(add(lastRef("memoryPointer"), con(1)))),
                add(lastRef("memoryPointer"), con(1))));

    public static final Token MOVE_LEFT =
        seq(
            def("<", 1, eqStr(con("<"))),
            tie(
                def("memoryPointer", len(sub(lastRef("memoryPointer"), con(1)))),
                sub(lastRef("memoryPointer"), con(1))));

    public static final Token INCREMENT =
        seq(
            def("+", 1, eqStr(con("+"))),
            tie(
                seq(
                    def("pre", lastRef("memoryPointer")),
                    def("val", 1),
                    def("post", sub(sub(con(MEMORY_SIZE), lastRef("memoryPointer")), con(1)))),
                lastRef("memory")),
            tie(
                def("memory", MEMORY_SIZE),
                cat(cat(
                        lastRef("pre"),
                        and(add(lastRef("val"), con(1)), con(0xFF))),
                        lastRef("post"))));

    public static final Token DECREMENT =
        seq(
            def("-", 1, eqStr(con("-"))),
            tie(
                seq(
                    def("pre", lastRef("memoryPointer")),
                    def("val", 1),
                    def("post", sub(sub(con(MEMORY_SIZE), lastRef("memoryPointer")), con(1)))),
                lastRef("memory")),
            tie(
                def("memory", MEMORY_SIZE),
                cat(cat(
                        lastRef("pre"),
                        and(sub(lastRef("val"), con(1)), con(0xFF))),
                        lastRef("post"))));

    public static final Token OUTPUT = def(".", 1, eqStr(con(".")));

    public static final Token INPUT = def(",", 1, eqStr(con(",")));

    public static final Token FORWARD_JUMP =
        seq(
            def("[", 1, eqStr(con("["))),
            tie(
                seq(
                    def("_", lastRef("memoryPointer")),
                    def("val", 1)),
                lastRef("memory")),
            pre(
                seq(
                    tie(
                        def("stack", add(add(len(lastRef("stack")), con(INSTRUCTION_POINTER_SIZE)), len(sub(currentOffset, con(1))))),
                        cat(cat(
                                con(new int[INSTRUCTION_POINTER_SIZE]),
                                sub(currentOffset, con(1))), // TODO: what if currentOffset size >= IPS size? (could just pre a def of IPS size when IPS - 1 is len IPS size)
                                lastRef("stack"))),
                    tie(
                        seq(
                            def("padding", mod(len(lastRef("stack")), con(INSTRUCTION_POINTER_SIZE))),
                            def("stack", sub(len(lastRef("stack")), mod(len(lastRef("stack")), con(INSTRUCTION_POINTER_SIZE))))),
                        lastRef("stack"))), 
                not(eqNum(lastRef("val"), con(0)))),
            pre( // TODO: could also just push stack, and on skipping step parse ] instead of ignore
                seq("skipping",
                        rep(
                            cho(
                                seq(def("_", 1, eqStr(con("["))), token("skipping")),
                                def("_", 1, not(eqStr(con("]")))))),
                        def("_", 1)),
                    eqNum(lastRef("val"), con(0))));

    public static final Token BACKWARD_JUMP =
        seq(
            def("]", 1, eqStr(con("]"))),
            tie(
                seq(
                    def("_", lastRef("memoryPointer")),
                    def("val", 1)),
                lastRef("memory")),
            tie(
                seq(
                    def("instructionPointer", INSTRUCTION_POINTER_SIZE),
                    def("stack", sub(len(lastRef("stack")), con(INSTRUCTION_POINTER_SIZE)))),
                lastRef("stack")),
            pre(
                sub(token("instructions"), lastRef("instructionPointer")),
                not(eqNum(lastRef("val"), con(0)))));

    public static final Token OTHER_CHARACTER = def("other", 1);

    public static final Token INIT_ENVIRONMENT =
        seq(
            tie(def("memoryPointer", MEMORY_POINTER_SIZE), con(new int[MEMORY_POINTER_SIZE])),
            tie(def("memory", MEMORY_SIZE), con(new int[MEMORY_SIZE])),
            tie(def("stack", INSTRUCTION_POINTER_SIZE), con(new int[INSTRUCTION_POINTER_SIZE])));

    public static final Token INTERPRETER =
        seq(INIT_ENVIRONMENT,
            rep("instructions",
                cho(MOVE_RIGHT, 
                    MOVE_LEFT, 
                    INCREMENT, 
                    DECREMENT, 
                    OUTPUT, 
                    INPUT, 
                    FORWARD_JUMP, 
                    BACKWARD_JUMP, 
                    OTHER_CHARACTER)));
    
    public static ValueExpression lastRef(final String name) {
        return (environment, encoding) -> wrap(ImmutableList.create(getValue(environment.order, name)));
    }
}
