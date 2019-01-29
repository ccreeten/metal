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

package io.parsingdata.metal.expression.comparison;

import static java.util.function.Function.identity;

import static io.parsingdata.metal.Trampoline.complete;
import static io.parsingdata.metal.Trampoline.intermediate;
import static io.parsingdata.metal.Util.checkNotNull;

import java.util.Objects;
import java.util.Optional;

import io.parsingdata.metal.Trampoline;
import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.Expression;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;

/**
 * Base class for all ComparisonExpression implementations.
 * <p>
 * Specifies two input {@link ValueExpression}s that are to be evaluated and
 * their results compared. Handles evaluation of inputs and resulting lists.
 * The abstract {@link #compare(Value, Value)} method is then called for
 * individual values.
 * <p>
 * The {@link #value} argument may be <code>null</code>, in which case it is
 * not evaluated and the output value is substituted with a list containing
 * only the {@link Value} most recently added to the {@link ParseState}.
 */
public abstract class ComparisonExpression implements Expression {

    public final ValueExpression value;
    public final ValueExpression predicate;

    public ComparisonExpression(final ValueExpression value, final ValueExpression predicate) {
        this.value = value;
        this.predicate = checkNotNull(predicate, "predicate");
    }

    @Override
    public boolean eval(final ParseState parseState, final Encoding encoding) {
        final Optional<ImmutableList<Value>> values = value == null ? parseState.order.current().flatMap(current -> Optional.of(ImmutableList.create(current))) : value.eval(parseState, encoding);
        if (!values.isPresent() || values.get().isEmpty()) {
            return false;
        }
        final Optional<ImmutableList<Value>> predicates = predicate.eval(parseState, encoding);
        if (!predicates.isPresent() || values.get().size != predicates.get().size) {
            return false;
        }
        return compare(values.get(), predicates.get()).computeResult();
    }

    private Trampoline<Boolean> compare(final ImmutableList<Value> currents, final ImmutableList<Value> predicates) {
        final boolean headResult = compare(currents.head, predicates.head);
        if (!headResult || currents.tail.isEmpty()) {
            return complete(() -> headResult);
        }
        return intermediate(() -> compare(currents.tail, predicates.tail));
    }

    public abstract boolean compare(final Value left, final Value right);

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + (value == null ? "" : value + ",") + predicate + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
            && Objects.equals(value, ((ComparisonExpression)obj).value)
            && Objects.equals(predicate, ((ComparisonExpression)obj).predicate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), value, predicate);
    }

}
