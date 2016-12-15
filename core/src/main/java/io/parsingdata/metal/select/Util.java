package io.parsingdata.metal.select;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.data.selection.ByName;
import io.parsingdata.metal.data.transformation.Reversal;

public final class Util {


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
        return get(graph, name) != null;
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
        final ParseValue value = get(graph, name);
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
        final ParseValue value = get(graph, name);
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
        final ParseValue value = get(graph, name);
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
        final ParseValue value = get(graph, name);
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
        final ParseValue value = get(graph, name);
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
        final ParseValue value = get(graph, name);
        if (value == null) {
            throw new IllegalArgumentException("Value doesn't exist: " + name);
        }
        return value.asString();
    }

    /** See {@link ByName#getValue(ParseGraph, String)}. */
    public static ParseValue get(final ParseGraph graph, final String name) {
        return ByName.getValue(graph, name);
    }

    /** See {@link Reversal#reverse(ParseGraph)}. */
    public static ParseGraph reverse(final ParseGraph graph) {
        return Reversal.reverse(graph);
    }

    /**
     * Reverse an {@link ImmutableList}&lt{@link ParseValue}&gt.
     *
     * @param list the list to reverse
     * @return a new list of values in reverse order of the given one
     */
    public static ImmutableList<ParseValue> reverseValueList(final ImmutableList<ParseValue> list) {
        if (list.isEmpty()) {
            return new ImmutableList<>();
        }
        ImmutableList<ParseValue> oldList = list.tail;
        ImmutableList<ParseValue> newList = ImmutableList.create(list.head);

        while (!oldList.isEmpty()) {
            newList = newList.add(oldList.head);
            oldList = oldList.tail;
        }
        return newList;
    }

    /**
     * Reverse an {@link ImmutableList}&lt{@link ParseItem}&gt.
     *
     * @param list the list to reverse
     * @return a new list of values in reverse order of the given one
     */
    public static ImmutableList<ParseItem> reverseItemList(final ImmutableList<ParseItem> list) {
        if (list.isEmpty()) {
            return new ImmutableList<>();
        }
        ImmutableList<ParseItem> oldList = list.tail;
        ImmutableList<ParseItem> newList = ImmutableList.create(list.head);

        while (!oldList.isEmpty()) {
            newList = newList.add(oldList.head);
            oldList = oldList.tail;
        }
        return newList;
    }

    /**
     * Convert a {@link ImmutableList<ParseValue>} to a {@link List}&lt{@link ParseValue}&gt.
     *
     * @param list the list to convert
     * @return a new list containing the values from the given list
     */
    public static List<ParseValue> toListOfValues(final ImmutableList<ParseValue> list) {
        final List<ParseValue> entryList = new ArrayList<>();
        ImmutableList<ParseValue> l = list;
        while (l.head != null) {
            entryList.add(l.head);
            l = l.tail;
        }
        return entryList;
    }

    /**
     * Convert a {@link ImmutableList<ParseItem>} to a {@link List}&lt{@link ParseItem}&gt.
     *
     * @param list the list to convert
     * @return a new list containing the values from the given list
     */
    public static List<ParseItem> toListOfItems(final ImmutableList<ParseItem> list) {
        final List<ParseItem> entryList = new ArrayList<>();
        ImmutableList<ParseItem> l = list;
        while (l.head != null) {
            entryList.add(l.head);
            l = l.tail;
        }
        return entryList;
    }
}
