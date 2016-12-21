package io.parsingdata.metal.mst;

import static io.parsingdata.metal.Shorthand.cho;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eqNum;
import static io.parsingdata.metal.Shorthand.expTrue;
import static io.parsingdata.metal.Shorthand.opt;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.mst.token.Choice;
import io.parsingdata.metal.mst.token.ExpressionNode;
import io.parsingdata.metal.mst.token.Sequence;
import io.parsingdata.metal.mst.token.TokenNode;
import io.parsingdata.metal.mst.visitor.ToTokenTransformer;
import io.parsingdata.metal.mst.visitor.TreeTransformer;
import io.parsingdata.metal.select.Selector;
import io.parsingdata.metal.token.Token;

public class ConvertToTokenVisitorTest {

    @Test
    public void testSimpleConversion() {
        final Token token = cho(def("a", 1), def("b", 1));
        final MSTNode mst = TokenNode.wrap(token);
        final Token rebuiltToken = mst.accept(new ToTokenTransformer());
        assertThat(token.toString(), is(equalTo(rebuiltToken.toString())));
    }

    @Test
    public void testConvertChoToSeq() throws IOException {
        final Environment environment = stream(0, 1);
        final Encoding encoding = enc();

        final Token originalToken = cho(def("a", 1), def("b", 1));
        final ParseResult orginalResult = originalToken.parse(environment, encoding);
        final Selector originalStruct = Selector.on(orginalResult.environment.order);
        assertTrue(originalStruct.contains("a"));
        assertFalse(originalStruct.contains("b"));

        final Token newToken = TokenNode.wrap(originalToken).accept(new SeqToChoConverter()).accept(new ToTokenTransformer());
        final ParseResult newResult = newToken.parse(environment, encoding);
        final Selector newStruct = Selector.on(newResult.environment.order);
        assertTrue(newStruct.contains("a"));
        assertTrue(newStruct.contains("b"));
    }

    @Test
    public void testConvertChoToSeqRecursive() throws IOException {
        final Environment environment = stream(0, 1, 2);
        final Encoding encoding = enc();

        final Token originalToken = cho(def("a", 1), cho(def("b", 1), def("c", 1)));
        final ParseResult orginalResult = originalToken.parse(environment, encoding);
        final Selector originalStruct = Selector.on(orginalResult.environment.order);
        assertTrue(originalStruct.contains("a"));
        assertFalse(originalStruct.contains("b"));
        assertFalse(originalStruct.contains("c"));

        final Token newToken = TokenNode.wrap(originalToken).accept(new SeqToChoConverter()).accept(new ToTokenTransformer());
        final ParseResult newResult = newToken.parse(environment, encoding);
        final Selector newStruct = Selector.on(newResult.environment.order);
        assertTrue(newStruct.contains("a"));
        assertTrue(newStruct.contains("b"));
        assertTrue(newStruct.contains("c"));
    }

    @Test
    public void removeConstraintsTest() throws IOException {
        final Environment environment = stream(0, 1, 2);
        final Encoding encoding = enc();

        final Token originalToken = seq(opt(def("a", 1, eqNum(con(2)))), seq(opt(def("b", 1, eqNum(con(1)))), opt(def("c", 1, eqNum(con(0))))));
        final ParseResult orginalResult = originalToken.parse(environment, encoding);
        final Selector originalStruct = Selector.on(orginalResult.environment.order);
        assertFalse(originalStruct.contains("a"));
        assertFalse(originalStruct.contains("b"));
        assertTrue(originalStruct.contains("c"));

        final Token newToken = TokenNode.wrap(originalToken).accept(new ExpressionToTrueConverter()).accept(new ToTokenTransformer());
        final ParseResult newResult = newToken.parse(environment, encoding);
        final Selector newStruct = Selector.on(newResult.environment.order);
        assertTrue(newStruct.contains("a"));
        assertTrue(newStruct.contains("b"));
        assertTrue(newStruct.contains("c"));
    }

    private static class SeqToChoConverter extends TreeTransformer {

        @Override
        public TokenNode visit(final Choice node) {
            final String name = node.name();
            final Encoding encoding = node.encoding();
            final TokenNode[] tokens = node.tokens().stream()
                    .map(n -> (TokenNode) n.accept(this))
                    .toArray(TokenNode[]::new);
            return new Sequence(name, encoding, tokens);
        }
    }

    private static class ExpressionToTrueConverter extends TreeTransformer {

        @Override
        public ExpressionNode visit(final ExpressionNode node) {
            return new ExpressionNode(expTrue());
        }
    }
}
