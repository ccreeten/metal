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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    public List<Boolean> eval(final ParseGraph graph, final Encoding encoding) {
        final ImmutableList<Optional<Value>> values = value == null ? ImmutableList.create(Optional.of(graph.current())) : value.eval(graph, encoding);
        if (values.isEmpty()) { return Collections.emptyList(); }
        final ImmutableList<Optional<Value>> predicates = predicate.eval(graph, encoding);
        if (values.size != predicates.size) { return Collections.singletonList(false); }
        return compare(values, predicates, new ArrayList<>()).computeResult();
    }

    private SafeTrampoline<List<Boolean>> compare(final ImmutableList<Optional<Value>> currents, final ImmutableList<Optional<Value>> predicates, final List<Boolean> bools) {
        if(currents.isEmpty()) { return complete(() -> bools); }
        if (!currents.head.isPresent() || !predicates.head.isPresent()) { return intermediate(() -> compare(currents.tail, predicates.tail, add(bools, false))); }
        final boolean headResult = compare(currents.head.get(), predicates.head.get());
        return intermediate(() -> compare(currents.tail, predicates.tail, add(bools, headResult)));
    }

    private List<Boolean> add(final List<Boolean> bools, final boolean bool) {
        bools.add(bool);
        return bools;
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
