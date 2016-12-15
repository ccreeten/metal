package io.parsingdata.metal.mst.token;

import java.util.Arrays;
import java.util.List;

import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.mst.MSTNode;
import io.parsingdata.metal.mst.visitor.Visitor;
import io.parsingdata.metal.mst.visitor.VoidVisitor;
import io.parsingdata.metal.token.Pre;

public final class Predicate extends TokenNode {

    private final MSTNode token;
    private final ExpressionNode predicate;

    public Predicate(final Pre predicate) {
        super(predicate.name, predicate.encoding);
        token = wrap(predicate.token);
        this.predicate = new ExpressionNode(predicate.predicate);
    }

    public Predicate(final String name, final Encoding encoding, final MSTNode token, final ExpressionNode predicate) {
        super(name, encoding);
        this.token = token;
        this.predicate = predicate;
    }

    public MSTNode token() {
        return token;
    }

    public ExpressionNode predicate() {
        return predicate;
    }

    @Override
    public List<MSTNode> children() {
        return Arrays.asList(token, predicate);
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
