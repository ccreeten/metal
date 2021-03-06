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

import static io.parsingdata.metal.Util.checkNotNull;

import java.util.Objects;

import io.parsingdata.metal.Util;
import io.parsingdata.metal.expression.Expression;

/**
 * Base class for {@link LogicalExpression} implementations with a single
 * operand.
 * <p>
 * A UnaryLogicalExpression has one <code>operand</code> (an
 * {@link Expression}). The <code>operand</code> is evaluated, the concrete
 * implementation's operator is applied to the result and returned.
 */
public abstract class UnaryLogicalExpression implements LogicalExpression {

    public final Expression operand;

    public UnaryLogicalExpression(final Expression operand) {
        this.operand = checkNotNull(operand, "operand");
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + operand + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
            && Objects.equals(operand, ((UnaryLogicalExpression)obj).operand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operand);
    }

}
