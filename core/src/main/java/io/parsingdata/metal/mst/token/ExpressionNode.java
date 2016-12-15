package io.parsingdata.metal.mst.token;

import java.util.Collections;
import java.util.List;

import io.parsingdata.metal.expression.Expression;
import io.parsingdata.metal.mst.MSTNode;
import io.parsingdata.metal.mst.visitor.Visitor;
import io.parsingdata.metal.mst.visitor.VoidVisitor;

public final class ExpressionNode implements MSTNode {

    private final Expression expression;

    public ExpressionNode(final Expression expression) {
        this.expression = expression;
    }

    public Expression expression() {
        return expression;
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
