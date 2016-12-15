package io.parsingdata.metal.select;

import static io.parsingdata.metal.Util.checkNotNull;

import java.math.BigInteger;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.data.selection.ByName;
import io.parsingdata.metal.data.selection.ByToken;
import io.parsingdata.metal.token.Def;
import io.parsingdata.metal.token.Token;

public final class Selector {

    private final ParseGraph graph;

    private Selector(final ParseGraph graph) {
        this.graph = graph;
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
        return on(selector.graph);
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
        final List<ParseItem> allItems = getAllItems(definition);
        for (final ParseItem item : allItems) {
            selectors.add(Selector.on(item.asGraph()));
        }
        return new SelectorList(definition, selectors);
    }

    /**
     * Returns the first item with a certain definition in the structure. It throws
     * an exception when the structure has no item.
     * <p>
     * It uses {@link #select(Token)} internally, so it is dependent in the behavior of this method.
     *
     * @param definition the definition of the item to search for and return
     * @return the first existing item with the given definition
     */
    public Selector selectFirst(final Token definition) {
        final List<Selector> select = select(definition);
        if (select.isEmpty()) {
            throw new InvalidOperationException("structure contains no item with definition" + definition);
        }
        return select.get(0);
    }

    /**
     * Returns the only item with a certain definition in the structure. It throws
     * an exception when the structure has no item or more than one item with this definition.
     * <p>
     * It uses {@link #select(Token)} internally, so it is dependent in the behavior of this method.
     *
     * @param definition the definition of the item to search for and return
     * @return the single existing item with the given definition
     */
    public Selector selectSingle(final Token definition) {
        final List<Selector> select = select(definition);
        if (select.isEmpty()) {
            throw new InvalidOperationException("structure contains no item with definition" + definition);
        }
        if(select.size() > 1) {
            throw new InvalidOperationException("structure contains more than 1 item with definition" + definition);
        }
        return select.get(0);
    }

    /**
     * Check if the structure contains an item of a certain definition.
     *
     * @param definition the definition to search for
     * @return true if the structure contains an item of given definition,
     *         false otherwise
     */
    public boolean contains(final Token definition) {
        return !select(definition).isEmpty();
    }

    /** See {@link Util#contains(ParseGraph, String)}. */
    public boolean contains(final String name) {
        return Util.contains(graph, name);
    }

    // TODO do we want to expose ParseGraph?

    /** Returns the original {@link ParseGraph} which this Selector was created on. */
    public ParseGraph getGraph() {
        return graph;
    }

    /**
     * See {@link Util#getBytes(ParseGraph, String)}, using the graph contained in this selector.
     * If multiple values are present, the earliest parsed value with given name will be returned,
     * which is the other way around compared to the linked method (and the way Metal does it).
     * */
    public byte[] getBytes(final String name) {
        return Util.getBytes(Util.reverse(graph), name);
    }

    /**
     * See {@link Util#getByte(ParseGraph, String)}, using the graph contained in this selector.
     * If multiple values are present, the earliest parsed value with given name will be returned,
     * which is the other way around compared to the linked method (and the way Metal does it).
     * */
    public byte getByte(final String name) {
        return Util.getByte(Util.reverse(graph), name);
    }

    /**
     * See {@link Util#getInt(ParseGraph, String)}, using the graph contained in this selector.
     * If multiple values are present, the earliest parsed value with given name will be returned,
     * which is the other way around compared to the linked method (and the way Metal does it).
     * */
    public int getInt(final String name) {
        return Util.getInt(Util.reverse(graph), name);
    }

    /**
     * See {@link Util#getLong(ParseGraph, String)}, using the graph contained in this selector.
     * If multiple values are present, the earliest parsed value with given name will be returned,
     * which is the other way around compared to the linked method (and the way Metal does it).
     * */
    public long getLong(final String name) {
        return Util.getLong(Util.reverse(graph), name);
    }

    /**
     * See {@link Util#getBigInt(ParseGraph, String)}, using the graph contained in this selector.
     * If multiple values are present, the earliest parsed value with given name will be returned,
     * which is the other way around compared to the linked method (and the way Metal does it).
     * */
    public BigInteger getBigInt(final String name) {
        return Util.getBigInt(Util.reverse(graph), name);
    }

    /**
     * See {@link Util#getString(ParseGraph, String)}, using the graph contained in this selector.
     * If multiple values are present, the earliest parsed value with given name will be returned,
     * which is the other way around compared to the linked method (and the way Metal does it).
     * */
    public String getString(final String name) {
        return Util.getString(Util.reverse(graph), name);
    }

    // TODO which of the following do we really need? (do we want to expose ParseValue/Item?)

    /**
     * See {@link ByName#getValue(ParseGraph, String)}, using the graph contained in this selector.
     * If multiple values are present, the earliest parsed value with given name will be returned,
     * which is the other way around compared to the linked method (and the way Metal does it).
     * */
    public ParseValue getValue(final String name) {
        return getAllValues(name).get(0);
    }

    /**
     * See {@link ByName#getAllValues(ParseGraph, String)}, using the graph contained in this selector.
     * The order of the list is in reverse order compared to the list returned by the linked method
     * (and the way Metal does it), i.e. items parsed earlier are at the front of the list.
     * */
    public List<ParseValue> getAllValues(final String name) {
        return Util.toListOfValues(Util.reverseValueList(ByName.getAllValues(graph, name)));
    }

    /**
     * See {@link ByToken#get(ParseGraph, String)}, using the graph contained in this selector.
     * If multiple values are present, the earliest parsed value with given name will be returned,
     * which is the other way around compared to the linked method (and the way Metal does it).
     * */
    public ParseItem getItem(final Token definition) {
        return getAllItems(definition).get(0);
    }

    /**
     * See {@link ByToken#getAllRoots(ParseGraph, String)}, using the graph contained in this selector.
     * The order of the list is in reverse order compared to the list returned by the linked method
     * (and the way Metal does it), i.e. items parsed earlier are at the front of the list.
     * */
    public List<ParseItem> getAllItems(final Token definition) {
        return Util.toListOfItems(Util.reverseItemList(ByToken.getAllRoots(graph, definition)));
    }

    @Override
    public String toString() {
        return "Selector[" + graph.toString() + "]";
    }

    private static final class SelectorList extends AbstractList<Selector> {

        final Token _definition;
        final List<Selector> _selectors;

        SelectorList(final Token definition, final List<Selector> selectors) {
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
}