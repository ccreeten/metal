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

public interface VoidVisitor {

    void visit(BoundedRepetition node);

    void visit(Choice node);

    void visit(Definition node);

    void visit(DoWhile node);

    void visit(ExpressionNode node);

    void visit(NoData node);

    void visit(OptionalToken node);

    void visit(Predicate node);

    void visit(Repetition repNode);

    void visit(Sequence node);

    void visit(SubStructure node);

    void visit(TokenInToken node);

    void visit(TokenReference node);

    void visit(ValueExpressionNode node);
}
