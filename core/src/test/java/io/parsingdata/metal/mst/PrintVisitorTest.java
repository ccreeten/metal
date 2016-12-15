package io.parsingdata.metal.mst;

import org.junit.Test;

import static io.parsingdata.metal.Shorthand.cho;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.Shorthand.seq;

import io.parsingdata.metal.mst.token.TokenNode;
import io.parsingdata.metal.mst.token.ValueExpressionNode;
import io.parsingdata.metal.mst.visitor.MSTPrinter;
import io.parsingdata.metal.mst.visitor.VoidVisitor;
import io.parsingdata.metal.token.Token;

public class PrintVisitorTest {

    @Test
    public void testCho() {
        final Token token = cho(def("a", 1), def("b", 1));
        final MSTNode mst = TokenNode.wrap(token);
        mst.accept(new MSTPrinter());
        System.out.println();
    }

    @Test
    public void testSeq() {
        final Token token = seq(def("a", 1), def("b", 1));
        final MSTNode mst = TokenNode.wrap(token);
        mst.accept(new MSTPrinter());
        System.out.println();
    }

    @Test
    public void testRepSeq() {
        final Token token = rep(seq(def("a", 1), def("b", 1)));
        final MSTNode mst = TokenNode.wrap(token);
        mst.accept(new MSTPrinter());
        System.out.println();
    }

    @Test
    public void printValues() {
        final Token token = rep(seq(def("a", 1), def("b", 1)));
        final MSTNode mst = TokenNode.wrap(token);
        mst.accept(new ValuePrinter());
    }

    private static class ValuePrinter implements VoidVisitor {

        @Override
        public void visit(final ValueExpressionNode node) {
            System.out.println(node.valueExpression());
        }
    }
}
