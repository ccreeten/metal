package io.parsingdata.metal.mst.visitor;

import io.parsingdata.metal.mst.token.ChoiceNode;
import io.parsingdata.metal.mst.token.DefinitionNode;
import io.parsingdata.metal.mst.token.ExpressionNode;
import io.parsingdata.metal.mst.token.RepNode;
import io.parsingdata.metal.mst.token.SequenceNode;
import io.parsingdata.metal.mst.token.ValueExpressionNode;

public interface VoidVisitor {

    void visit(DefinitionNode node);

    void visit(ChoiceNode node);

    void visit(RepNode repNode);

    void visit(SequenceNode node);

    void visit(ExpressionNode node);

    void visit(ValueExpressionNode node);
}
