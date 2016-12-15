package io.parsingdata.metal.mst.visitor;

import io.parsingdata.metal.mst.token.ChoiceNode;
import io.parsingdata.metal.mst.token.DefinitionNode;
import io.parsingdata.metal.mst.token.ExpressionNode;
import io.parsingdata.metal.mst.token.RepNode;
import io.parsingdata.metal.mst.token.SequenceNode;
import io.parsingdata.metal.mst.token.ValueExpressionNode;

public interface Visitor<T> {

    T visit(DefinitionNode node);

    T visit(ChoiceNode node);

    T visit(RepNode repNode);

    T visit(SequenceNode node);

    T visit(ExpressionNode node);

    T visit(ValueExpressionNode node);
}
