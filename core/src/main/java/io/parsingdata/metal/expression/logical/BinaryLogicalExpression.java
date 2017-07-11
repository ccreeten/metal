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

package io.parsingdata.metal.expression.logical;

import static io.parsingdata.metal.SafeTrampoline.complete;
import static io.parsingdata.metal.SafeTrampoline.intermediate;
import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.data.transformation.Reversal.reverse;

import java.util.Objects;

import io.parsingdata.metal.SafeTrampoline;
import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.Expression;

/**
 * Base class for {@link LogicalExpression} implementations with two operands.
 * <p>
 * A BinaryLogicalExpression has two operands, <code>left</code> and
 * <code>right</code> (both {@link Expression}s). Both operands are evaluated,
 * their results combined using the operator the concrete expression
 * implements and then returned.
 */
public abstract class BinaryLogicalExpression implements LogicalExpression {

    public final Expression left;
    public final Expression right;

    public BinaryLogicalExpression(final Expression left, final Expression right) {
        this.left = checkNotNull(left, "left");
        this.right = checkNotNull(right, "right");
    }

    @Override
    public ImmutableList<Boolean> eval(final ParseGraph graph, final Encoding encoding) {
        return evalLists(left.eval(graph, encoding), right.eval(graph, encoding));
    }
    
    private ImmutableList<Boolean> evalLists(final ImmutableList<Boolean> lefts, final ImmutableList<Boolean> rights) {
        return reverse(padList(evalLists(lefts, rights, new ImmutableList<>()).computeResult(), Math.abs(lefts.size - rights.size)).computeResult());
    }
    
    private SafeTrampoline<ImmutableList<Boolean>> evalLists(final ImmutableList<Boolean> lefts, final ImmutableList<Boolean> rights, final ImmutableList<Boolean> results) {
        if (lefts.isEmpty() || rights.isEmpty()) { return complete(() -> results); }
        return intermediate(() -> evalLists(lefts.tail, rights.tail, results.add(eval(lefts.head, rights.head))));
    }

    private SafeTrampoline<ImmutableList<Boolean>> padList(final ImmutableList<Boolean> list, final long size) {
        if (size <= 0) { return complete(() -> list); }
        return intermediate(() -> padList(list.add(false), size - 1));
    }

    public abstract boolean eval(final boolean left, final boolean right);

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + left + "," + right + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
            && Objects.equals(left, ((BinaryLogicalExpression)obj).left)
            && Objects.equals(right, ((BinaryLogicalExpression)obj).right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

}
