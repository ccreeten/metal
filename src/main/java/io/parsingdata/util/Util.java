package io.parsingdata.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.data.ParseValueList;

public final class Util {

    private Util() {
    }

    /** Get a value with given name at a given depth. (0-based index) */
    public static ParseValue valueAtDepth(final ParseGraph values, final String name, final int depth) {
        ParseValueList all = values.getAll(name);
        for (int i = 0; i < depth && all.head != null; i++) {
            all = all.tail;
        }
        return all.head;
    }

    /** Print the values, in order of offset, using a Stringifier. */
    public static void print(final ParseResult result, final Stringifier stringifier) {
        final List<ParseValue> values = collectValues(result);

        values.sort(new CompareByOffset());

        for (final ParseValue value : values) {
            System.out.println(stringifier.toString(value));
        }
    }

    private static List<ParseValue> collectValues(final ParseResult result) {
        final List<ParseValue> values = new ArrayList<>();
        collectValues(result.getEnvironment().order, values);
        return values;
    }

    private static void collectValues(final ParseGraph graph, final List<ParseValue> values) {
        final ParseItem head = graph.head;

        if (head == null) {
            return;
        }

        if (head.isValue()) {
            values.add(head.asValue());
        }
        else if (head.isGraph()) {
            collectValues(head.asGraph(), values);
        }

        collectValues(graph.tail, values);
    }

    private static final class CompareByOffset implements Comparator<ParseValue> {
        @Override
        public int compare(final ParseValue x, final ParseValue y) {
            final int cmp = Long.compare(x.getOffset(), y.getOffset());
            if (cmp == 0) {
                return Integer.compare(x.getValue().length, y.getValue().length);
            }
            return cmp;
        }
    }
}
