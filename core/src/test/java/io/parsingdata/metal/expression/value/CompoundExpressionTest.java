package io.parsingdata.metal.expression.value;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.count;
import static io.parsingdata.metal.Shorthand.eqNum;
import static io.parsingdata.metal.Shorthand.exp;
import static io.parsingdata.metal.Shorthand.gtNum;
import static io.parsingdata.metal.Shorthand.or;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.repn;
import static io.parsingdata.metal.Shorthand.rev;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.expression.Expression;

public class CompoundExpressionTest {

    @Test
    public void gtEqNum() throws IOException {
        final ParseGraph graph = seq(repn(any("value"), con(2)), repn(any("other"), con(2))).parse(stream(0, 1, 0, 1), enc()).get().order;

        assertFalse(gtEqNum(exp(con(0), con(0)), ref("value")).evalAndReduce(graph, enc()));
        assertFalse(gtEqNum(exp(con(0), con(1)), ref("value")).evalAndReduce(graph, enc()));
        assertFalse(gtEqNum(exp(con(0), con(3)), ref("value")).evalAndReduce(graph, enc()));

        assertTrue(gtEqNum(ref("value"), ref("value")).evalAndReduce(graph, enc()));
        assertFalse(gtEqNum(exp(con(0), count(ref("value"))), ref("value")).evalAndReduce(graph, enc()));
        assertTrue(gtEqNum(exp(con(1), count(ref("value"))), ref("value")).evalAndReduce(graph, enc()));
        assertTrue(gtEqNum(exp(con(2), count(ref("value"))), ref("value")).evalAndReduce(graph, enc()));

        assertTrue(gtEqNum(ref("value"), ref("other")).evalAndReduce(graph, enc()));
        assertFalse(gtEqNum(ref("value"), rev(ref("other"))).evalAndReduce(graph, enc()));
    }

    private static Expression gtEqNum(final ValueExpression value, final ValueExpression predicate) {
        return or(gtNum(value, predicate), eqNum(value, predicate));
    }
}
