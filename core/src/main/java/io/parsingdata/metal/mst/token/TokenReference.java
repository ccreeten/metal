package io.parsingdata.metal.mst.token;

import java.util.Collections;
import java.util.List;

import io.parsingdata.metal.mst.MSTNode;
import io.parsingdata.metal.mst.visitor.Visitor;
import io.parsingdata.metal.mst.visitor.VoidVisitor;
import io.parsingdata.metal.token.TokenRef;

public final class TokenReference extends TokenNode {

    private final String referenceName;

    public TokenReference(final TokenRef tokenReference) {
        super(tokenReference.name, tokenReference.encoding);
        referenceName = tokenReference.referenceName;
    }

    public String referenceName() {
        return referenceName;
    }

    @Override
    public List<MSTNode> children() {
        return Collections.emptyList();
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
