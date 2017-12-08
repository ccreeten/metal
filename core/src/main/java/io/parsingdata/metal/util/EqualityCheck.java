package io.parsingdata.metal.util;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;

public interface EqualityCheck<T> {

    <U> EqualityCheck<T> check(Function<? super T, ? extends U> valueExtractor);

    <U> EqualityCheck<T> check(Function<? super T, ? extends U> valueExtractor, BiPredicate<? super U, ? super U> comparisonMethod);

    boolean evaluate();

    static <T> EqualityCheck<T> failed() {
        return new CompletedEqualityCheck<>(false);
    }

    static <T> EqualityCheck<T> succeeded() {
        return new CompletedEqualityCheck<>(true);
    }

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
        // liskov violation, subclass can never equal superclass
        if (left.getClass().equals(right.getClass())) {
            return new ProceedingEqualityCheck<>(left, (T) right);
        }
        return failed();
    }

    public static <T> EqualityCheck<T> equalityOf(final T left, final T right) {
        // for any non-null reference value x, x.equals(x) should return true
        if (left == right) {
            return succeeded();
        }
        // for any non-null reference value x, x.equals(null) should return false
        if (right == null) {
            return failed();
        }
        // liskov violation, subclass can never equal superclass
        if (left.getClass().equals(right.getClass())) {
            return new ProceedingEqualityCheck<>(left, right);
        }
        return failed();
    }

    static final class ProceedingEqualityCheck<T> implements EqualityCheck<T> {

        private final T left;
        private final T right;

        private ProceedingEqualityCheck(final T left, final T right) {
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