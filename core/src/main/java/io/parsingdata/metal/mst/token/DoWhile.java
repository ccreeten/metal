package io.parsingdata.metal.mst.token;

import java.util.Arrays;
import java.util.List;

import io.parsingdata.metal.mst.MSTNode;
import io.parsingdata.metal.mst.visitor.Visitor;
import io.parsingdata.metal.mst.visitor.VoidVisitor;
import io.parsingdata.metal.token.While;

public final class DoWhile extends TokenNode {

    // TODO should these all be defined as MSTNodes? Or as the most specific type?
    // this counts for all Node classes

    private final MSTNode token;
    private final ExpressionNode predicate;

    DoWhile(final While doWhile) {
        super(doWhile.name, doWhile.encoding);
        token = wrap(doWhile.token);
        predicate = new ExpressionNode(doWhile.predicate);
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
