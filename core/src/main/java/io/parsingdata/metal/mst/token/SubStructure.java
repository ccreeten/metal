package io.parsingdata.metal.mst.token;

import java.util.Arrays;
import java.util.List;

import io.parsingdata.metal.mst.MSTNode;
import io.parsingdata.metal.mst.visitor.Visitor;
import io.parsingdata.metal.mst.visitor.VoidVisitor;
import io.parsingdata.metal.token.Sub;

public final class SubStructure extends TokenNode {

    private final MSTNode token;
    private final ValueExpressionNode address;

    SubStructure(final Sub subStructure) {
        super(subStructure.name, subStructure.encoding);
        token = wrap(subStructure.token);
        address = new ValueExpressionNode(subStructure.address);
    }

    public MSTNode token() {
        return token;
    }

    public ValueExpressionNode address() {
        return address;
    }

    @Override
    public List<MSTNode> children() {
        return Arrays.asList(token, address);
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
