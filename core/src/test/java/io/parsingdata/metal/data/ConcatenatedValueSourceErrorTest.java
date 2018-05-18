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

package io.parsingdata.metal.data;

import static java.util.stream.IntStream.generate;

import static org.junit.Assert.assertFalse;

import static io.parsingdata.metal.Shorthand.fold;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.tie;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.ParseStateFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import org.junit.Test;

import io.parsingdata.metal.Shorthand;
import io.parsingdata.metal.token.Token;

public class ConcatenatedValueSourceErrorTest {

    @Test
    public void recursiveConcatenatedSources() {
        final Environment env = env(stream(generate(() -> 0).limit(10000).toArray()));
        final Token token = rep(
            seq(
                any("val"),
                tie(any("val"), fold(ref("val"), Shorthand::cat)))
        );
        token.parse(env);
    }

    @Test
    public void emptyConcatenatedValueSource() {
        assertFalse(ConcatenatedValueSource.create(new ImmutableList<>()).isPresent());
    }

}
