package io.parsingdata.metal;

import static io.parsingdata.metal.Util.checkNotNull;

import java.math.BigInteger;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.ParseItemList;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.data.ParseValueList;
import io.parsingdata.metal.data.selection.ByName;
import io.parsingdata.metal.data.selection.ByToken;
import io.parsingdata.metal.token.Def;
import io.parsingdata.metal.token.Token;

public final class Selector {

    private final ParseGraph _graph;

    private Selector(final ParseGraph graph) {
        _graph = graph;
    }

    /**
     * Create a new selector for a graph.
     *
     * @param graph the graph to execute the selections on
     * @return a new selector encapsulating the given graph
     * @throws IllegalArgumentException if graph == null
     */
    public static Selector on(final ParseGraph graph) {
        checkNotNull(graph, "graph");
        return new Selector(graph);
    }

    public static Selector on(final Selector selector) {
        checkNotNull(selector, "selector");
        return on(selector._graph);
    }

    /**
     * Select all items from the graph contained in this selector,
     * matching given definition.
     *
     * @param definition the definition of the items to select
     * @return a list of {@link Selector}s, each encapsulating a matching item
     */
    public List<Selector> select(final Token definition) {
        if (definition instanceof Def) {
            throw new IllegalArgumentException("Cannot select on Def, instead get by name.");
        }

        final List<Selector> selectors = new ArrayList<>();
        ParseItemList allItems = getAllItems(definition);
        while (!allItems.isEmpty()) {
            selectors.add(Selector.on(allItems.head.asGraph()));
            allItems = allItems.tail;
        }
        return new SelectorList(definition, selectors);
    }

    /** Returns the original {@link ParseGraph} which this Selector was created on. */
    public ParseGraph getGraph() {
        return _graph;
    }

    /**
     * See {@link MetalUtil#getBytes(ParseGraph, String)}, using the graph contained in this selector.
     * If multiple values are present, the earliest parsed value with given name will be returned,
     * which is the other way around compared to the linked method (and the way Metal does it).
     * */
    public byte[] getBytes(final String name) {
        return Util.getBytes(_graph.reverse(), name);
    }

    /**
     * See {@link MetalUtil#getByte(ParseGraph, String)}, using the graph contained in this selector.
     * If multiple values are present, the earliest parsed value with given name will be returned,
     * which is the other way around compared to the linked method (and the way Metal does it).
     * */
    public byte getByte(final String name) {
        return Util.getByte(_graph.reverse(), name);
    }

    /**
     * See {@link MetalUtil#getInt(ParseGraph, String)}, using the graph contained in this selector.
     * If multiple values are present, the earliest parsed value with given name will be returned,
     * which is the other way around compared to the linked method (and the way Metal does it).
     * */
    public int getInt(final String name) {
        return Util.getInt(_graph.reverse(), name);
    }

    /**
     * See {@link MetalUtil#getLong(ParseGraph, String)}, using the graph contained in this selector.
     * If multiple values are present, the earliest parsed value with given name will be returned,
     * which is the other way around compared to the linked method (and the way Metal does it).
     * */
    public long getLong(final String name) {
        return Util.getLong(_graph.reverse(), name);
    }

    /**
     * See {@link MetalUtil#getBigInt(ParseGraph, String)}, using the graph contained in this selector.
     * If multiple values are present, the earliest parsed value with given name will be returned,
     * which is the other way around compared to the linked method (and the way Metal does it).
     * */
    public BigInteger getBigInt(final String name) {
        return Util.getBigInt(_graph.reverse(), name);
    }

    /**
     * See {@link MetalUtil#getString(ParseGraph, String)}, using the graph contained in this selector.
     * If multiple values are present, the earliest parsed value with given name will be returned,
     * which is the other way around compared to the linked method (and the way Metal does it).
     * */
    public String getString(final String name) {
        return Util.getString(_graph.reverse(), name);
    }

    /**
     * See {@link MetalUtil#getValue(ParseGraph, String)}, using the graph contained in this selector.
     * If multiple values are present, the earliest parsed value with given name will be returned,
     * which is the other way around compared to the linked method (and the way Metal does it).
     * */
    public ParseValue getValue(final String name) {
        return getAllValues(name).head;
    }

    /**
     * See {@link MetalUtil#getAllValues(ParseGraph, String)}, using the graph contained in this selector.
     * The order of the list is in reverse order compared to the list returned by the linked method
     * (and the way Metal does it), i.e. items parsed earlier are at the front of the list.
     * */
    public ParseValueList getAllValues(final String name) {
        return Util.reverse(ByName.getAllValues(_graph, name));
    }

    /**
     * See {@link MetalUtil#getItem(ParseGraph, String)}, using the graph contained in this selector.
     * If multiple values are present, the earliest parsed value with given name will be returned,
     * which is the other way around compared to the linked method (and the way Metal does it).
     * */
    public ParseItem getItem(final Token definition) {
        return getAllItems(definition).head;
    }

    /**
     * See {@link MetalUtil#getAllItems(ParseGraph, String)}, using the graph contained in this selector.
     * The order of the list is in reverse order compared to the list returned by the linked method
     * (and the way Metal does it), i.e. items parsed earlier are at the front of the list.
     * */
    public ParseItemList getAllItems(final Token definition) {
        return Util.reverse(ByToken.getAll(_graph, definition));
    }

    @Override
    public String toString() {
        return "Selector[" + _graph.toString() + "]";
    }

    private static class SelectorList extends AbstractList<Selector> {

        private final Token _definition;
        private final List<Selector> _selectors;

        private SelectorList(final Token definition, final List<Selector> selectors) {
            _definition = definition;
            _selectors = selectors;
        }

        @Override
        public Selector get(final int index) {
            if (index >= size()) {
                throw new IndexOutOfBoundsException("[Index: " + index + ", Size: " + size() + "] for definition: " + _definition);
            }
            return _selectors.get(index);
        }

        @Override
        public int size() {
            return _selectors.size();
        }
    }

    private static final class Util {

        private Util() {
        }

        /**
         * Check if a certain value exists in a graph.
         *
         * @param graph the graph in which to search for the value
         * @param name the name of the value to search for
         * @return true if the value exists in the graph, otherwise false
         */
        public static boolean contains(final ParseGraph graph, final String name) {
            return graph.get(name) != null;
        }

        /**
         * Return the value with given name in the graph as a byte.
         *
         * @param graph the graph to search in
         * @param name the name of the value to search for
         * @return the value with given name in the graph as a byte
         * @throws IllegalArgumentException when no value exists with given name
         */
        public static byte getByte(final ParseGraph graph, final String name) {
            final ParseValue value = graph.get(name);
            if (value == null) {
                throw new IllegalArgumentException("Value doesn't exist: " + name);
            }
            return value.asNumeric().byteValue();
        }

        /**
         * Return the value with given name in the graph as a byte array.
         *
         * @param graph the graph to search in
         * @param name the name of the value to search for
         * @return the value with given name in the graph as a byte array
         * @throws IllegalArgumentException when no value exists with given name
         */
        public static byte[] getBytes(final ParseGraph graph, final String name) {
            final ParseValue value = graph.get(name);
            if (value == null) {
                throw new IllegalArgumentException("Value doesn't exist: " + name);
            }
            return value.getValue();
        }

        /**
         * Return the value with given name in the graph as an integer.
         *
         * @param graph the graph to search in
         * @param name the name of the value to search for
         * @return the value with given name in the graph as an integer
         * @throws IllegalArgumentException when no value exists with given name
         */
        public static int getInt(final ParseGraph graph, final String name) {
            final ParseValue value = graph.get(name);
            if (value == null) {
                throw new IllegalArgumentException("Value doesn't exist: " + name);
            }
            return value.asNumeric().intValue();
        }

        /**
         * Return the value with given name in the graph as a long.
         *
         * @param graph the graph to search in
         * @param name the name of the value to search for
         * @return the value with given name in the graph as a long
         * @throws IllegalArgumentException when no value exists with given name
         */
        public static long getLong(final ParseGraph graph, final String name) {
            final ParseValue value = graph.get(name);
            if (value == null) {
                throw new IllegalArgumentException("Value doesn't exist: " + name);
            }
            return value.asNumeric().longValue();
        }

        /**
         * Return the value with given name in the graph as a {@link BigInteger}.
         *
         * @param graph the graph to search in
         * @param name the name of the value to search for
         * @return the value with given name in the graph as a {@link BigInteger}
         * @throws IllegalArgumentException when no value exists with given name
         */
        public static BigInteger getBigInt(final ParseGraph graph, final String name) {
            final ParseValue value = graph.get(name);
            if (value == null) {
                throw new IllegalArgumentException("Value doesn't exist: " + name);
            }
            return value.asNumeric();
        }

        /**
         * Return the value with given name in the graph as a {@link String}.
         *
         * @param graph the graph to search in
         * @param name the name of the value to search for
         * @return the value with given name in the graph as a {@link String}
         * @throws IllegalArgumentException when no value exists with given name
         */
        public static String getString(final ParseGraph graph, final String name) {
            final ParseValue value = graph.get(name);
            if (value == null) {
                throw new IllegalArgumentException("Value doesn't exist: " + name);
            }
            return value.asString();
        }

        /**
         * Reverse a {@link ParseValueList}.
         *
         * @param list the list to reverse
         * @return a new list of values in reverse order of the given one
         */
        public static ParseValueList reverse(final ParseValueList list) {
            if (list.isEmpty()) {
                return ParseValueList.EMPTY;
            }
            ParseValueList oldList = list.tail;
            ParseValueList newList = ParseValueList.create(list.head);

            while (!oldList.isEmpty()) {
                newList = newList.add(oldList.head);
                oldList = oldList.tail;
            }
            return newList;
        }

        /**
         * Reverse a {@link ParseItemList}.
         *
         * @param list the list to reverse
         * @return a new list of values in reverse order of the given one
         */
        public static ParseItemList reverse(final ParseItemList list) {
            if (list.isEmpty()) {
                return ParseItemList.EMPTY;
            }
            ParseItemList oldList = list.tail;
            ParseItemList newList = ParseItemList.create(list.head);

            while (!oldList.isEmpty()) {
                newList = newList.add(oldList.head);
                oldList = oldList.tail;
            }
            return newList;
        }

        /**
         * Convert a {@link ParseValueList} to a {@link List}&lt{@link ParseValue}&gt.
         *
         * @param list the list to convert
         * @return a new list containing the values from the given list
         */
        public static List<ParseValue> toList(final ParseValueList list) {
            final List<ParseValue> entryList = new ArrayList<>();
            ParseValueList l = list;
            while (l.head != null) {
                entryList.add(l.head);
                l = l.tail;
            }
            return entryList;
        }

        /**
         * Convert a {@link ParseItemList} to a {@link List}&lt{@link ParseItem}&gt.
         *
         * @param list the list to convert
         * @return a new list containing the values from the given list
         */
        public static List<ParseItem> toList(final ParseItemList list) {
            final List<ParseItem> entryList = new ArrayList<>();
            ParseItemList l = list;
            while (l.head != null) {
                entryList.add(l.head);
                l = l.tail;
            }
            return entryList;
        }
    }
}
