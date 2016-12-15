package io.parsingdata.metal.select;

public final class InvalidOperationException extends RuntimeException {

    static final long serialVersionUID = 1L;

    InvalidOperationException(final String message) {
        super(message);
    }
}