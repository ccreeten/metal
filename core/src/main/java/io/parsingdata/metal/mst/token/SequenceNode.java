package io.parsingdata.metal.mst.token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.parsingdata.metal.mst.MSTNode;
import io.parsingdata.metal.mst.visitor.Visitor;
import io.parsingdata.metal.mst.visitor.VoidVisitor;
import io.parsingdata.metal.token.Seq;
import io.parsingdata.metal.token.Token;

public final class SequenceNode extends TokenNode {

    private final List<MSTNode> tokens = new ArrayList<>();

    SequenceNode(final Seq sequence) {
        super(sequence.name, sequence.encoding);
        for (final Token token : sequence.tokens()) {
            tokens.add(wrap(token));
        }
    }

    @Override
    public List<MSTNode> children() {
        return Collections.unmodifiableList(tokens);
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
