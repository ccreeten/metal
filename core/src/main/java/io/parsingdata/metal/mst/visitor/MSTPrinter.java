package io.parsingdata.metal.mst.visitor;

import java.util.List;

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

public final class MSTPrinter implements VoidVisitor {

    @Override
    public void visit(final BoundedRepetition node) {
        // TODO Auto-generated method stub
    }

    @Override
    public void visit(final Choice node) {
        System.out.print("choice(");
        printChildren(node.children());
        System.out.print(")");
    }

    @Override
    public void visit(final Definition node) {
        System.out.print("definition(");
        printChildren(node.children());
        System.out.print(")");
    }

    @Override
    public void visit(final DoWhile node) {
        // TODO Auto-generated method stub
    }

    @Override
    public void visit(final ExpressionNode node) {
        System.out.print(node.expression());
    }

    @Override
    public void visit(final NoData node) {
        System.out.print("nodata(");
        printChildren(node.children());
        System.out.print(")");
    }

    @Override
    public void visit(final OptionalToken node) {
        // TODO Auto-generated method stub
    }

    @Override
    public void visit(final Predicate node) {
        // TODO Auto-generated method stub
    }

    @Override
    public void visit(final Repetition node) {
        System.out.print("repition(");
        node.token().accept(this);
        System.out.print(")");
    }

    @Override
    public void visit(final Sequence node) {
        System.out.print("sequence(");
        printChildren(node.children());
        System.out.print(")");
    }

    @Override
    public void visit(final SubStructure node) {
        // TODO Auto-generated method stub
    }

    @Override
    public void visit(final TokenInToken node) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(final TokenReference node) {
        // TODO Auto-generated method stub
    }

    @Override
    public void visit(final ValueExpressionNode node) {
        System.out.print(node.valueExpression());
    }

    private void printChildren(final List<MSTNode> children) {
        for (int i = 0; i < children.size() - 1; i++) {
            children.get(i).accept(this);
            System.out.print(",");
        }
        children.get(children.size() - 1).accept(this);
    }
}
