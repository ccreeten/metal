package io.parsingdata.metal.mst.token;

import java.util.Collections;
import java.util.List;

import io.parsingdata.metal.expression.value.ValueExpression;
import io.parsingdata.metal.mst.MSTNode;
import io.parsingdata.metal.mst.visitor.Visitor;
import io.parsingdata.metal.mst.visitor.VoidVisitor;

public final class ValueExpressionNode implements MSTNode {

    private final ValueExpression valueExpression;

    public ValueExpressionNode(final ValueExpression valueExpression) {
        this.valueExpression = valueExpression;
    }

    public ValueExpression valueExpression() {
        return valueExpression;
    }

    @Override
    public List<MSTNode> children() {
        return Collections.emptyList();
    }

    @Override
    public <T> T accept(final Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public void accept(final VoidVisitor visitor) {
        visitor.visit(this);
    }

}
