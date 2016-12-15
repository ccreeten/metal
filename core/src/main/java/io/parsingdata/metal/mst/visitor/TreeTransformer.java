package io.parsingdata.metal.mst.visitor;

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
import io.parsingdata.metal.mst.token.TokenReference;
import io.parsingdata.metal.mst.token.ValueExpressionNode;

public class TreeTransformer implements Visitor<MSTNode> {

    @Override
    public MSTNode visit(final BoundedRepetition node) {
        visitChildren(node);
        return node;
    }

    @Override
    public MSTNode visit(final Choice node) {
        visitChildren(node);
        return node;
    }

    @Override
    public MSTNode visit(final Definition node) {
        visitChildren(node);
        return node;
    }

    @Override
    public MSTNode visit(final DoWhile node) {
        visitChildren(node);
        return node;
    }

    @Override
    public MSTNode visit(final ExpressionNode node) {
        visitChildren(node);
        return node;
    }

    @Override
    public MSTNode visit(final NoData node) {
        visitChildren(node);
        return node;
    }

    @Override
    public MSTNode visit(final OptionalToken node) {
        visitChildren(node);
        return node;
    }

    @Override
    public MSTNode visit(final Predicate node) {
        visitChildren(node);
        return node;
    }

    @Override
    public MSTNode visit(final Repetition node) {
        visitChildren(node);
        return node;
    }

    @Override
    public MSTNode visit(final Sequence node) {
        visitChildren(node);
        return node;
    }

    @Override
    public MSTNode visit(final SubStructure node) {
        visitChildren(node);
        return node;
    }

    @Override
    public MSTNode visit(final TokenInToken node) {
        visitChildren(node);
        return node;
    }

    @Override
    public MSTNode visit(final TokenReference node) {
        visitChildren(node);
        return node;
    }

    @Override
    public MSTNode visit(final ValueExpressionNode node) {
        visitChildren(node);
        return node;
    }

    protected void visitChildren(final MSTNode node) {
        for (final MSTNode child : node.children()) {
            child.accept(this);
        }
    }
}
