/*
 * Copyright 2013-2017 Netherlands Forensic Institute
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

package io.parsingdata.metal.data.selection;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import static io.parsingdata.metal.Shorthand.opt;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.io.IOException;

import org.junit.Test;

import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.token.Token;

public class ByNameTest {

    private static final Token TOKEN_1 = opt("token1", any("value"));
    private static final Token TOKEN_2 = opt("token2", any("value"));
    private static final Token SEQUENCE = seq(TOKEN_1, TOKEN_2);

    @Test
    public void testDefinitionByName() throws IOException {
        final ParseResult result = SEQUENCE.parse(stream(0, 1), enc());
        assertThat(TOKEN_1, is(equalTo(ByName.getDefinition(result.environment.order, "token1"))));
        assertThat(TOKEN_2, is(equalTo(ByName.getDefinition(result.environment.order, "token2"))));
    }
}
