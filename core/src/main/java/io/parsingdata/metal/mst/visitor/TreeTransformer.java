package io.parsingdata.metal.mst.visitor;

import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.mst.MSTNode;
import io.parsingdata.metal.mst.token.BoundedRepetition;
import io.parsingdata.metal.mst.token.Choice;
import io.parsingdata.metal.mst.token.Definition;
import io.parsingdata.metal.mst.token.DoWhile;
import io.parsingdata.metal.mst.token.ExpressionNode;
import io.parsingdata.metal.mst.token.NoData;
import io.parsingdata.metal.mst.token.OptionalToken;
import io.parsingdata.metal.mst.token.Predicate;
import io.parsingdata.metal.mst.token.Repetition;
import io.parsingdata.metal.mst.token.Sequence;
import io.parsingdata.metal.mst.token.SubStructure;
import io.parsingdata.metal.mst.token.TokenInToken;
import io.parsingdata.metal.mst.token.TokenNode;
import io.parsingdata.metal.mst.token.TokenReference;
import io.parsingdata.metal.mst.token.ValueExpressionNode;

public abstract class TreeTransformer implements Visitor<MSTNode> {

    @Override
    public TokenNode visit(final BoundedRepetition node) {
        final String name = node.name();
        final Encoding encoding = node.encoding();
        // TODO better way to ensure/enforce types? and make casts unnecessary?
        final TokenNode token = (TokenNode) node.token().accept(this);
        final ValueExpressionNode n = (ValueExpressionNode) node.n().accept(this);
        return new BoundedRepetition(name, encoding, token, n);
    }

    @Override
    public TokenNode visit(final Choice node) {
        final String name = node.name();
        final Encoding encoding = node.encoding();
        final TokenNode[] tokens = node.tokens().stream()
                .map(n -> (TokenNode) n.accept(this))
                .toArray(TokenNode[]::new);
        return new Choice(name, encoding, tokens);
    }

    @Override
    public TokenNode visit(final Definition node) {
        final String name = node.name();
        final Encoding encoding = node.encoding();
        final ValueExpressionNode size = (ValueExpressionNode) node.size().accept(this);
        final ExpressionNode predicate = (ExpressionNode) node.predicate().accept(this);
        return new Definition(name, encoding, size, predicate);
    }

    @Override
    public TokenNode visit(final DoWhile node) {
        final String name = node.name();
        final Encoding encoding = node.encoding();
        final TokenNode token = (TokenNode) node.token().accept(this);
        final ExpressionNode predicate = (ExpressionNode) node.predicate().accept(this);
        return new DoWhile(name, encoding, token, predicate);
    }

    @Override
    public ExpressionNode visit(final ExpressionNode node) {
        return node;
    }

    @Override
    public TokenNode visit(final NoData node) {
        final String name = node.name();
        final Encoding encoding = node.encoding();
        final ValueExpressionNode size = (ValueExpressionNode) node.size().accept(this);
        return new NoData(name, encoding, size);
    }

    @Override
    public TokenNode visit(final OptionalToken node) {
        final String name = node.name();
        final Encoding encoding = node.encoding();
        final TokenNode token = (TokenNode) node.token().accept(this);
        return new OptionalToken(name, encoding, token);
    }

    @Override
    public TokenNode visit(final Predicate node) {
        final String name = node.name();
        final Encoding encoding = node.encoding();
        final TokenNode token = (TokenNode) node.token().accept(this);
        final ExpressionNode predicate = (ExpressionNode) node.predicate().accept(this);
        return new Predicate(name, encoding, token, predicate);
    }

    @Override
    public TokenNode visit(final Repetition node) {
        final String name = node.name();
        final Encoding encoding = node.encoding();
        final TokenNode token = (TokenNode) node.token().accept(this);
        return new Repetition(name, encoding, token);
    }

    @Override
    public TokenNode visit(final Sequence node) {
        final String name = node.name();
        final Encoding encoding = node.encoding();
        final TokenNode[] tokens = node.tokens().stream()
                .map(n -> (TokenNode) n.accept(this))
                .toArray(TokenNode[]::new);
        return new Sequence(name, encoding, tokens);
    }

    @Override
    public TokenNode visit(final SubStructure node) {
        final String name = node.name();
        final Encoding encoding = node.encoding();
        final TokenNode token = (TokenNode) node.token().accept(this);
        final ValueExpressionNode address = (ValueExpressionNode) node.address().accept(this);
        return new SubStructure(name, encoding, token, address);
    }

    @Override
    public TokenNode visit(final TokenInToken node) {
        final String name = node.name();
        final Encoding encoding = node.encoding();
        final TokenNode token = (TokenNode) node.token().accept(this);
        final ValueExpressionNode dataExpression = (ValueExpressionNode) node.dataExpression().accept(this);
        return new TokenInToken(name, encoding, token, dataExpression);
    }

    @Override
    public TokenNode visit(final TokenReference node) {
        final String name = node.name();
        final Encoding encoding = node.encoding();
        final String referenceName = node.referenceName();
        return new TokenReference(name, encoding, referenceName);
    }

    @Override
    public ValueExpressionNode visit(final ValueExpressionNode node) {
        return node;
    }
}
