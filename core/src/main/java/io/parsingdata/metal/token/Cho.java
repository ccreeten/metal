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

package io.parsingdata.metal.token;

import static io.parsingdata.metal.Util.checkContainsNoNulls;
import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.Util.failure;
import static io.parsingdata.metal.Util.success;
import static io.parsingdata.metal.data.ImmutableList.create;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.encoding.Encoding;

/**
 * A {@link Token} that specifies a choice out of a list of tokens.
 *<p>
 * A Cho consists of a list of <code>tokens</code>. If none of the tokens
 * succeed, the Cho fails. If any token succeeds, the Cho succeeds. Precedence
 * is from left to right.
 */
public class Cho extends Token {

    public final ImmutableList<Token> tokens;

    public Cho(final String name, final Encoding encoding, final Token token1, final Token token2, final Token... additionalTokens) {
        super(name, encoding);
        this.tokens = create(checkContainsNoNulls(additionalTokens, "tokens"))
                .add(checkNotNull(token2, "token2"))
                .add(checkNotNull(token1, "token1"));
    }

    @Override
    protected Optional<Environment> parseImpl(final String scope, final Environment environment, final Encoding encoding) throws IOException {
        final Optional<Environment> result = iterate(scope, environment.addBranch(this), encoding, tokens);
        if (result.isPresent()) {
            return success(result.get().closeBranch());
        }
        return failure();
    }

    private Optional<Environment> iterate(final String scope, final Environment environment, final Encoding encoding, final ImmutableList<Token> list) throws IOException {
        if (list.isEmpty()) {
            return failure();
        }
        final Optional<Environment> result = list.head.parse(scope, environment, encoding);
        if (result.isPresent()) { return result; }
        return iterate(scope, environment, encoding, list.tail);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + makeNameFragment() + tokens + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj)
            && Objects.equals(tokens, ((Cho)obj).tokens);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), tokens);
    }

}
