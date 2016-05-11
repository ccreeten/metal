package io.parsingdata.util;

import static java.nio.charset.CodingErrorAction.REPORT;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import io.parsingdata.metal.data.ParseValue;

public class Stringifiers {

    public static final ValueStringifier DEFAULT = new ValueStringifier() {

        @Override
        public String toString(final ParseValue value) {
            final byte[] bytes = value.getValue();
            return String.format("\"0x%s #%d%s\"", Long.toHexString(value.offset).toUpperCase(), bytes.length, toHexArr(bytes, 5));
        }
    };

    public static final ValueStringifier ORACLE = new ValueStringifier() {

        @Override
        public String toString(final ParseValue value) {
            final Charset charset = value.getEncoding() == null ? StandardCharsets.UTF_8 : value.getEncoding().getCharset();

            final StringBuilder builder = new StringBuilder();
            builder.append('"');
            builder.append("[0x").append(Long.toHexString(value.offset).toUpperCase()).append("] ");
            builder.append(value.getFullName()).append(' ');

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

            try {
                final CharsetDecoder decoder = charset.newDecoder().onUnmappableCharacter(REPORT).onMalformedInput(REPORT);
                final CharBuffer buffer = decoder.decode(ByteBuffer.wrap(value.getValue()));

                if (validCharacterRange(buffer)) {
                    // Valid String
                    builder.append(isNumeric ? " " : "");
                    builder.append(buffer.toString().trim()); // Trim 0 bytes
                }
            }
            catch (final CharacterCodingException e) {
                // Not a valid string for this encoding
            }

            return builder.append('"').toString();
        }

        private boolean validCharacterRange(final CharBuffer buffer) {
            for (int i = 0; i < buffer.length(); i++) {
                final int character = buffer.get();
                if (character < ' ' || character > '~') {
                    return false;
                }
            }
            buffer.rewind();
            return true;
        }
    };

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
