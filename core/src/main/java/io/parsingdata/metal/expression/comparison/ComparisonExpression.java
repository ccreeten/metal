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

import static io.parsingdata.metal.SafeTrampoline.complete;
import static io.parsingdata.metal.SafeTrampoline.intermediate;
import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.data.transformation.Reversal.reverse;
import static java.util.function.Function.identity;

import java.util.Objects;
import java.util.Optional;

import io.parsingdata.metal.SafeTrampoline;
import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseGraph;
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
 * only the {@link Value} most recently added to the {@link Environment}.
 */
public abstract class ComparisonExpression implements Expression {

    public final ValueExpression value;
    public final ValueExpression predicate;

    public ComparisonExpression(final ValueExpression value, final ValueExpression predicate) {
        this.value = value;
        this.predicate = checkNotNull(predicate, "predicate");
    }

    @Override
    public ImmutableList<Boolean> eval(final ParseGraph graph, final Encoding encoding) {
        return compareLists(value == null ? ImmutableList.create(graph.current().map(identity())) : value.eval(graph, encoding), predicate.eval(graph, encoding));
    }
    
    private ImmutableList<Boolean> compareLists(final ImmutableList<Optional<Value>> values, final ImmutableList<Optional<Value>> predicates) {
        return reverse(padList(compareLists(values, predicates, new ImmutableList<>()).computeResult(), Math.abs(values.size - predicates.size)).computeResult());
    }
    
    private SafeTrampoline<ImmutableList<Boolean>> compareLists(final ImmutableList<Optional<Value>> values, final ImmutableList<Optional<Value>> predicates, final ImmutableList<Boolean> results) {
        if (values.isEmpty() || predicates.isEmpty()) { return complete(() -> results); }
        return intermediate(() -> compareLists(values.tail, predicates.tail, results.add(compare(values.head, predicates.head))));
    }

    private SafeTrampoline<ImmutableList<Boolean>> padList(final ImmutableList<Boolean> list, final long size) {
        if (size <= 0) { return complete(() -> list); }
        return intermediate(() -> padList(list.add(false), size - 1));
    }

    private boolean compare(final Optional<Value> left, final Optional<Value> right) {
        if (!left.isPresent() || !right.isPresent()) { return false; }
        return compare(left.get(), right.get());
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
        return Objects.hash(value, predicate);
    }

}
