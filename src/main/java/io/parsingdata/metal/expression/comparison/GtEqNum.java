/*
 * Copyright (c) 2016 Netherlands Forensic Institute
 * All rights reserved.
 */
package io.parsingdata.metal.expression.comparison;

import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;

public class GtEqNum extends ComparisonExpression {

    public GtEqNum(final ValueExpression current, final ValueExpression predicate) {
        super(current, predicate);
    }

    @Override
    public boolean compare(final Value current, final Value predicate) {
        return current.asNumeric().compareTo(predicate.asNumeric()) >= 0;
    }

}
