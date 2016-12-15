package io.parsingdata.metal.mst.token;

import java.util.Arrays;
import java.util.List;

import io.parsingdata.metal.mst.MSTNode;
import io.parsingdata.metal.mst.visitor.Visitor;
import io.parsingdata.metal.mst.visitor.VoidVisitor;
import io.parsingdata.metal.token.Tie;

public final class TokenInToken extends TokenNode {

    private final MSTNode token;
    private final ValueExpressionNode dataExpression;

    TokenInToken(final Tie tokenInToken) {
        super(tokenInToken.name, tokenInToken.encoding);
        token = wrap(tokenInToken.token);
        dataExpression = new ValueExpressionNode(tokenInToken.dataExpression);
    }

    public MSTNode token() {
        return token;
    }

    public ValueExpressionNode dataExpression() {
        return dataExpression;
    }

    @Override
    public List<MSTNode> children() {
        return Arrays.asList(token, dataExpression);
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
