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

package io.parsingdata.metal.data;

import static java.math.BigInteger.ZERO;

import static io.parsingdata.metal.Trampoline.complete;
import static io.parsingdata.metal.Trampoline.intermediate;
import static io.parsingdata.metal.Util.checkNotNegative;
import static io.parsingdata.metal.Util.checkNotNull;

import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import io.parsingdata.metal.Trampoline;
import io.parsingdata.metal.Util;
import io.parsingdata.metal.expression.value.Value;

public class ConcatenatedValueSource extends Source {

    public final ImmutableList<Value> values;
    public final BigInteger length;

    private ConcatenatedValueSource(final ImmutableList<Value> values, final BigInteger length) {
        this.values = checkNotNull(values, "values");
        this.length = checkNotNegative(length, "length");
    }

    public static Optional<ConcatenatedValueSource> create(final ImmutableList<Optional<Value>> optionalValues) {
        final ImmutableList<Value> values = unwrap(optionalValues, new ImmutableList<>()).computeResult();
        final BigInteger length = calculateTotalSize(values);
        if (length.compareTo(ZERO) == 0) {
            return Optional.empty();
        }
        return Optional.of(new ConcatenatedValueSource(values, length));
    }

    private static <T> Trampoline<ImmutableList<T>> unwrap(final ImmutableList<Optional<T>> input, final ImmutableList<T> output) {
        if (input.isEmpty()) {
            return complete(() -> output);
        }
        return input.head
            .map(value -> intermediate(() -> unwrap(input.tail, output.add(value))))
            .orElseGet(() -> intermediate(() -> unwrap(input.tail, output)));
    }

    private static BigInteger calculateTotalSize(final ImmutableList<Value> values) {
        return calculateTotalSize(values, ZERO).computeResult();
    }

    private static Trampoline<BigInteger> calculateTotalSize(final ImmutableList<Value> values, final BigInteger size) {
        if (values.isEmpty()) {
            return complete(() -> size);
        }
        return intermediate(() -> calculateTotalSize(values.tail, size.add(values.head.slice.length)));
    }

    @Override
    protected byte[] getData(final BigInteger offset, final BigInteger length) {
        if (!isAvailable(offset, length)) {
            throw new IllegalStateException("Data to read is not available (offset=" + offset + ";length=" + length + ";source=" + this + ").");
        }
        final byte[] output = new byte[length.intValueExact()];
        return getData(values, ZERO, ZERO, offset, length, output, () -> complete(() -> output)).computeResult();
    }

    private Trampoline<byte[]> getData(final ImmutableList<Value> values, final BigInteger currentOffset, final BigInteger currentDest, final BigInteger offset, final BigInteger length, final byte[] output, final Supplier<Trampoline<byte[]>> continuation) {
        if (length.compareTo(ZERO) <= 0) {
            return continuation.get();
        }
        final Slice headSlice = values.head.slice;
        if (currentOffset.add(headSlice.length).compareTo(offset) <= 0) {
            return getData(values.tail, currentOffset.add(headSlice.length), currentDest, offset, length, output, continuation);
        }
        final BigInteger localOffset = offset.subtract(currentOffset).compareTo(ZERO) < 0 ? ZERO : offset.subtract(currentOffset);
        final BigInteger toCopy = length.compareTo(headSlice.length.subtract(localOffset)) > 0 ? headSlice.length.subtract(localOffset) : length;
        final Source headSource = headSlice.source;
        if (headSource instanceof ConcatenatedValueSource) {
            final ConcatenatedValueSource concatenatedHeadSource = ((ConcatenatedValueSource) headSource);
            return intermediate(() -> concatenatedHeadSource.getData(concatenatedHeadSource.values, currentOffset, currentDest, headSlice.offset, headSlice.length, output, () -> intermediate(() -> getData(values.tail, currentOffset.add(headSlice.length), currentDest.add(toCopy), offset, length.subtract(toCopy), output, continuation))));
        }
        System.arraycopy(headSlice.getData(), localOffset.intValueExact(), output, currentDest.intValueExact(), toCopy.intValueExact());
        return intermediate(() -> getData(values.tail, currentOffset.add(headSlice.length), currentDest.add(toCopy), offset, length.subtract(toCopy), output, continuation));
    }


    @Override
    protected boolean isAvailable(final BigInteger offset, final BigInteger length) {
        return checkNotNegative(length, "length").add(checkNotNegative(offset, "offset")).compareTo(this.length) <= 0;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + values + "(" + length + "))";
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
            && Objects.equals(values, ((ConcatenatedValueSource)obj).values)
            && Objects.equals(length, ((ConcatenatedValueSource)obj).length);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), values, length);
    }

}
