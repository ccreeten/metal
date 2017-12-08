/*
 * Copyright 2013-2016 Netherlands Forensic Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.parsingdata.metal.encoding;

import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.util.EqualityCheck.sameClass;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class Encoding {

    public static final Sign DEFAULT_SIGN = Sign.UNSIGNED;
    public static final Charset DEFAULT_CHARSET = StandardCharsets.US_ASCII;
    public static final ByteOrder DEFAULT_BYTE_ORDER = ByteOrder.BIG_ENDIAN;

    public final Sign sign;
    public final Charset charset;
    public final ByteOrder byteOrder;

    public Encoding() {
        this(DEFAULT_SIGN, DEFAULT_CHARSET, DEFAULT_BYTE_ORDER);
    }

    public Encoding(final Sign signed) {
        this(signed, DEFAULT_CHARSET, DEFAULT_BYTE_ORDER);
    }

    public Encoding(final Charset charset) {
        this(DEFAULT_SIGN, charset, DEFAULT_BYTE_ORDER);
    }

    public Encoding(final ByteOrder byteOrder) {
        this(DEFAULT_SIGN, DEFAULT_CHARSET, byteOrder);
    }

    public Encoding(final Sign sign, final Charset charset, final ByteOrder byteOrder) {
        this.sign = checkNotNull(sign, "sign");
        this.charset = checkNotNull(charset, "charset");
        this.byteOrder = checkNotNull(byteOrder, "byteOrder");
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + sign + "," + charset + "," + byteOrder + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return sameClass(this, obj)
            .check(encoding -> encoding.sign)
            .check(encoding -> encoding.charset)
            .check(encoding -> encoding.byteOrder)
            .evaluate();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), sign, charset, byteOrder);
    }

}
