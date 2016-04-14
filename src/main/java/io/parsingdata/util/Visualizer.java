/*
 * Copyright (c) 2016 Netherlands Forensic Institute
 * All rights reserved.
 */
package io.parsingdata.util;

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
    private static final String NULL = "Null";
    private static final String DEF = "Def";

    private static final Class<?>[] TOKENS = {Cho.class, Def.class, Nod.class, Opt.class, Pre.class, Rep.class, RepN.class, Seq.class, Str.class, Sub.class, While.class};
    private static final Map<String, String> COLORS = new HashMap<>();

    static {
        double hue = 0.0;
        final double step = 0.85 / TOKENS.length;
        for (final Class<?> token : TOKENS) {
            COLORS.put(token.getSimpleName(), String.format("%.5f 0.3 0.9", hue));
            hue += step;
        }
        COLORS.put(ROOT, "0.9 0.3 0.9");
        COLORS.put(NULL, "0.95 0.3 0.9");
    }

    public static void printGraphViz(final ParseGraph graph) {
        printGraphViz(graph, new ValueNodeStringifier() {

            @Override
            public String toString(final ParseValue value) {
                return String.format("\"[0x%s]: %s\"", Long.toHexString(value.offset).toUpperCase(), value.asNumeric());
            }
        });
    }

    public static void printGraphViz(final ParseGraph graph, final ValueNodeStringifier stringifier) {
        System.out.printf("digraph {%nnode [style = filled]%n%s[label = %s, color = \"%s\"]%n", ROOT, ROOT, getHSVString(ROOT));
        toViz(graph, "", ROOT, stringifier);
        System.out.printf("}%n");
    }

    private static void toViz(final ParseGraph graph, final String path, final String parentNode, final ValueNodeStringifier stringifier) {
        final ParseItem head = graph.head;
        if (head == null) {
            return;
        }

        final String headPath = path + 0;
        final String tailPath = path + 1;

        if (head.isValue()) {
            final String valueName = DEF + headPath;
            printNodeDefinition(stringifier.toString(head.asValue()), valueName, getHSVString(DEF));
            printEdgeDefinition(parentNode, valueName, "head");
        }
        else if (head.isGraph()) {
            final String def = head.getDefinition().getClass().getSimpleName();
            final String headName = def + headPath;
            printNodeDefinition(def, headName);
            printEdgeDefinition(parentNode, headName, "head");

            toViz(head.asGraph(), headPath, headName, stringifier);
        }

        final String def = graph.tail.size == 0 ? NULL : graph.tail.getDefinition().getClass().getSimpleName();
        final String tailName = def + tailPath;
        printNodeDefinition(def, tailName);
        printEdgeDefinition(parentNode, tailName, "tail");

        toViz(graph.tail, tailPath, tailName, stringifier);
    }

    private static void printEdgeDefinition(final String parent, final String child, final String edgeLabel) {
        System.out.printf("%s -> %s [label = %s]", parent, child, edgeLabel);
    }

    private static void printNodeDefinition(final String definition, final String identifier) {
        printNodeDefinition(definition, identifier, getHSVString(definition));
    }

    private static void printNodeDefinition(final String definition, final String identifier, final String color) {
        System.out.printf("%s[label = %s, color = \"%s\"]%n", identifier, definition, color);
    }

    private static String getHSVString(final String def) {
        // TODO when in VisualizerTest, when trying reverse def is an empty String at some point
        return COLORS.getOrDefault(def, "0.0 0.0 0.0");
    }

    public static interface ValueNodeStringifier {

        public String toString(ParseValue value);
    }
}
