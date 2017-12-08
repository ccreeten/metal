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

import static io.parsingdata.metal.Trampoline.complete;
import static io.parsingdata.metal.Trampoline.intermediate;
import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.Util.success;

import java.util.Objects;
import java.util.Optional;

import io.parsingdata.metal.Trampoline;
import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.encoding.Encoding;

/**
 * A {@link Token} that specifies a possible repetition of a token.
 * <p>
 * A Rep consists of a single <code>token</code>. A parse is attempted as long
 * as parsing succeeds. Since any amount of iterations is acceptable
 * (including zero), parsing a Rep will always succeed.
 *
 * @see RepN
 */
public class Rep extends Token {

    public final Token token;

    public Rep(final String name, final Token token, final Encoding encoding) {
        super(name, encoding);
        this.token = checkNotNull(token, "token");
    }

    @Override
    protected Optional<ParseState> parseImpl(final Environment environment) {
        return iterate(environment.addBranch(this)).computeResult();
    }

    private Trampoline<Optional<ParseState>> iterate(final Environment environment) {
        return token
            .parse(environment)
            .map(nextParseState -> intermediate(() -> iterate(environment.withParseState(nextParseState))))
            .orElseGet(() -> complete(() -> success(environment.parseState.closeBranch())));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + makeNameFragment() + token + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equalityOf(this, obj).check(rep -> rep.token).evaluate();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), token);
    }

}
