package io.parsingdata.metal.mst.token;

import java.util.Arrays;
import java.util.List;

import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.mst.MSTNode;
import io.parsingdata.metal.mst.visitor.Visitor;
import io.parsingdata.metal.mst.visitor.VoidVisitor;
import io.parsingdata.metal.token.RepN;

public final class BoundedRepetition extends TokenNode {

    private final TokenNode token;
    private final ValueExpressionNode n;

    // TODO think about if the constructors should be public

    public BoundedRepetition(final RepN boundedRepitition) {
        super(boundedRepitition.name, boundedRepitition.encoding);
        token = wrap(boundedRepitition.token);
        n = new ValueExpressionNode(boundedRepitition.n);
    }

    public BoundedRepetition(final String name, final Encoding encoding, final TokenNode token, final ValueExpressionNode n) {
        super(name, encoding);
        this.token = token;
        this.n = n;
    }

    public TokenNode token() {
        return token;
    }

    public ValueExpressionNode n() {
        return n;
    }

    @Override
    public List<MSTNode> children() {
        return Arrays.asList(token, n);
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
