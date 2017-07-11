package io.parsingdata.metal;

import static io.parsingdata.metal.Util.allTrue;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.count;
import static io.parsingdata.metal.Shorthand.eqNum;
import static io.parsingdata.metal.Shorthand.exp;
import static io.parsingdata.metal.Shorthand.gtNum;
import static io.parsingdata.metal.Shorthand.not;
import static io.parsingdata.metal.Shorthand.or;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.repn;
import static io.parsingdata.metal.Shorthand.rev;
import static io.parsingdata.metal.Shorthand.seq;

import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.expression.Expression;
import io.parsingdata.metal.expression.comparison.ComparisonExpression;
import io.parsingdata.metal.expression.value.ValueExpression;

public class CompoundExpressionTest {

    @Test
    public void evalImp() throws IOException {
        final ParseGraph graph = repn(any("value"), con(2)).parse(stream(0, 1), enc()).get().order;

        final ComparisonExpression left = gtNum(ref("value"), exp(con(0), con(2)));
        final ComparisonExpression right = gtNum(ref("value"), exp(con(1), con(2)));
        final Expression implication = imp(left, right);

        assertThat(left.eval(graph, enc()), is(equalTo(new ImmutableList<>().add(false).add(true))));
        assertThat(right.eval(graph, enc()), is(equalTo(new ImmutableList<>().add(false).add(false))));
        assertThat(implication.eval(graph, enc()), is(equalTo(new ImmutableList<>().add(true).add(false))));
    }

    @Test
    public void evalGtEqNum() throws IOException {
        final ParseGraph graph = seq(repn(any("value"), con(2)), repn(any("other"), con(2))).parse(stream(0, 1, 0, 1), enc()).get().order;

        assertFalse(allTrue(gtEqNum(exp(con(0), con(0)), ref("value")).eval(graph, enc())));
        assertFalse(allTrue(gtEqNum(exp(con(0), con(1)), ref("value")).eval(graph, enc())));
        assertFalse(allTrue(gtEqNum(exp(con(0), con(3)), ref("value")).eval(graph, enc())));

        assertTrue(allTrue(gtEqNum(ref("value"), ref("value")).eval(graph, enc())));
        assertFalse(allTrue(gtEqNum(exp(con(0), count(ref("value"))), ref("value")).eval(graph, enc())));
        assertTrue(allTrue(gtEqNum(exp(con(1), count(ref("value"))), ref("value")).eval(graph, enc())));
        assertTrue(allTrue(gtEqNum(exp(con(2), count(ref("value"))), ref("value")).eval(graph, enc())));

        assertTrue(allTrue(gtEqNum(ref("value"), ref("other")).eval(graph, enc())));
        assertFalse(allTrue(gtEqNum(ref("value"), rev(ref("other"))).eval(graph, enc())));
    }

    private static Expression imp(final Expression l, final Expression r) {
        return or(not(l), r);
    }

    private static Expression gtEqNum(final ValueExpression value, final ValueExpression predicate) {
        return or(gtNum(value, predicate), eqNum(value, predicate));
    }

}
