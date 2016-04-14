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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.opt;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.Shorthand.repn;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.sub;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.ParseItemList;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.data.ParseValueList;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.token.Token;

public class ByTokenTest {

    private static final Token DEF1 = def("value1", con(1));
    private static final Token DEF2 = def("value2", con(1));
    private static final Token TWO_BYTES = def("two", 2);
    private static final Token UNUSED_DEF = def("value1", con(1));

    private static final Token SIMPLE_SEQ = seq(DEF1, DEF2);
    private static final Token SEQ_REP = seq(DEF1, rep(DEF2));
    private static final Token SEQ_SUB = seq(DEF1, sub(TWO_BYTES, ref("value1")), DEF2, sub(TWO_BYTES, ref("value2")));
    private static final Token REPN_DEF2 = repn(DEF2, con(2));

    private static final Token MUT_REC_1 = seq(DEF1, new Token(enc()) {

        @Override
        protected ParseResult parseImpl(final String scope, final Environment env, final Encoding enc) throws IOException {
            return MUT_REC_2.parse(scope, env, enc);
        }

    });

    private static final Token MUT_REC_2 = seq(REPN_DEF2, opt(MUT_REC_1));

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void nullCheck() throws IOException {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Argument definition may not be null");

        ByToken.get(parseResultGraph(stream(0, 1, 2), SIMPLE_SEQ), null);
    }

    @Test
    public void findRootToken() throws IOException {
        final ParseGraph graph = parseResultGraph(stream(0, 1, 2), SIMPLE_SEQ);
        final ParseItem parseItem = ByToken.get(graph, SIMPLE_SEQ);

        assertThat(parseItem.getDefinition(), is(equalTo(SIMPLE_SEQ)));
    }

    @Test
    public void findNestedToken() throws IOException {
        final ParseGraph graph = parseResultGraph(stream(0, 1, 2), SIMPLE_SEQ);
        final ParseItem parseItem = ByToken.get(graph, DEF1);

        assertThat(parseItem.getDefinition(), is(equalTo(DEF1)));
    }

    @Test
    public void findUnusedToken() throws IOException {
        final ParseGraph graph = parseResultGraph(stream(0, 1, 2), SIMPLE_SEQ);
        final ParseItem parseItem = ByToken.get(graph, UNUSED_DEF);

        assertThat(parseItem, is(nullValue()));
    }

    @Test
    public void getAllNullCheck() throws IOException {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Argument definition may not be null");

        ByToken.getAll(parseResultGraph(stream(0, 1, 2), SIMPLE_SEQ), null);
    }

    @Test
    public void getAllUnusedToken() throws IOException {
        final ParseGraph graph = parseResultGraph(stream(0), SEQ_REP);
        final ParseItemList list = ByToken.getAll(graph, UNUSED_DEF);

        assertThat(list.size, is(equalTo(0L)));
    }

    @Test
    public void getAllNonePresent() throws IOException {
        final ParseGraph graph = parseResultGraph(stream(0), SEQ_REP);
        final ParseItemList list = ByToken.getAll(graph, DEF2);

        assertThat(list.size, is(equalTo(0L)));
    }

    @Test
    public void getAllSingleDef() throws IOException {
        final ParseGraph graph = parseResultGraph(stream(0, 1, 2, 3, 4, 5), SEQ_REP);
        final ParseItemList list = ByToken.getAll(graph, DEF1);

        assertThat(list.size, is(equalTo(1L)));
        assertThat(list.head.getDefinition(), is(equalTo(DEF1)));
    }

    @Test
    public void getAllRepDef() throws IOException {
        final ParseGraph graph = parseResultGraph(stream(0, 1, 2, 3, 4, 5), SEQ_REP);
        final ParseItemList list = ByToken.getAll(graph, DEF2);

        assertThat(list.size, is(equalTo(5L)));
        assertThat(list.head.getDefinition(), is(equalTo(DEF2)));
    }

    @Test
    public void getAllRepSeq() throws IOException {
        final ParseGraph graph = parseResultGraph(stream(0, 1, 2, 3, 4, 5), rep(SIMPLE_SEQ));
        final ParseItemList list1 = ByToken.getAll(graph, DEF1);
        final ParseItemList list2 = ByToken.getAll(graph, DEF2);

        assertThat(list1.size, is(equalTo(3L)));
        assertThat(list2.size, is(equalTo(3L)));

        assertThat(list1.head.getDefinition(), is(equalTo(DEF1)));
        assertThat(list2.head.getDefinition(), is(equalTo(DEF2)));

        assertThat(list1.tail.head.asValue().asNumeric().intValue(), is(equalTo(2)));
        assertThat(list2.tail.head.asValue().asNumeric().intValue(), is(equalTo(3)));
    }

    @Test
    public void getAllSub() throws IOException {
        final ParseGraph graph = parseResultGraph(stream(4, 2, 2, 3, 4, 5), SEQ_SUB);
        final ParseItemList list = ByToken.getAll(graph, TWO_BYTES);

        assertThat(list.size, is(equalTo(2L)));
        assertThat(list.head.getDefinition(), is(equalTo(TWO_BYTES)));
        assertThat(list.head.asValue().getValue(), is(equalTo(new byte[]{2, 3})));
    }

    @Test
    public void getAllMutualRecursive() throws IOException {
        final ParseGraph graph = parseResultGraph(stream(0, 1, 2, 3, 4, 5), MUT_REC_1);

        final ParseItemList repList = ByToken.getAll(graph, REPN_DEF2);
        assertThat(repList.size, is(equalTo(4L)));

        final ParseItemList recList = ByToken.getAll(graph, MUT_REC_1);
        assertThat(recList.size, is(equalTo(4L)));
    }

    @Test
    public void compareGetAllNameWithToken() throws IOException {
        final ParseGraph graph = parseResultGraph(stream(0, 1, 2, 3, 4, 5), SEQ_REP);

        ParseValueList valueList = ByName.getAll(graph, "value2");
        ParseItemList itemList = ByToken.getAll(graph, DEF2);

        while (valueList.head != null) {
            assertThat(valueList.head, is(equalTo(itemList.head.asValue())));

            valueList = valueList.tail;
            itemList = itemList.tail;
        }
    }

    private ParseGraph parseResultGraph(final Environment env, final Token def) throws IOException {
        final ParseResult parseResult = def.parse(env, enc());
        return parseResult.getEnvironment().order;
    }

}