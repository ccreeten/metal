package io.parsingdata.metal.mst.visitor;

import static io.parsingdata.metal.Shorthand.cho;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.Shorthand.seq;

import io.parsingdata.metal.mst.token.ChoiceNode;
import io.parsingdata.metal.mst.token.DefinitionNode;
import io.parsingdata.metal.mst.token.ExpressionNode;
import io.parsingdata.metal.mst.token.RepNode;
import io.parsingdata.metal.mst.token.SequenceNode;
import io.parsingdata.metal.mst.token.ValueExpressionNode;
import io.parsingdata.metal.token.Token;

public class ConvertToTokenVisitor implements Visitor<Token> {

    @Override
    public Token visit(final DefinitionNode node) {
        return def(node.name(), node.size().valueExpression(), node.predicate().expression(), node.encoding());
    }

    @Override
    public Token visit(final ChoiceNode node) {
        return cho(node.children().stream().map(n -> n.accept(this)).toArray(Token[]::new));
    }

    @Override
    public Token visit(final RepNode node) {
        return rep(node.token().accept(this));
    }

    @Override
    public Token visit(final SequenceNode node) {
        return seq(node.children().stream().map(n -> n.accept(this)).toArray(Token[]::new));
    }

    @Override
    public Token visit(final ExpressionNode node) {
        throw new IllegalStateException("cannot convert expression to Token");
    }

    @Override
    public Token visit(final ValueExpressionNode node) {
        throw new IllegalStateException("cannot convert value expression to Token");
    }
}
