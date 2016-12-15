package io.parsingdata.metal.mst.token;

import java.util.Arrays;
import java.util.List;

import io.parsingdata.metal.mst.MSTNode;
import io.parsingdata.metal.mst.visitor.Visitor;
import io.parsingdata.metal.mst.visitor.VoidVisitor;
import io.parsingdata.metal.token.Opt;

public final class OptionalToken extends TokenNode {

    private final MSTNode token;

    OptionalToken(final Opt optionalToken) {
        super(optionalToken.name, optionalToken.encoding);
        token = wrap(optionalToken.token);
    }

    public MSTNode token() {
        return token;
    }

    @Override
    public List<MSTNode> children() {
        return Arrays.asList(token);
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
