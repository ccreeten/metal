package io.parsingdata.util;

import java.math.BigInteger;
import java.util.Arrays;

import org.apache.commons.codec.binary.Hex;

import io.parsingdata.metal.data.ParseValue;

public class Stringifiers {

    /** Returns toString of the parseValue. */
    public static final Stringifier BASIC = new Stringifier() {

        @Override
        public String toString(final ParseValue value) {
            final String string = value.toString();
            return string.substring(0, Math.min(string.length(), 500));
        }
    };

    /** Returns byte count and first 5 bytes, together with offset. */
    public static final Stringifier BYTES = new Stringifier() {

        @Override
        public String toString(final ParseValue value) {
            final byte[] bytes = value.getValue();
            return String.format("\"0x%02X #%d%s\"", value.offset, bytes.length, toHexArr(bytes, 5));
        }
    };

    /** Returns the value name, together with offset. */
    public static final Stringifier NAMES = new Stringifier() {

        @Override
        public String toString(final ParseValue value) {
            return String.format("0x%s (%d)| %s ",
                                 Long.toString(value.getOffset(), 16).toUpperCase(),
                                 value.getOffset(),
                                 value.getName());

        }
    };

    /** Tries to guess the type of the data and return a logical representation for it. */
    public static final Stringifier ORACLE = new Stringifier() {

        @Override
        public String toString(final ParseValue value) {
            final StringBuilder builder = new StringBuilder()
                .append("0x")
                .append(Long.toHexString(value.offset).toUpperCase())
                .append("| ")
                .append(value.getFullName())
                .append(":");

            final boolean isNumeric =
                value.asNumeric().compareTo(BigInteger.valueOf(Long.MIN_VALUE)) >= 0 &&
                value.asNumeric().compareTo(BigInteger.valueOf(Long.MAX_VALUE)) <= 0;

            if (isNumeric) {
                builder.append(String.format(" numeric: [%d]", value.asNumeric().longValue()));
            }

            final String string = value.asString().trim();
            if (isTextualString(string)) {
                builder.append(String.format(" string: [%s]", string));
            }

            return builder.append(" " + toHexArr(value.getValue(), 8)).toString();
        }
    };

    /** Tries to guess the type of the data and return a logical representation for it. */
    public static final Stringifier GRAPHVIZ = new Stringifier() {

        @Override
        public String toString(final ParseValue value) {
            final StringBuilder builder = new StringBuilder()
                .append("\"0x")
                .append(Long.toHexString(value.offset).toUpperCase())
                .append("| ")
                .append(value.getFullName())
                .append(":");

            final boolean isNumeric =
                value.asNumeric().compareTo(BigInteger.valueOf(Long.MIN_VALUE)) >= 0 &&
                value.asNumeric().compareTo(BigInteger.valueOf(Long.MAX_VALUE)) <= 0;

            if (isNumeric) {
                builder.append(String.format(" numeric: [%d]", value.asNumeric().longValue()));
            }

            final String string = value.asString().trim();
            final boolean isTextualString = !string.isEmpty() && isTextualString(string);

            if (isTextualString) {
                builder.append(String.format(" string: [%s]", string));
            }

            if (!isNumeric && !isTextualString) {
                builder.append(" bytes: " + toHexArr(value.getValue(), 8));
            }
            return builder.append("\"").toString();
        }
    };


    /** Returns numeric/string/bytes, together with offset and name. */
    public static final Stringifier ALL = new Stringifier() {

        @Override
        public String toString(final ParseValue value) {
            final String string = String.format("0x%s (%d)| %s - numeric: [%d] - string: [%s] - bytes [%s]",
                                                Long.toString(value.getOffset(), 16).toUpperCase(),
                                                value.getOffset(),
                                                value.getName(),
                                                value.asNumeric(),
                                                value.asString(),
                                                Hex.encodeHexString(value.getValue()).toUpperCase());
            return string.substring(0, Math.min(string.length(), 500));
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
