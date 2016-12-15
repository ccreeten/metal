package io.parsingdata.metal.mst.visitor;

import static io.parsingdata.metal.Shorthand.cho;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.nod;
import static io.parsingdata.metal.Shorthand.opt;
import static io.parsingdata.metal.Shorthand.pre;
import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.Shorthand.repn;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.sub;
import static io.parsingdata.metal.Shorthand.tie;
import static io.parsingdata.metal.Shorthand.token;
import static io.parsingdata.metal.Shorthand.whl;

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
import io.parsingdata.metal.token.Token;

public class ToTokenTransformer implements Visitor<Token> {

    @Override
    public Token visit(final BoundedRepetition node) {
        return repn(node.name(), node.token().accept(this), node.n().valueExpression(), node.encoding());
    }

    @Override
    public Token visit(final Choice node) {
        return cho(node.children().stream().map(n -> n.accept(this)).toArray(Token[]::new));
    }

    @Override
    public Token visit(final Definition node) {
        return def(node.name(), node.size().valueExpression(), node.predicate().expression(), node.encoding());
    }

    @Override
    public Token visit(final DoWhile node) {
        return whl(node.name(), node.token().accept(this), node.predicate().expression(), node.encoding());
    }

    @Override
    public Token visit(final ExpressionNode node) {
        throw new IllegalStateException("cannot convert expression to Token");
    }

    @Override
    public Token visit(final NoData node) {
        return nod(node.name(), node.size().valueExpression());
    }

    @Override
    public Token visit(final OptionalToken node) {
        return opt(node.name(), node.token().accept(this), node.encoding());
    }

    @Override
    public Token visit(final Predicate node) {
        return pre(node.name(), node.token().accept(this), node.predicate().expression(), node.encoding());
    }

    @Override
    public Token visit(final Repetition node) {
        return rep(node.token().accept(this));
    }

    @Override
    public Token visit(final Sequence node) {
        return seq(node.children().stream().map(n -> n.accept(this)).toArray(Token[]::new));
    }

    @Override
    public Token visit(final SubStructure node) {
        return sub(node.name(), node.token().accept(this), node.address().valueExpression(), node.encoding());
    }

    @Override
    public Token visit(final TokenInToken node) {
        return tie(node.name(), node.token().accept(this), node.dataExpression().valueExpression(), node.encoding());
    }

    @Override
    public Token visit(final TokenReference node) {
        return token(node.referenceName());
    }

    @Override
    public Token visit(final ValueExpressionNode node) {
        throw new IllegalStateException("cannot convert value expression to Token");
    }
}
