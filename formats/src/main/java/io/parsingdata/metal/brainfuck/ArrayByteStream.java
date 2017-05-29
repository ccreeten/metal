package io.parsingdata.metal.brainfuck;

import java.io.IOException;

import io.parsingdata.metal.data.ByteStream;

public class ArrayByteStream implements ByteStream {

    private final byte[] _buffer;

    public ArrayByteStream(final byte[] buffer) {
        _buffer = buffer.clone();
    }

    @Override
    public int read(final long offset, final byte[] buffer) throws IOException {
        if (offset + buffer.length > _buffer.length) {
            return 0;
        }
        System.arraycopy(_buffer, (int) offset, buffer, 0, buffer.length);
        return buffer.length;
    }
}