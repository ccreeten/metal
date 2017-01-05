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

import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.data.selection.ByToken.getAllValues;
import static io.parsingdata.metal.expression.value.OptionalValue.wrap;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.OptionalValue;
import io.parsingdata.metal.expression.value.ValueExpression;
import io.parsingdata.metal.token.Token;

/**
 * A {@link ValueExpression} that represents all
 * {@link io.parsingdata.metal.expression.value.Value}s in the parse state
 * that match the provided <code>definition</code>.
 */
public class TokenRef implements ValueExpression {

    public final Token definition;

    public TokenRef(final Token definition) {
        this.definition = checkNotNull(definition, "definition");
    }

    @Override
    public ImmutableList<OptionalValue> eval(final Environment environment, final Encoding encoding) {
        return wrap(getAllValues(environment.order, definition));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + definition + ")";
    }

}
