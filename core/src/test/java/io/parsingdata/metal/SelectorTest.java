package io.parsingdata.metal;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.str;

import io.parsingdata.metal.Selector.InvalidOperationException;

import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;

import java.io.IOException;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.token.Token;

public class SelectorTest {

    @Rule
    public final ExpectedException _thrown = ExpectedException.none();

    private static final Token VALUE = def("value", 1);

    private static final Token OTHER = def("other", 1);

    private static final Token ITEM = seq(
                                          VALUE,
                                          VALUE);

    private static final Token NAMED_ITEM = str("item", ITEM);

    // FORMAT is 6 bytes long
    private static final Token FORMAT = seq(
                                            VALUE,
                                            OTHER,
                                            ITEM,
                                            NAMED_ITEM);

    private static final Token REP_FORMAT = rep(FORMAT);

    @Test
    public void testNullSelector() {
        _thrown.expect(IllegalArgumentException.class);
        _thrown.expectMessage(containsString("selector may not be null"));
        Selector.on((Selector) null);
    }

    @Test
    public void testNullGraph() {
        _thrown.expect(IllegalArgumentException.class);
        _thrown.expectMessage(containsString("graph may not be null"));
        Selector.on((ParseGraph) null);
    }

    @Test
    public void testSelectOnDef() throws IOException {
        _thrown.expect(IllegalArgumentException.class);
        _thrown.expectMessage(containsString("Cannot select on Def"));
        final ParseResult result = FORMAT.parse(stream(0, 1, 2, 3, 4, 5), enc());
        Selector.on(result.environment.order).select(VALUE);
    }

    @Test
    public void testOutOfBounds() throws IOException {
        _thrown.expect(IndexOutOfBoundsException.class);
        _thrown.expectMessage(containsString("[Index: 2, Size: 1] for definition"));
        final ParseResult result = FORMAT.parse(stream(0, 1, 2, 3, 4, 5), enc());
        Selector.on(result.environment.order).select(NAMED_ITEM).get(2);
    }

    @Test
    public void testEmtpyGraph() {
        assertTrue(Selector.on(ParseGraph.EMPTY).getGraph().isEmpty());
    }

    @Test
    public void testSelectorOnSelector() throws IOException {
        final ParseResult result = VALUE.parse(stream(0), enc());
        final int value = Selector.on(Selector.on(result.environment.order)).getInt("value");
        assertThat(value, is(equalTo(0)));
    }
    
    @Test
    public void testSelectSingleOnEmptyGraph() throws IOException {
        _thrown.expect(InvalidOperationException.class);
        _thrown.expectMessage(containsString("structure contains no item with definition"));
        Selector.on(ParseGraph.EMPTY).selectSingle(NAMED_ITEM);
    }
    
    @Test
    public void testSelectSingleWhenMorePresent() throws IOException {
        _thrown.expect(InvalidOperationException.class);
        _thrown.expectMessage(containsString("structure contains more than 1 item with definition"));
        final ParseResult result = FORMAT.parse(stream(0, 1, 2, 3, 4, 5), enc());
        Selector.on(result.environment.order).selectSingle(ITEM);
    }

    @Test
    public void testGetFirstValue() throws IOException {
        final ParseResult result = FORMAT.parse(stream(0, 1, 2, 3, 4, 5), enc());
        final int value = Selector.on(result.environment.order).getInt("value");
        assertThat(value, is(equalTo(0)));
    }

    @Test
    public void testGetValues() throws IOException {
        final ParseResult result = FORMAT.parse(stream(0, 1, 2, 3, 4, 5), enc());
        final List<ParseValue> values = Selector.on(result.environment.order).getAllValues("value");
        assertThat(values.size(), is(equalTo(5)));
        final ParseValue firstValue = values.get(0);
        assertThat(firstValue.offset, is(equalTo(0L)));
        assertThat(firstValue.asNumeric().intValue(), is(equalTo(0)));
    }

    @Test
    public void testGetNamedItems() throws IOException {
        final ParseResult result = REP_FORMAT.parse(stream(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11), enc());
        final List<ParseItem> items = Selector.on(result.environment.order).getAllItems(NAMED_ITEM);
        assertThat(items.size(), is(equalTo(2)));
        assertThatItemsHaveDefinition(items, NAMED_ITEM);
    }

    @Test
    public void testSelectNamedItem() throws IOException {
        final ParseResult result = REP_FORMAT.parse(stream(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11), enc());
        final List<Selector> selectors = Selector.on(result.environment.order).select(NAMED_ITEM);
        assertThat(selectors.size(), is(equalTo(2)));
        assertThatSelectorsHaveDefinition(selectors, NAMED_ITEM);
    }

    @Test
    public void testGetValueAfterScopeRestriction() throws IOException {
        final ParseResult result = REP_FORMAT.parse(stream(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11), enc());
        final int value = Selector.on(result.environment.order).select(NAMED_ITEM).get(1).getInt("value");
        assertThat(value, is(equalTo(10)));
    }

    @Test
    public void testGetValueAfterDoubleScopeRestriction1() throws IOException {
        final ParseResult result = REP_FORMAT.parse(stream(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11), enc());
        final int value = Selector.on(result.environment.order)
            .selectFirst(REP_FORMAT)
            .selectSingle(NAMED_ITEM)
            .getInt("value");
        assertThat(value, is(equalTo(4)));
    }

    @Test
    public void testGetValueAfterDoubleScopeRestriction2() throws IOException {
        final ParseResult result = REP_FORMAT.parse(stream(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11), enc());
        final int value = Selector.on(result.environment.order)
            .select(REP_FORMAT).get(0)
            .selectSingle(NAMED_ITEM)
            .getInt("value");
        assertThat(value, is(equalTo(4)));
    }

    @Test
    public void testGetValueAfterDoubleScopeRestriction3() throws IOException {
        final ParseResult result = REP_FORMAT.parse(stream(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11), enc());
        final int value = Selector.on(result.environment.order)
            .selectFirst(REP_FORMAT)
            .selectSingle(NAMED_ITEM)
            .getInt("value");
        assertThat(value, is(equalTo(4)));
    }

    private void assertThatItemsHaveDefinition(final List<ParseItem> items, final Token definition) {
        for (final ParseItem item : items) {
            assertThatHasDefinition(item, definition);
        }
    }

    private void assertThatSelectorsHaveDefinition(final List<Selector> selectors, final Token definition) {
        for (final Selector selector : selectors) {
            assertThatHasDefinition(selector.getGraph(), definition);
        }
    }

    private void assertThatHasDefinition(final ParseItem item, final Token definition) {
        assertThat(item.getDefinition(), is(equalTo(definition)));
    }
}
