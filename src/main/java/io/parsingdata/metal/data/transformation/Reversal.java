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

package io.parsingdata.metal.data.transformation;

import java.util.Stack;

import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseItem;

public class Reversal {

    public static ParseGraph reverse(final ParseGraph oldGraph) {
        final Stack<ParseGraph> sOld = new Stack<>();
        final Stack<ParseGraph> sNew = new Stack<>();
        sOld.push(oldGraph);
        sNew.push(ParseGraph.EMPTY);

        while (sOld.size() > 1 || !sOld.peek().isEmpty()) {
            final ParseGraph oldG = sOld.pop();
            final ParseGraph newG = sNew.pop();

            if (oldG.isEmpty()) {
                sNew.push(new ParseGraph(newG, sNew.pop(), sOld.pop().getDefinition()));
                continue;
            }

            final ParseItem head = oldG.head;

            if (head.isGraph()) {
                sOld.push(oldG.tail);
                sNew.push(newG);

                sOld.push(oldG);
                sOld.push(head.asGraph());
                sNew.push(ParseGraph.EMPTY);
            }
            else {
                sOld.push(oldG.tail);
                sNew.push(new ParseGraph(head, newG, oldG.getDefinition()));
            }
        }

        return sNew.peek();
    }
}