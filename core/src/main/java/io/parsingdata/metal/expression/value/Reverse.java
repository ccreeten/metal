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

package io.parsingdata.metal.expression.value;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.encoding.Encoding;

/**
 * A {@link ValueExpression} that reverses the results of its operand.
 * <p>
 * Reverse has a single operand <code>values</code> (a
 * {@link ValueExpression}). When evaluated, it evaluates <code>values</code>
 * and then reverses and returns the result.
 */
public class Reverse implements ValueExpression {

    public final ValueExpression values;

    public Reverse(final ValueExpression values) {
        this.values = values;
    }

    @Override
    public ImmutableList<OptionalValue> eval(final Environment environment, final Encoding encoding) {
        return values.eval(environment, encoding).reverse();
    }

}
