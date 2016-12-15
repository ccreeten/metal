package io.parsingdata.metal.mst.token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.parsingdata.metal.mst.MSTNode;
import io.parsingdata.metal.mst.visitor.Visitor;
import io.parsingdata.metal.mst.visitor.VoidVisitor;
import io.parsingdata.metal.token.Cho;
import io.parsingdata.metal.token.Token;

public final class Choice extends TokenNode {

    private final List<MSTNode> tokens = new ArrayList<>();

    Choice(final Cho choice) {
        super(choice.name, choice.encoding);
        for (final Token token : choice.tokens()) {
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
