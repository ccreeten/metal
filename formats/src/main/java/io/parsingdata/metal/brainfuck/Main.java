package io.parsingdata.metal.brainfuck;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import io.parsingdata.metal.brainfuck.BrainFuckCallbacks.DebugCallback;
import io.parsingdata.metal.brainfuck.BrainFuckCallbacks.InputCallback;
import io.parsingdata.metal.brainfuck.BrainFuckCallbacks.OutputCallback;
import io.parsingdata.metal.data.ByteStream;
import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.callback.Callbacks;

public final class Main {

    public static void main(final String[] args) throws IOException {
        final String program = ExamplePrograms.HELLO_WORLD;
        final InputStream input = System.in;
        final OutputStream output = System.out;

        runProgram(program, input, output);
        // debugProgram(program, input, output);
    }

    private static void runProgram(final String program, final InputStream input, final OutputStream output) throws IOException {
        final ByteStream stream = new ArrayByteStream(program.getBytes(StandardCharsets.UTF_8));
        final Environment environment = new Environment(stream, Callbacks.create().add(BrainFuck.INPUT, new InputCallback(input)).add(BrainFuck.OUTPUT, new OutputCallback(program, output)));
        BrainFuck.INTERPRETER.parse(environment, BrainFuck.ENCODING);
    }

    private static void debugProgram(final String program, final InputStream input, final OutputStream output) throws IOException {
        final ByteStream stream = new ArrayByteStream(program.getBytes(StandardCharsets.UTF_8));
        final Environment environment = new Environment(stream, Callbacks.create().add(BrainFuck.INPUT, new InputCallback(input)).add(new DebugCallback(program, output)));
        BrainFuck.INTERPRETER.parse(environment, BrainFuck.ENCODING);
    }
}
