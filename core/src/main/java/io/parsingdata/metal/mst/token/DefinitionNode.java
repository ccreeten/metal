package io.parsingdata.metal.mst.token;


import java.util.Arrays;
import java.util.List;

import io.parsingdata.metal.mst.MSTNode;
import io.parsingdata.metal.mst.visitor.Visitor;
import io.parsingdata.metal.mst.visitor.VoidVisitor;
import io.parsingdata.metal.token.Def;

public final class DefinitionNode extends TokenNode {

    private final ValueExpressionNode size;
    private final ExpressionNode predicate;

    public DefinitionNode(final Def definition) {
        super(definition.name, definition.encoding);
        size = new ValueExpressionNode(definition.size);
        predicate = new ExpressionNode(definition.predicate);
    }

    public ValueExpressionNode size() {
        return size;
    }

    public ExpressionNode predicate() {
        return predicate;
    }

    @Override
    public List<MSTNode> children() {
        return Arrays.asList(size, predicate);
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
