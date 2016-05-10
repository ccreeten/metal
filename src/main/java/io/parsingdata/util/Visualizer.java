/*
 * Copyright (c) 2016 Netherlands Forensic Institute
 * All rights reserved.
 */
package io.parsingdata.util;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.token.Cho;
import io.parsingdata.metal.token.Def;
import io.parsingdata.metal.token.Nod;
import io.parsingdata.metal.token.Opt;
import io.parsingdata.metal.token.Pre;
import io.parsingdata.metal.token.Rep;
import io.parsingdata.metal.token.RepN;
import io.parsingdata.metal.token.Seq;
import io.parsingdata.metal.token.Str;
import io.parsingdata.metal.token.Sub;
import io.parsingdata.metal.token.While;

public final class Visualizer {

    private static final String ROOT = "Root";
    private static final String EMPTY = "Empty";
    private static final String DEF = "Def";

    private static final Class<?>[] TOKENS = {Cho.class, Def.class, Nod.class, Opt.class, Pre.class, Rep.class, RepN.class, Seq.class, Str.class, Sub.class, While.class};
    private static final Map<String, String> COLORS = new HashMap<>();

    private final ValueStringifier stringifier;

    static {
        double hue = 0.0;
        final double step = 0.85 / TOKENS.length;
        for (final Class<?> token : TOKENS) {
            COLORS.put(token.getSimpleName(), String.format("%.5f 0.3 0.9", hue));
            hue += step;
        }
        COLORS.put(ROOT, "0.9 0.3 0.9");
        COLORS.put(EMPTY, "0.95 0.3 0.9");
    }

    public Visualizer() {
        this(new ValueStringifier() {

            @Override
            public String toString(final ParseValue value) {
                boolean isString = true;
                for (final byte character : value.getValue()) {
                    final int charValue = character & 0xff;
                    if (charValue < ' ' || charValue > '~') {
                        isString = false;
                        break;
                    }
                }

                final Object val = isString ? new String(value.getValue(), StandardCharsets.UTF_8) : Long.toHexString(value.asNumeric().longValue()).toUpperCase();
                return String.format("\"[0x%s] %s: %s\"", Long.toHexString(value.offset).toUpperCase(), value.getFullName(), val);
            }
        });
    }

    public Visualizer(final ValueStringifier stringifier) {
        this.stringifier = stringifier;
    }

    public void printGraphViz(final ParseGraph graph) {
        System.out.printf("digraph {%nnode [style = filled]%n%s[label = %s, color = \"%s\"]%n", ROOT, ROOT, getHSVString(ROOT));
        toViz(graph, "", ROOT);
        System.out.printf("}%n");
    }

    private void toViz(final ParseGraph graph, final String path, final String parentNode) {
        final ParseItem head = graph.head;
        if (head == null) {
            return;
        }

        final String headPath = path + 0;
        final String tailPath = path + 1;

        if (head.isValue()) {
            final String valueName = DEF + headPath;
            printNodeDefinition(valueName, stringifier.toString(head.asValue()), getHSVString(DEF));
            printEdgeDefinition(parentNode, valueName, "head");
        }
        else if (head.isGraph()) {
            toViz(head.asGraph(), parentNode, headPath, "head");
        }

        toViz(graph.tail, parentNode, tailPath, "tail");
    }

    private void toViz(final ParseGraph graph, final String parentNode, final String graphPath, final String edgeLabel) {
        final String definition = graphToDef(graph);
        final String identifier = definition + graphPath;
        printNodeDefinition(identifier, definition);
        printEdgeDefinition(parentNode, identifier, edgeLabel);

        toViz(graph, graphPath, identifier);
    }

    private String graphToDef(final ParseGraph graph) {
        return graph == ParseGraph.EMPTY ? EMPTY : graph.getDefinition().getClass().getSimpleName();
    }

    private void printEdgeDefinition(final String parent, final String child, final String edgeLabel) {
        System.out.printf("%s -> %s [label = %s]", parent, child, edgeLabel);
    }

    private void printNodeDefinition(final String identifier, final String definition) {
        printNodeDefinition(identifier, definition, getHSVString(definition));
    }

    private void printNodeDefinition(final String identifier, final String nodeLabel, final String color) {
        System.out.printf("%s[label = %s, color = \"%s\"]%n", identifier, nodeLabel, color);
    }

    private String getHSVString(final String def) {
        return COLORS.getOrDefault(def, "0.0 0.0 0.0");
    }

    public static interface ValueStringifier {

        public String toString(ParseValue value);
    }
}
