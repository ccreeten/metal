package io.parsingdata.metal.mst.token;

import java.util.Arrays;
import java.util.List;

import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.mst.MSTNode;
import io.parsingdata.metal.mst.visitor.Visitor;
import io.parsingdata.metal.mst.visitor.VoidVisitor;
import io.parsingdata.metal.token.Opt;

public final class OptionalToken extends TokenNode {

    private final MSTNode token;

    public OptionalToken(final Opt optionalToken) {
        super(optionalToken.name, optionalToken.encoding);
        token = wrap(optionalToken.token);
    }

    public OptionalToken(final String name, final Encoding encoding, final MSTNode token) {
        super(name, encoding);
        this.token = token;
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
