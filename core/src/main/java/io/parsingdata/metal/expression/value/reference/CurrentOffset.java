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

package io.parsingdata.metal.expression.value.reference;

import static io.parsingdata.metal.expression.value.ConstantFactory.createFromNumeric;
import static io.parsingdata.metal.util.EqualityCheck.sameClass;

import java.util.Optional;

import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;

/**
 * A {@link ValueExpression} that represents the current offset in the
 * {@link ParseState}.
 */
public class CurrentOffset implements ValueExpression {

    @Override
    public ImmutableList<Optional<Value>> eval(final ParseState parseState, final Encoding encoding) {
        return ImmutableList.create(Optional.of(createFromNumeric(parseState.offset, new Encoding())));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public boolean equals(final Object obj) {
        return sameClass(this, obj).evaluate();
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
