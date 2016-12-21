package io.parsingdata.metal.mst.token;

import java.util.Arrays;
import java.util.List;

import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.mst.MSTNode;
import io.parsingdata.metal.mst.visitor.Visitor;
import io.parsingdata.metal.mst.visitor.VoidVisitor;
import io.parsingdata.metal.token.Tie;

public final class TokenInToken extends TokenNode {

    private final TokenNode token;
    private final ValueExpressionNode dataExpression;

    public TokenInToken(final Tie tokenInToken) {
        super(tokenInToken.name, tokenInToken.encoding);
        token = wrap(tokenInToken.token);
        dataExpression = new ValueExpressionNode(tokenInToken.dataExpression);
    }

    public TokenInToken(final String name, final Encoding encoding, final TokenNode token, final ValueExpressionNode dataExpression) {
        super(name, encoding);
        this.token = token;
        this.dataExpression = dataExpression;
    }

    public TokenNode token() {
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
