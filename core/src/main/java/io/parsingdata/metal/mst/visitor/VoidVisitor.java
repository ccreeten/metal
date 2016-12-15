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

public interface VoidVisitor {

    default void visit(final BoundedRepetition node) {
        visitChildren(node);
    }

    default void visit(final Choice node) {
        visitChildren(node);
    }

    default void visit(final Definition node) {
        visitChildren(node);
    }

    default void visit(final DoWhile node) {
        visitChildren(node);
    }

    default void visit(final ExpressionNode node) {
        visitChildren(node);
    }

    default void visit(final NoData node) {
        visitChildren(node);
    }

    default void visit(final OptionalToken node) {
        visitChildren(node);
    }

    default void visit(final Predicate node) {
        visitChildren(node);
    }

    default void visit(final Repetition node) {
        visitChildren(node);
    }

    default void visit(final Sequence node) {
        visitChildren(node);
    }

    default void visit(final SubStructure node) {
        visitChildren(node);
    }

    default void visit(final TokenInToken node) {
        visitChildren(node);
    }

    default void visit(final TokenReference node) {
        visitChildren(node);
    }

    default void visit(final ValueExpressionNode node) {
        visitChildren(node);
    }

    default void visitChildren(final MSTNode node) {
        for (final MSTNode child : node.children()) {
            child.accept(this);
        }
    }
}
