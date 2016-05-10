package io.parsingdata.metal.expression.value;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.encoding.Encoding;

public class CachedExpression implements ValueExpression {

    private final ValueExpression _expression;
    private OptionalValue _cachedValue;

    public CachedExpression(final ValueExpression expression) {
        _expression = expression;
    }

    @Override
    public OptionalValue eval(final Environment env, final Encoding enc) {
        if (_cachedValue == null) {
            _cachedValue = _expression.eval(env, enc);
        }
        return _cachedValue;
    }

}
