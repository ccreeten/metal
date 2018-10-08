/*
 * Copyright 2013-2016 Netherlands Forensic Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.parsingdata.metal;

import static org.junit.Assert.assertFalse;

import static io.parsingdata.metal.AutoEqualityTest.DUMMY_STREAM;
import static io.parsingdata.metal.Shorthand.add;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.div;
import static io.parsingdata.metal.Shorthand.neg;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.repn;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.data.ParseState.createFromByteStream;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.ParseStateFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Optional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.token.Token;

public class ErrorsTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void noValueForSize() {
        thrown = ExpectedException.none();
        // Basic division by zero.
        final Token token = def("a", div(con(1), con(0)));
        assertFalse(token.parse(env(stream(1))).isPresent());
        // Try to negate division by zero.
        final Token token2 = def("a", neg(div(con(1), con(0))));
        assertFalse(token2.parse(env(stream(1))).isPresent());
        // Add one to division by zero.
        final Token token3 = def("a", add(div(con(1), con(0)), con(1)));
        assertFalse(token3.parse(env(stream(1))).isPresent());
        // Add division by zero to one.
        final Token token4 = def("a", add(con(1), div(con(1), con(0))));
        assertFalse(token4.parse(env(stream(1))).isPresent());
    }

    @Test
    public void multiValueInRepN() {
        final Token dummy = any("a");
        final Token multiRepN =
            seq(any("b"),
                any("b"),
                repn(dummy, ref("b"))
            );
       Optional<ParseState> result = multiRepN.parse(env(stream(2, 2, 2, 2)));
       assertFalse(result.isPresent());
    }

    @Test
    public void parseStateWithNegativeOffset() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Argument offset may not be negative.");
        createFromByteStream(DUMMY_STREAM, BigInteger.valueOf(-1));
    }

}
