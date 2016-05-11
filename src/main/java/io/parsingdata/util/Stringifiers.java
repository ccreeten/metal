package io.parsingdata.util;

import java.util.Arrays;

import io.parsingdata.metal.data.ParseValue;

public class Stringifiers {

    public static final ValueStringifier DEFAULT = new ValueStringifier() {

        @Override
        public String toString(final ParseValue value) {
            final byte[] bytes = value.getValue();
            return String.format("\"0x%02X #%d%s\"", value.offset, bytes.length, toHexArr(bytes, 5));
        }
    };

    public static final ValueStringifier ORACLE = new ValueStringifier() {

        @Override
        public String toString(final ParseValue value) {
            final StringBuilder builder = new StringBuilder();
            builder.append('"');
            builder.append("[0x").append(Long.toHexString(value.offset).toUpperCase()).append("|");
            builder.append(value.getFullName()).append("] ");

            final boolean isNumeric = value.getValue().length == 1 || // byte
            value.getValue().length == 4 || // int
            value.getValue().length == 8; // long

            if (isNumeric) {
                // Possible Integer or Long
                builder.append("0x");
                builder.append(Long.toHexString(value.asNumeric().longValue()).toUpperCase());
                builder.append(' ');
                builder.append(value.asNumeric().longValue());
                builder.append('L');
            }

            final String string = value.asString().trim();
            if (isTextualString(string)) {
                // Valid String
                builder.append(isNumeric ? " " : "");
                builder.append(string);
            }
            return builder.append('"').toString();
        }
    };

    private static boolean isTextualString(final String string) {
        for (final char c : string.toCharArray()) {
            if (c < ' ' || c > '~') {
                return false;
            }
        }
        return true;
    }

    private static String toHexArr(final byte[] bytes, final int maxLength) {
        return toHexArr(Arrays.copyOfRange(bytes, 0, Math.min(bytes.length, maxLength)));
    }

    private static String toHexArr(final byte[] bytes) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < bytes.length - 1; i++) {
            sb.append(String.format("%02X ", bytes[i]));
        }
        sb.append(String.format("%02X]", bytes[bytes.length - 1]));
        return sb.toString();
    }
}
