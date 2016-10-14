package io.parsingdata.metal.expression.value;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.util.EncodingFactory.signed;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.io.IOException;

import org.junit.Test;

import io.parsingdata.metal.Shorthand;
import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.OptionalValueList;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.data.selection.ByName;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.token.Token;

public class MapExpressionTest {

    private final Token values = rep(any("value"));

    @Test
    public void testToOffset() throws IOException {
        final ParseResult result = values.parse(stream(7, 8, 9), signed());
        final ValueExpression map = map(ref("value"), offset());
        final OptionalValueList list = map.eval(result.environment, signed());
        System.out.printf("%s on %s%n%s%n", map, ByName.getAllValues(result.environment.order, "value"), list);
    }

    @Test
    public void testNegate() throws IOException {
        final ParseResult result = values.parse(stream(2, 2, 2), signed());
        final ValueExpression map = map(ref("value"), neg());
        final OptionalValueList list = map.eval(result.environment, signed());
        System.out.printf("%s on %s%n%s%n", map, ByName.getAllValues(result.environment.order, "value"), list);
    }

    @Test
    public void testAddOne() throws IOException {
        final ParseResult result = values.parse(stream(5, 5, 5), signed());
        final ValueExpression map = map(ref("value"), add(con(1)));
        final OptionalValueList list = map.eval(result.environment, signed());
        System.out.printf("%s on %s%n%s%n", map, ByName.getAllValues(result.environment.order, "value"), list);
    }

    @Test
    public void testCat() throws IOException {
        final ParseResult result = values.parse(stream(5, 5, 5), signed());
        final ValueExpression map = map(ref("value"), cat(con(1)));
        final OptionalValueList list = map.eval(result.environment, signed());
        System.out.printf("%s on %s%n%s%n", map, ByName.getAllValues(result.environment.order, "value"), list);
    }

    private ValueExpression offset() {
        return Shorthand.offset(con());
    }

    private ValueExpression neg() {
        return Shorthand.neg(con());
    }

    private ValueExpression add(final ValueExpression value) {
        return Shorthand.add(con(), value);
    }

    private ValueExpression cat(final ValueExpression value) {
        return Shorthand.cat(con(), value);
    }

    public ValueExpression map(final ValueExpression values, final ValueExpression function) {
        return new Map(values, function);
    }

    public class Map implements ValueExpression {

        private final ValueExpression values;
        private final ValueExpression function;

        public Map(final ValueExpression values, final ValueExpression function) {
            this.values = checkNotNull(values, "values");
            this.function = checkNotNull(function, "indices");
        }

        @Override
        public OptionalValueList eval(final Environment env, final Encoding enc) {
            return eval(values.eval(env, enc), function, env, enc);
        }

        private OptionalValueList eval(final OptionalValueList list, final ValueExpression function, final Environment env, final Encoding enc) {
            if (list.isEmpty()) {
                return OptionalValueList.EMPTY;
            }
            if (function instanceof UnaryValueExpression) {
                return eval(list.tail, function, env, enc).add(eval(list.head, (UnaryValueExpression) function, env, enc));
            }
            return eval(list.tail, function, env, enc).add(eval(list.head, (BinaryValueExpression) function, env, enc));
        }

        private OptionalValue eval(final OptionalValue value, final UnaryValueExpression function, final Environment env, final Encoding enc) {
            if (!value.isPresent()) {
                return OptionalValue.empty();
            }
            return function.eval(value.get(), env, enc);
        }

        private OptionalValue eval(final OptionalValue value, final BinaryValueExpression function, final Environment env, final Encoding enc) {
            if (!value.isPresent()) {
                return OptionalValue.empty();
            }
            final OptionalValueList right = function.right.eval(env, enc);
            if (right.size != 1 || !right.head.isPresent()) {
                return OptionalValue.empty();
            }
            return function.eval(value.get(), right.head.get(), env, enc);
        }

        @Override
        public String toString() {
            if (function instanceof UnaryValueExpression) {
                return getClass().getSimpleName() + "(" + values + "," + function.getClass().getSimpleName() + "())";
            }
            final BinaryValueExpression f = (BinaryValueExpression) function;
            return getClass().getSimpleName() + "(" + values + "," + f.getClass().getSimpleName() + "(" + f.right + ")" + ")";
        }
    }

}
