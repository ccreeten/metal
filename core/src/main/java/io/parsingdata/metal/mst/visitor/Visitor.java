package io.parsingdata.metal.mst.visitor;

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

public interface Visitor<T> {

    // TODO: default implementation? if so, empty? basic recursive travel?

    T visit(BoundedRepetition node);

    T visit(Choice node);

    T visit(Definition node);

    T visit(DoWhile node);

    T visit(ExpressionNode node);

    T visit(NoData node);

    T visit(OptionalToken node);

    T visit(Predicate node);

    T visit(Repetition repNode);

    T visit(Sequence node);

    T visit(SubStructure node);

    T visit(TokenInToken node);

    T visit(TokenReference node);

    T visit(ValueExpressionNode node);
}
