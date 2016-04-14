/*
 * Copyright (c) 2016 Netherlands Forensic Institute
 * All rights reserved.
 */
package io.parsingdata.util;

import static io.parsingdata.metal.Shorthand.cho;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eqNum;
import static io.parsingdata.metal.Shorthand.expTrue;
import static io.parsingdata.metal.Shorthand.nod;
import static io.parsingdata.metal.Shorthand.opt;
import static io.parsingdata.metal.Shorthand.pre;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.Shorthand.repn;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.str;
import static io.parsingdata.metal.Shorthand.sub;
import static io.parsingdata.metal.Shorthand.whl;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;

import java.io.IOException;

import org.junit.Test;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.token.Token;

/**
 *
 * @author Netherlands Forensic Institute.
 */
public class VisualizerTest {

    private static final Token STAGE_3 = str("ength",
                                             seq(
                                                 repn(def("offset", 1), con(1)),
                                                 sub(
                                                     whl(def("while", 1), eqNum(con(5))),
                                                     ref("offset")),
                                                 rep(def("till the end", 1))));

    private static final Token STAGE_2 = seq(
                                             nod(con(1)),
                                             opt(def("righton", 1)),
                                             pre(def("satisfied", 1), expTrue()),
                                             STAGE_3);

    private static final Token GOD = cho(def("nope", 1, eqNum(con(666))),
                                         STAGE_2);

    @Test
    public void test() {
        final ParseGraph graph = parseResultGraph(stream(0, 1, 2, 4, 5, 6), GOD);
        Visualizer.printGraphViz(graph.reverse());
    }

    private ParseGraph parseResultGraph(final Environment env, final Token def) {
        try {
            return def.parse(env, enc()).getEnvironment().order;
        }
        catch (final IOException e) {
            throw new AssertionError("Parsing failed", e);
        }
    }
}
