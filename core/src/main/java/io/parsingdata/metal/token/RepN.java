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
import static io.parsingdata.metal.token.util.Trampoline.base;
import static io.parsingdata.metal.token.util.Trampoline.recurse;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;
import io.parsingdata.metal.token.util.Trampoline;

/**
 * A {@link Token} that specifies a bounded repetition of a token.
 * <p>
 * A RepN consists of a <code>token</code> (a {@link Token}) and an
 * <code>n</code> (a {@link ValueExpression}). First <code>n</code> is
 * evaluated. Parsing fails if it does not evaluate to a single value. The
 * token is then parsed for an amount of times equal to the evaluated value of
 * <code>n</code>. RepN succeeds if this succeeds.
 *
 * @see Rep
 * @see ValueExpression
 */
public class RepN extends Token {

    public final Token token;
    public final ValueExpression n;

    public RepN(final String name, final Token token, final ValueExpression n, final Encoding encoding) {
        super(name, encoding);
        this.token = checkNotNull(token, "token");
        this.n = checkNotNull(n, "n");
    }

    @Override
    protected Optional<Environment> parseImpl(final String scope, final Environment environment, final Encoding encoding) throws IOException {
        final ImmutableList<Optional<Value>> counts = n.eval(environment.order, encoding);
        if (counts.size != 1 || !counts.head.isPresent()) {
            return failure();
        }
        final Optional<Environment> result = iterate(scope, Optional.of(environment.addBranch(this)), encoding, counts.head.get().asNumeric().longValue()).computeResult();
        if (result.isPresent()) {
            return success(result.get().closeBranch());
        }
        return failure();
    }

    private Trampoline<Optional<Environment>> iterate(final String scope, final Optional<Environment> environment, final Encoding encoding, final long count) throws IOException {
        if (environment.isPresent()) {
            if (count <= 0) {
                return base(() -> success(environment.get()));
            } else {
                return recurse(() -> iterate(scope, token.parse(scope, environment.get(), encoding), encoding, count - 1));
            }
        }
        return base(Util::failure);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + makeNameFragment() + token + "," + n + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj)
            && Objects.equals(token, ((RepN)obj).token)
            && Objects.equals(n, ((RepN)obj).n);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), token, n);
    }

}
