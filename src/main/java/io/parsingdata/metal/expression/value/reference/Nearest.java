package io.parsingdata.metal.expression.value.reference;

import static io.parsingdata.metal.Util.checkNotNull;

import java.util.ArrayDeque;
import java.util.Deque;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.OptionalValue;
import io.parsingdata.metal.expression.value.ValueExpression;

public class Nearest implements ValueExpression {

    private final String _name;

    public Nearest(final String name) {
        _name = checkNotNull(name, "name");
    }

    @Override
    public OptionalValue eval(final Environment env, final Encoding enc) {
        final ParseGraph graph = env.order;
        return getNearest(graph, _name);
    }

    private OptionalValue getNearest(final ParseGraph graph, final String name) {
        Deque<ParseItem> queue = queueItems(graph);

        while (!queue.isEmpty()) {
            final Deque<ParseItem> newQueue = new ArrayDeque<>();

            while (!queue.isEmpty()) {
                final ParseItem item = queue.poll();
                if (item.isRef()) {
                    continue;
                }
                else if (item.isValue()) {
                    if (item.asValue().matches(name)) {
                        return OptionalValue.of(item.asValue());
                    }
                }
                else {
                    newQueue.addAll(queueItems(item.asGraph()));
                }
            }

            queue = newQueue;
        }
        return OptionalValue.empty();
    }

    private Deque<ParseItem> queueItems(final ParseGraph graph) {
        ParseGraph g = graph;
        final Deque<ParseItem> queue = new ArrayDeque<>();

        while (!g.isEmpty()) {
            queue.add(g.head);
            g = g.tail;
        }
        return queue;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + _name + ")";
    }

}