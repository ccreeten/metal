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

import java.io.IOException;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.data.selection.ByName;
import io.parsingdata.metal.encoding.Encoding;

/**
 * A {@link Token} that references a previously parsed token.
 * <p>
 * TokenRef consists of a <code>referenceName</code> (a String). In order to
 * allow the construction of recursive tokens, this token can be used to
 * reference an enclosing token and use it at the current location. An example
 * for use of this token is to recursively define a linked list.
 * <p>
 * The referenced token is located in the current parse state by traversing it
 * backwards until it is located. Parsing will fail if it is not found.
 *
 * @see io.parsingdata.metal.expression.value.reference.NameRef
 * @see io.parsingdata.metal.expression.value.reference.TokenRef
 */
public class TokenRef extends Token {

    public final String referenceName;

    public TokenRef(String name, String referenceName, Encoding encoding) {
        super(name, encoding);
        this.referenceName = checkNotNull(referenceName, "referenceName");
        if (referenceName.isEmpty()) { throw new IllegalArgumentException("Argument referenceName may not be empty."); }
    }

    @Override
    protected ParseResult parseImpl(String scope, Environment environment, Encoding encoding) throws IOException {
        return ByName.getDefinition(environment.order, referenceName).parse(scope, environment, encoding);
    }

    @Override
    public Token getCanonical(Environment environment) {
        return ByName.getDefinition(environment.order, referenceName);
    }
}
