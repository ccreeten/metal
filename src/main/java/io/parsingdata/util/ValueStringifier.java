package io.parsingdata.util;

import io.parsingdata.metal.data.ParseValue;

public interface ValueStringifier {

    public String toString(ParseValue value);
}