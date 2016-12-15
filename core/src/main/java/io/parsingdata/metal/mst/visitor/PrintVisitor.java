package io.parsingdata.metal.mst.visitor;

import java.util.List;

import io.parsingdata.metal.mst.MSTNode;
import io.parsingdata.metal.mst.token.ChoiceNode;
import io.parsingdata.metal.mst.token.DefinitionNode;
import io.parsingdata.metal.mst.token.ExpressionNode;
import io.parsingdata.metal.mst.token.RepNode;
import io.parsingdata.metal.mst.token.SequenceNode;
import io.parsingdata.metal.mst.token.ValueExpressionNode;

public final class PrintVisitor implements VoidVisitor {

    @Override
    public void visit(final DefinitionNode node) {
        System.out.print("Def(");
        printChildren(node.children());
        System.out.print(")");
    }

    @Override
    public void visit(final ChoiceNode node) {
        System.out.print("Cho(");
        printChildren(node.children());
        System.out.print(")");
    }

    @Override
    public void visit(final RepNode node) {
        System.out.print("Rep(");
        node.token().accept(this);
        System.out.print(")");
    }

    @Override
    public void visit(final SequenceNode node) {
        System.out.print("Seq(");
        printChildren(node.children());
        System.out.print(")");
    }

    @Override
    public void visit(final ExpressionNode node) {
        System.out.print(node.expression());
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
