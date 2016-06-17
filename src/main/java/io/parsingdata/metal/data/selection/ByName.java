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
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.data.ParseValueList;

public class ByName {

    /**
     * @param name Name of the value
     * @return The first value (bottom-up) with the provided name in this graph
     */
    public static ParseValue get(final ParseGraph g, final String name) {
        final Stack<ParseGraph> stack = new Stack<>();
        stack.push(g);

        while (!stack.isEmpty()) {
            final ParseGraph graph = stack.pop();
            if (graph.isEmpty()) {
                continue;
            }
            stack.push(graph.tail);
            final ParseItem head = graph.head;
            if (head.isValue() && head.asValue().matches(name)) {
                return head.asValue();
            }
            if (head.isGraph()) {
                stack.push(head.asGraph());
            }
        }

        return null;
    }

    /**
     * @param name Name of the value
     * @return All values with the provided name in this graph
     */
    public static ParseValueList getAll(final ParseGraph parseGraph, final String name) {
        return getAll(parseGraph, name, ParseValueList.EMPTY);
    }

    private static ParseValueList getAll(final ParseGraph graph, final String name, final ParseValueList result) {
        if (graph.isEmpty()) {
            return result;
        }
        final ParseValueList tailResults = getAll(graph.tail, name, result);
        final ParseItem head = graph.head;
        if (head.isValue() && head.asValue().matches(name)) {
            return tailResults.add(head.asValue());
        }
        if (head.isGraph()) {
            return tailResults.add(getAll(head.asGraph(), name, result));
        }
        return tailResults;
    }

}
