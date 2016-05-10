package io.parsingdata.metal.expression.comparison;

import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;

public class LtEqNum extends ComparisonExpression {

    public LtEqNum(final ValueExpression current, final ValueExpression predicate) {
        super(current, predicate);
    }

    @Override
    public boolean compare(final Value current, final Value predicate) {
        return current.asNumeric().compareTo(predicate.asNumeric()) <= 0;
    }

}
