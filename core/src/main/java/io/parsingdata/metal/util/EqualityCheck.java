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
package io.parsingdata.metal.util;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;

public interface EqualityCheck<T> {

    <U> EqualityCheck<T> check(Function<? super T, ? extends U> valueExtractor);

    <U> EqualityCheck<T> check(Function<? super T, ? extends U> valueExtractor, BiPredicate<? super U, ? super U> comparisonMethod);

    boolean evaluate();

    @SuppressWarnings("unchecked")
    public static <T> EqualityCheck<T> sameClass(final T left, final Object right) {
        // for any non-null reference value x, x.equals(x) should return true
        if (left == right) {
            return succeeded();
        }
        // for any non-null reference value x, x.equals(null) should return false
        if (right == null) {
            return failed();
        }
        // technically a liskov violation, subclass can never equal superclass
        if (left.getClass().equals(right.getClass())) {
            return new IntermediateEqualityCheck<>(left, (T) right);
        }
        return failed();
    }

    static <T> EqualityCheck<T> failed() {
        return new CompletedEqualityCheck<>(false);
    }

    static <T> EqualityCheck<T> succeeded() {
        return new CompletedEqualityCheck<>(true);
    }

    static final class IntermediateEqualityCheck<T> implements EqualityCheck<T> {

        private final T left;
        private final T right;

        private IntermediateEqualityCheck(final T left, final T right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public <R> EqualityCheck<T> check(final Function<? super T, ? extends R> valueExtractor) {
            return check(valueExtractor, Objects::equals);
        }

        @Override
        public <U> EqualityCheck<T> check(final Function<? super T, ? extends U> valueExtractor, final BiPredicate<? super U, ? super U> comparisonMethod) {
            if (comparisonMethod.test(valueExtractor.apply(left), valueExtractor.apply(right))) {
                return this;
            }
            return failed();
        }

        @Override
        public boolean evaluate() {
            return true;
        }
    }

    // TODO: could let the checking methods have a default implementation
    // then for succeed/fail I could create a constant, using a lambda returning true/false
    // using raw types, like for example Collections#EMPTY_LIST
    static final class CompletedEqualityCheck<T> implements EqualityCheck<T> {

        private final boolean result;

        private CompletedEqualityCheck(final boolean result) {
            this.result = result;
        }

        @Override
        public <U> EqualityCheck<T> check(final Function<? super T, ? extends U> valueExtractor) {
            return this;
        }

        @Override
        public <U> EqualityCheck<T> check(final Function<? super T, ? extends U> valueExtractor, final BiPredicate<? super U, ? super U> comparisonMethod) {
            return this;
        }

        @Override
        public boolean evaluate() {
            return result;
        }
    }
}