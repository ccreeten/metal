package io.parsingdata.metal.util;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

import io.parsingdata.metal.data.ByteStream;
import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.stream.ArrayByteStream;
import io.parsingdata.metal.util.stream.FileByteStream;

public final class ParseUtil {

    /**
     * Parse a byte array with given format.
     *
     * @see {@link ParseUtil#parse(ByteStream, Token)}.
     */
    public static ParseResult parse(final byte[] array, final Token format) throws IOException {
        return parse(new ArrayByteStream(array), format);
    }

    /**
     * Parse a byte array with given format in given encoding.
     *
     * @see {@link ParseUtil#parse(ByteStream, Token, Encoding)}.
     */
    public static ParseResult parse(final byte[] array, final Token format, final Encoding encoding) throws IOException {
        return parse(new ArrayByteStream(array), format, encoding);
    }

    /**
     * Parse a byte array with given format in given encoding, starting at a certain offset.
     *
     * @see {@link ParseUtil#parse(ByteStream, long, Token, Encoding)}.
     */
    public static ParseResult parse(final byte[] array, final long offset, final Token format, final Encoding encoding) throws IOException {
        return parse(new ArrayByteStream(array), offset, format, encoding);
    }

    /**
     * Parse a {@link File} with given format.
     *
     * @see {@link ParseUtil#parse(ByteStream, Token)}.
     */
    public static ParseResult parse(final File file, final Token format) throws IOException {
        return parse(new FileByteStream(file), format);
    }

    /**
     * Parse a {@link File} with given format in given encoding.
     *
     * @see {@link ParseUtil#parse(ByteStream, Token, Encoding)}.
     */
    public static ParseResult parse(final File file, final Token format, final Encoding encoding) throws IOException {
        return parse(new FileByteStream(file), format, encoding);
    }

    /**
     * Parse a {@link File} with given format in given encoding, starting at a certain offset.
     *
     * @see {@link ParseUtil#parse(ByteStream, long, Token, Encoding)}.
     */
    public static ParseResult parse(final File file, final long offset, final Token format, final Encoding encoding) throws IOException {
        return parse(new FileByteStream(file), offset, format, encoding);
    }

    /**
    * Parses a {@link ByteStream} with given format.
    *
    * It uses a default encoding and starts at offset 0.
    *
    * @param stream the source to read and parse from
    * @param format the format to parse with
    * @return a {@link ParseResult}
    * @throws IOException whenever {@link Token#parse(Environment, Encoding)} throws one
    */
    public static ParseResult parse(final ByteStream stream, final Token format) throws IOException {
        return parse(stream, format, new Encoding());
    }

    /**
    * Parses a {@link ByteStream} with given format in given encoding.
    *
    * It starts at offset 0.
    *
    * @param stream the source to read and parse from
    * @param format the format to parse with
    * @param encoding the encoding of the data to parse
    * @return a {@link ParseResult}
    * @throws IOException whenever {@link Token#parse(Environment, Encoding)} throws one
    */
    public static ParseResult parse(final ByteStream stream, final Token format, final Encoding encoding) throws IOException {
        return parse(stream, 0, format, encoding);
    }

    /**
    * Parses a {@link ByteStream} with given format in given encoding, starting at a certain offset.
    *
    * @param stream the source to read and parse from
    * @param offset the offset to start parsing from
    * @param format the format to parse with
    * @param encoding the encoding of the data to parse
    * @return a {@link ParseResult}
    * @throws IOException whenever {@link Token#parse(Environment, Encoding)} throws one
    */
    public static ParseResult parse(final ByteStream source, final long offset, final Token format, final Encoding encoding) throws IOException {
        return format.parse(new Environment(source, offset), encoding);
    }

    /** See {@link GraphUtil#getByte(ParseGraph, String)}. */
    public static byte getByte(final ParseResult parse, final String name) {
        return io.parsingdata.metal.util.GraphUtil.getByte(parse.getEnvironment().order, name);
    }

    /** See {@link GraphUtil#getBytes(ParseGraph, String)}. */
    public static byte[] getBytes(final ParseResult parse, final String name) {
        return io.parsingdata.metal.util.GraphUtil.getBytes(parse.getEnvironment().order, name);
    }

    /** See {@link GraphUtil#getInt(ParseGraph, String)}. */
    public static int getInt(final ParseResult parse, final String name) {
        return io.parsingdata.metal.util.GraphUtil.getInt(parse.getEnvironment().order, name);
    }

    /** See {@link GraphUtil#getLong(ParseGraph, String)}. */
    public static long getLong(final ParseResult parse, final String name) {
        return io.parsingdata.metal.util.GraphUtil.getLong(parse.getEnvironment().order, name);
    }

    /** See {@link GraphUtil#getBigInt(ParseGraph, String)}. */
    public static BigInteger getBigInt(final ParseResult parse, final String name) {
        return io.parsingdata.metal.util.GraphUtil.getBigInt(parse.getEnvironment().order, name);
    }

    /** See {@link GraphUtil#getString(ParseGraph, String)}. */
    public static String getString(final ParseResult parse, final String name) {
        return io.parsingdata.metal.util.GraphUtil.getString(parse.getEnvironment().order, name);
    }
}
