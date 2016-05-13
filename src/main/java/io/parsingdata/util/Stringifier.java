package io.parsingdata.util;

import io.parsingdata.metal.data.ParseValue;

public interface Stringifier {

    public String toString(ParseValue value);
}