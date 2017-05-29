package io.parsingdata.metal.brainfuck;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.data.Slice;
import io.parsingdata.metal.data.callback.Callback;
import io.parsingdata.metal.data.selection.ByName;
import io.parsingdata.metal.token.Token;

public final class BrainFuckCallbacks {

    private BrainFuckCallbacks() {
    }

    public static class InputCallback implements Callback {

        private final Scanner scanner;

        public InputCallback(final InputStream input) {
            scanner = new Scanner(input, StandardCharsets.UTF_8.name());
        }

        @Override
        public void handle(final Token token, final ParseResult result) {
            if (result.succeeded && token == BrainFuck.INPUT) {
                try {
                    final int value = scanner.nextByte();
                    final Slice slice = ByName.getValue(result.environment.order, "memory").slice;
                    final byte[] updatedMemory = ByName.getValue(result.environment.order, "memory").getValue();
                    updatedMemory[ByName.getValue(result.environment.order, "memoryPointer").asNumeric().intValue()] = (byte) value;
                    final Field field = Slice.class.getDeclaredField("data");
                    field.setAccessible(true);
                    field.set(slice, updatedMemory);
                } catch (final Throwable t) {
                    throw new RuntimeException(t);
                }
            }
        }
    }

    public static class OutputCallback implements Callback {

        private final int programLength;
        private final PrintWriter writer;

        public OutputCallback(final String program, final OutputStream output) {
            programLength = program.length();
            writer = new PrintWriter(output);
        }

        @Override
        public void handle(final Token token, final ParseResult result) {
            if (result.succeeded && token == BrainFuck.OUTPUT) {
                writer.print((char) ByName.getValue(result.environment.order, "memory").getValue()[ByName.getValue(result.environment.order, "memoryPointer").asNumeric().intValue()]);
                writer.flush();
            }
            // TODO: not sure why it prints more... because of the 'backtracking' successes?
            if (result.environment.offset == programLength && token == BrainFuck.OUTPUT) {
                System.exit(0);
            }
        }
    }

    public static class DebugCallback implements Callback {

        private final int programLength;
        private final PrintWriter writer;

        private String prevState = null;

        public DebugCallback(final String program, final OutputStream output) {
            programLength = program.length();
            writer = new PrintWriter(output);
        }

        @Override
        public void handle(final Token token, final ParseResult result) {
            if (ByName.getValue(result.environment.order, "memory") != null) {
                final byte[] bytes = ByName.getValue(result.environment.order, "memory").getValue();
                final String[] ubytes = new String[bytes.length];
                for (int i = 0; i < bytes.length; i++) {
                    final int val = bytes[i] & 0xFF;
                    ubytes[i] = String.format("%02X", val);
                }
                final int pointer = ByName.getValue(result.environment.order, "memoryPointer").asNumeric().intValue();
                String string = Arrays.toString(ubytes) + "\n ";
                for (int i = 0; i < pointer; i++) {
                    string += ("    ");
                }
                string += ("^");
                if (!string.equals(prevState)) {
                    writer.println(string);
                    writer.flush();
                    prevState = string;
                }
            }
            if (result.environment.offset == programLength && token == BrainFuck.OUTPUT) {
                System.exit(0);
            }
        }
    }
}
