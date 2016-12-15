package io.parsingdata.metal.mst.token;

import java.util.Arrays;
import java.util.List;

import io.parsingdata.metal.mst.MSTNode;
import io.parsingdata.metal.mst.visitor.Visitor;
import io.parsingdata.metal.mst.visitor.VoidVisitor;
import io.parsingdata.metal.token.Nod;

public final class NoData extends TokenNode {

    private final ValueExpressionNode size;

    NoData(final Nod noData) {
        // encoding is always null
        super(noData.name, noData.encoding);
        size = new ValueExpressionNode(noData.size);
    }

    public ValueExpressionNode size() {
        return size;
    }

    @Override
    public List<MSTNode> children() {
        return Arrays.asList(size);
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
