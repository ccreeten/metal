package io.parsingdata.metal.expression.value.reference;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eqNum;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.sub;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;

import java.io.IOException;

import org.junit.Test;

import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.expression.value.ValueExpression;
import io.parsingdata.metal.token.Token;

public class NearestTest {

    @Test
    public void testSimple() throws IOException {
        final Token simpleSeq = seq(
                                    def("valSize", 1),
                                    def("valSize", 1),
                                    def("testVal", nRef("valSize")));
        final ParseGraph graph = simpleSeq.parse(stream(2, 1, 0, 0), enc()).getEnvironment().order;
        assertThat(graph.get("testVal").getValue().length, is(equalTo(1)));
    }

    @Test
    public void testComplex() throws IOException {
        final Token fail = seq(
                               def("ref", 1),
                               def("value1", 1),
                               sub(
                                   seq(
                                       def("value1", 1),
                                       def("value2", 1)),
                                   ref("ref")),
                               def("value2", 1, eqNum(nRef("value1"))));
        final ParseResult result = fail.parse(stream(3, 42, 42, 43, 43), enc());
        assertTrue(result.succeeded());
    }

    private static ValueExpression nRef(final String name) {
        return new Nearest(name);
    }
}
