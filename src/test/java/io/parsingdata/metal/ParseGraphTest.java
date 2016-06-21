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

package io.parsingdata.metal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.repn;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.sub;
import static io.parsingdata.metal.TokenDefinitions.any;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseGraphList;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.ParseRef;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.token.Token;

public class ParseGraphTest {

    private final Token t = any("t");

    private final ParseGraph pg;
    private final ParseGraph pgc;
    private final ParseGraph pgl;
    private final ParseValue a;
    private final ParseValue b;
    private final ParseValue c;
    private final ParseValue d;
    private final ParseValue e;
    private final ParseValue f;
    private final ParseValue g;
    private final ParseValue h;

    public ParseGraphTest() {
        a = makeVal('a', 0L);
        b = makeVal('b', 2L);
        c = makeVal('c', 4L);
        d = makeVal('d', 6L);
        e = makeVal('e', 8L);
        f = makeVal('f', 10L);
        g = makeVal('g', 12L);
        h = makeVal('h', 14L);
        pg = makeSimpleGraph();
        pgc = makeCycleGraph();
        pgl = makeLongGraph();
    }

    private static ParseValue makeVal(final char n, final long o) {
        return new ParseValue("", Character.toString(n), def(Character.toString(n), o), o, new byte[]{(byte) n}, enc());
    }

    private ParseGraph makeSimpleGraph() {
        return ParseGraph.EMPTY
            .add(a) // [a]
            .add(b) // [b]
            .addBranch(t) //  +---+
            .add(c) //  |  [c]
            .addBranch(t) //  |   +---+
            .add(d) //  |   |  [d]
            .add(e) //  |   |  [e]
            .closeBranch() //  |   +---+
            .add(f) //  |  [f]
            .closeBranch() //  +---+
            .add(g) // [g]
            .add(h); // [h]
    }

    @Test
    public void simple() {
        Assert.assertTrue(pg.head.isValue());
        Assert.assertEquals(h, pg.head);
        Assert.assertTrue(pg.tail.head.isValue());
        Assert.assertEquals(g, pg.tail.head);
        Assert.assertTrue(pg.tail.tail.head.isGraph());
        Assert.assertTrue(pg.tail.tail.head.asGraph().head.isValue());
        Assert.assertEquals(f, pg.tail.tail.head.asGraph().head);
        Assert.assertTrue(pg.tail.tail.head.asGraph().tail.head.isGraph());
        Assert.assertTrue(pg.tail.tail.head.asGraph().tail.head.asGraph().head.isValue());
        Assert.assertEquals(e, pg.tail.tail.head.asGraph().tail.head.asGraph().head);
        Assert.assertTrue(pg.tail.tail.head.asGraph().tail.head.asGraph().tail.head.isValue());
        Assert.assertEquals(d, pg.tail.tail.head.asGraph().tail.head.asGraph().tail.head);
        Assert.assertTrue(pg.tail.tail.head.asGraph().tail.tail.head.isValue());
        Assert.assertEquals(c, pg.tail.tail.head.asGraph().tail.tail.head);
        Assert.assertTrue(pg.tail.tail.tail.head.isValue());
        Assert.assertEquals(b, pg.tail.tail.tail.head);
        Assert.assertTrue(pg.tail.tail.tail.tail.head.isValue());
        Assert.assertEquals(a, pg.tail.tail.tail.tail.head);
    }

    private ParseGraph makeCycleGraph() {
        return ParseGraph.EMPTY
            .add(a)
            .addBranch(t)
            .add(b)
            .add(new ParseRef(a.getOffset(), sub(any("a"), con(a.getOffset()))))
            .closeBranch();
    }

    @Test
    public void cycle() {
        Assert.assertEquals(2, pgc.size);
        Assert.assertTrue(pgc.head.isGraph());
        Assert.assertTrue(pgc.head.asGraph().head.isRef());
        Assert.assertEquals(pgc, pgc.head.asGraph().head.asRef().resolve(pgc));
        Assert.assertTrue(pgc.head.asGraph().tail.head.isValue());
        Assert.assertEquals(b, pgc.head.asGraph().tail.head);
        Assert.assertTrue(pgc.tail.head.isValue());
        Assert.assertEquals(a, pgc.tail.head);
    }

    private ParseGraph makeLongGraph() {
        return ParseGraph.EMPTY
            .add(a)
            .addBranch(t)
            .addBranch(t)
            .add(b)
            .closeBranch()
            .addBranch(t)
            .closeBranch()
            .add(c)
            .addBranch(t)
            .add(d)
            .closeBranch()
            .closeBranch()
            .add(e)
            .addBranch(t)
            .add(f)
            .closeBranch();
    }

    @Test
    public void listGraphs() {
        final ParseGraphList list = pgl.getGraphs();
        Assert.assertEquals(6, list.size);
    }

    @Test
    public void firstValue() {
        Assert.assertTrue(pgl.containsValue());
        Assert.assertEquals(a, pgl.getLowestOffsetValue());
        Assert.assertEquals(f, pgl.head.asGraph().getLowestOffsetValue());
        Assert.assertEquals(a, pg.getLowestOffsetValue());
        Assert.assertEquals(c, pg.tail.tail.head.asGraph().getLowestOffsetValue());
        Assert.assertEquals(d, pg.tail.tail.head.asGraph().tail.head.asGraph().getLowestOffsetValue());
    }

    @Test
    public void testSimpleGetGraphAfter() {
        final ParseGraph graph = makeSimpleGraph();
        final ParseItem itemB = graph.tail.tail.tail.head;
        Assert.assertTrue(itemB.isValue());
        Assert.assertEquals(b, itemB);
        final ParseGraph subGraph = graph.getGraphAfter(itemB);
        Assert.assertTrue(subGraph.head.isValue());
        Assert.assertEquals(h, subGraph.head);
        Assert.assertTrue(subGraph.tail.head.isValue());
        Assert.assertEquals(g, subGraph.tail.head);
        Assert.assertTrue(subGraph.tail.tail.head.isGraph());
        Assert.assertTrue(subGraph.tail.tail.head.asGraph().head.isValue());
        Assert.assertEquals(f, subGraph.tail.tail.head.asGraph().head);
    }

    // TODO test reverse graph != original graph

    @Test
    public void testDefinitionsAfterReverseSimpleCustom() throws IOException {
        final ParseGraph graph = seq(def("1byte", 1), repn(def("abyte", 1), con(1))).parse(stream(1, 2, 3), enc()).getEnvironment().order;
        final ParseGraph reverse = graph.reverse();
        final ParseGraph reversedReverse = reverse.reverse();

        assertEqualDefinitions(graph, reversedReverse);
    }

    @Test
    public void testValuesAfterReverseSimpleCustom() throws IOException {
        final ParseGraph graph = seq(def("1byte", 1), repn(def("abyte", 1), con(1))).parse(stream(1, 2, 3), enc()).getEnvironment().order;
        final ParseGraph reverse = graph.reverse();
        final ParseGraph reversedReverse = reverse.reverse();

        assertEqualValues(graph, reversedReverse);
    }

    @Test
    public void testDefinitionsAfterReverseSimple() throws IOException {
        final ParseGraph graph = makeSimpleGraph();
        final ParseGraph reverse = graph.reverse();
        final ParseGraph reversedReverse = reverse.reverse();

        assertEqualDefinitions(graph, reversedReverse);
    }

    @Test
    public void testValuesAfterReverseSimple() throws IOException {
        final ParseGraph graph = makeSimpleGraph();
        final ParseGraph reverse = graph.reverse();
        final ParseGraph reversedReverse = reverse.reverse();

        assertEqualValues(graph, reversedReverse);
    }

    @Test
    public void testDefinitionsAfterReverseLongGraph() throws IOException {
        final ParseGraph graph = makeLongGraph();
        final ParseGraph reverse = graph.reverse();
        final ParseGraph reversedReverse = reverse.reverse();

        assertEqualDefinitions(graph, reversedReverse);
    }

    @Test
    public void testValuesAfterReverseLongGraph() throws IOException {
        final ParseGraph graph = makeLongGraph();
        final ParseGraph reverse = graph.reverse();
        final ParseGraph reversedReverse = reverse.reverse();

        assertEqualValues(graph, reversedReverse);
    }

    private void assertEqualDefinitions(final ParseGraph graph1, final ParseGraph graph2) {
        if (graph1.isEmpty()) {
            assertTrue(graph2.isEmpty());
            return;
        }

        final ParseItem head1 = graph1.head;
        final ParseItem head2 = graph2.head;

        assertThat(graph1.getDefinition(), is(equalTo(graph2.getDefinition())));
        if (head1.isValue() || head1.isRef()) {
            assertThat(head1.getDefinition(), is(equalTo(head2.getDefinition())));
        }
        else {
            assertEqualDefinitions(graph1.head.asGraph(), graph2.head.asGraph());
        }
        assertEqualDefinitions(graph1.tail, graph2.tail);
    }

    private void assertEqualValues(final ParseGraph graph1, final ParseGraph graph2) {
        if (graph1.isEmpty()) {
            assertTrue(graph2.isEmpty());
            return;
        }

        final ParseItem head1 = graph1.head;
        final ParseItem head2 = graph2.head;

        if (head1.isValue()) {
            assertThat(head1.asValue().asNumeric(), is(equalTo(head2.asValue().asNumeric())));
        }
        else if (head1.isRef()) {
            assertThat(head1.asRef().location, is(equalTo(head2.asRef().location)));
        }
        else {
            assertEqualValues(graph1.head.asGraph(), graph2.head.asGraph());
        }
        assertEqualValues(graph1.tail, graph2.tail);
    }
}
