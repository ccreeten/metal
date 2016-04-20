/*
 * Copyright (c) 2016 Netherlands Forensic Institute
 * All rights reserved.
 */
package io.parsingdata.metal.data;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eqNum;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Range;
import org.junit.Test;

import io.parsingdata.metal.data.selection.ByToken;
import io.parsingdata.metal.token.Token;

public class ParseItemListTest {

    @Test
    public void testStreamSum() {
        final Token num = def("value", 1);

        final ParseGraph graph = parseResultGraph(stream(0, 1, 2), rep(num));
        final ParseItemList items = ByToken.getAll(graph, num);

        final int sum1 = items.stream().map(item -> item.asValue().asNumeric().intValue()).reduce(0, (l, r) -> l + r);
        final int sum2 = items.stream().map(ParseItem::asValue).map(ParseValue::asNumeric).mapToInt(BigInteger::intValue).sum();

        assertThat(sum1, is(equalTo(3)));
        assertThat(sum1, is(equalTo(sum2)));
    }

    @Test
    public void testStreamRanges() {
        final Token bodySize = def("size", 1);
        final Token body = def("body", ref("size"));
        final Token footer = def("footer", 1, eqNum(con(0)));
        final Token block = seq(bodySize, body, footer);

        final ParseGraph graph = parseResultGraph(stream(2, 1, 1, 0, 3, 1, 1, 1, 0, 1, 1, 0, 5, 1, 1, 1, 1, 1, 0), rep(block));
        final ParseItemList items = ByToken.getAll(graph, body);

        final long bodyCount = items.stream().count();
        assertThat(bodyCount, is(equalTo(4L)));

        final Function<ParseValue, Range<Long>> toRange = (value) -> Range.between(value.offset, value.offset + value.getValue().length);
        final Comparator<Range<Long>> byMinimum = (x, y) -> Long.compare(x.getMinimum(), y.getMinimum());

        final List<Range<Long>> expectedRanges = ranges(range(1, 3), range(5, 8), range(10, 11), range(13, 18));
        final List<Range<Long>> actualRanges = items.stream()
            .map(ParseItem::asValue)
            .map(toRange)
            .sorted(byMinimum)
            .collect(Collectors.toList());

        assertThat(actualRanges, is(equalTo(expectedRanges)));
    }

    private Range<Long> range(final long fromInclusive, final long toInclusive) {
        return Range.between(fromInclusive, toInclusive);
    }

    @SafeVarargs
    private final List<Range<Long>> ranges(final Range<Long>... ranges) {
        return Arrays.asList(ranges);
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
