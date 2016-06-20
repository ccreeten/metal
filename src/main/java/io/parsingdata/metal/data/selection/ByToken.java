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

package io.parsingdata.metal.data.selection;

import java.util.Stack;

import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.ParseItemList;
import io.parsingdata.metal.token.Token;

public class ByToken {

    private ByToken() {
    }

    public static ParseItem get(final ParseGraph graph, final Token definition) {
        if (definition == null) {
            throw new IllegalArgumentException("Argument definition may not be null.");
        }

        final Stack<ParseGraph> stack = new Stack<>();
        stack.push(graph);

        while (!stack.isEmpty()) {
            final ParseGraph g = stack.pop();
            if (g.isEmpty()) {
                continue;
            }
            if (g.getDefinition() == definition) {
                return g;
            }
            stack.push(g.tail);
            final ParseItem head = g.head;
            if (head.isValue() && head.getDefinition() == definition) {
                return head.asValue();
            }
            if (head.isGraph()) {
                stack.push(head.asGraph());
            }
        }

        return null;
    }

    public static ParseItemList getAll(final ParseGraph graph, final Token definition) {
        if (definition == null) {
            throw new IllegalArgumentException("Argument definition may not be null.");
        }

        final Stack<ParseItem> stack = new Stack<>();
        stack.push(graph);

        ParseItemList list = ParseItemList.EMPTY;
        while (!stack.isEmpty()) {
            final ParseItem next = stack.pop();
            if (next.isRef()) {
                continue;
            }
            else if (next.isValue()) {
                if (next.asValue().getDefinition() == definition) {
                    list = list.add(next.asValue());
                }
                continue;
            }
            else if (next.asGraph().isEmpty()) {
                continue;
            }

            if (next.getDefinition() == definition) {
                list = list.add(next);
            }

            stack.push(next.asGraph().head);
            stack.push(next.asGraph().tail);
        }

        return list;
    }

}
