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

import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.Util.failure;
import static io.parsingdata.metal.Util.success;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.Expression;

/**
 * A {@link Token} that specifies a precondition for parsing a nested token.
 * <p>
 * A Pre consists of a <code>token</code> (a {@link Token}) and a
 * <code>predicate</code> (an {@link Expression}). First
 * <code>predicate</code> is evaluated. If it evaluates to <code>true</code>,
 * the token is parsed. Parsing this token will only succeed if the
 * <code>predicate</code> evaluates to <code>true</code> and if parsing the
 * nested token succeeds.
 *
 * @see Expression
 */
public class Pre extends Token {

    public final Token token;
    public final Expression predicate;

    public Pre(final String name, final Token token, final Expression predicate, final Encoding encoding) {
        super(name, encoding);
        this.token = checkNotNull(token, "token");
        this.predicate = checkNotNull(predicate, "predicate");
    }

    @Override
    protected Optional<Environment> parseImpl(final String scope, final Environment environment, final Encoding encoding) throws IOException {
        if (!predicate.eval(environment.order, encoding)) {
            return failure();
        }
        final Optional<Environment> result = token.parse(scope, environment.addBranch(this), encoding);
        return result.isPresent() ? success(result.get().closeBranch()) : failure();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + makeNameFragment() + token + ", " + predicate + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj)
            && Objects.equals(token, ((Pre)obj).token)
            && Objects.equals(predicate, ((Pre)obj).predicate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), token, predicate);
    }

}
