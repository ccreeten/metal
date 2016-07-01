package io.parsingdata.metal.util;

import java.util.ArrayList;
import java.util.List;

import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.ParseItemList;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.data.ParseValueList;

public final class ListUtil {

    /**
     * Convert a {@link ParseValueList} to a {@link List}&lt{@link ParseValue}&gt.
     *
     * @param parseValueList the list to convert
     * @return a new list containing the values from the given list
     */
    public static List<ParseValue> toList(final ParseValueList parseValueList) {
        final List<ParseValue> entryList = new ArrayList<>();
        ParseValueList l = parseValueList;
        while (l.head != null) {
            entryList.add(l.head);
            l = l.tail;
        }
        return entryList;
    }

    /**
     * Convert a {@link ParseItemList} to a {@link List}&lt{@link ParseItem}&gt.
     *
     * @param parseItemList the list to convert
     * @return a new list containing the items from the given list
     */
    public static List<ParseItem> toList(final ParseItemList parseItemList) {
        final List<ParseItem> entryList = new ArrayList<>();
        ParseItemList l = parseItemList;
        while (l.head != null) {
            entryList.add(l.head);
            l = l.tail;
        }
        return entryList;
    }
}
