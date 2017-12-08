package io.parsingdata.metal.util;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;

public interface EqualityCheck<T> {

    <U> EqualityCheck<U> thenAs(Class<? extends U> clazz);

    <U> EqualityCheck<U> andThenCheck(U left, Object right, Function<? super T, Object> equalityMethod);

    <U> EqualityCheck<T> check(Function<? super T, ? extends U> valueExtractor);

    <U> EqualityCheck<T> check(Function<? super T, ? extends U> valueExtractor, BiPredicate<? super U, ? super U> comparisonMethod);

    boolean evaluate();

    static <T> EqualityCheck<T> failed() {
        return new CompletedEqualityCheck<>(false);
    }

    static <T> EqualityCheck<T> succeeded() {
        return new CompletedEqualityCheck<>(true);
    }

    public static EqualityCheck<Object> equalityOf(final Object left, final Object right) {
        // for any non-null reference value x, x.equals(x) should return true
        if (left == right) {
            return succeeded();
        }
        // for any non-null reference value x, x.equals(null) should return false
        if (right == null) {
            return failed();
        }
        return new ProceedingEqualityCheck<>(left, right);
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

    @SuppressWarnings("unchecked")
    public static <T> EqualityCheck<T> isInstance(final T left, final Object right) {
        // for any non-null reference value x, x.equals(x) should return true
        if (left == right) {
            return succeeded();
        }
        // for any non-null reference value x, x.equals(null) should return false
        if (right == null) {
            return failed();
        }
        // symmetry violation, x.equals(y) != y.equals(x) with sub and superclass
        if (left.getClass().isInstance(right)) {
            return new ProceedingEqualityCheck<>(left, (T) right);
        }
        return failed();
    }

//    public static EqualityCheck<T> checkSuper()

    static final class ProceedingEqualityCheck<T> implements EqualityCheck<T> {

        private final T left;
        private final T right;

        private ProceedingEqualityCheck(final T left, final T right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public <U> EqualityCheck<U> thenAs(final Class<? extends U> clazz) {
            if (clazz.isInstance(left) && clazz.isInstance(right)) {
                return new ProceedingEqualityCheck<>(clazz.cast(left), clazz.cast(right));
            }
            return failed();
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
        public <U> EqualityCheck<U> andThenCheck(final U left, final Object right, final Function<? super T, Object> equalityMethod) {
            return null;
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
        public <U> EqualityCheck<U> thenAs(final Class<? extends U> clazz) {
            // TODO: could technically cast, ugly but safe, no new object necessary
            return new CompletedEqualityCheck<>(result);
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
        public <U> EqualityCheck<U> andThenCheck(final U left, final Object right, final Function<? super T, Object> equalityMethod) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean evaluate() {
            return result;
        }
    }
}