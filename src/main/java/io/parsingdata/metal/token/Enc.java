/*
 * Copyright (c) 2016 Netherlands Forensic Institute
 * All rights reserved.
 */
package io.parsingdata.metal.token;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Util.checkNotNull;

import java.io.IOException;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.encoding.Encoding;

public class Enc extends Token {

    private final Token _op;

    public Enc(final Token op, final Encoding enc) {
        super(enc);
        _op = checkNotNull(op, "op");
    }

    @Override
    protected ParseResult parseImpl(final String scope, final Environment env, final Encoding enc) throws IOException {
        return _op.parse(scope, env, enc);
    }

    public static void main(final String[] args) {
        con(new byte[0]);
    }

}
