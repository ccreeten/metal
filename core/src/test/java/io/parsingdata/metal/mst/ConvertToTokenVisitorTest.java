package io.parsingdata.metal.mst;

import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import static io.parsingdata.metal.Shorthand.cho;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.seq;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.mst.token.ChoiceNode;
import io.parsingdata.metal.mst.token.TokenNode;
import io.parsingdata.metal.mst.visitor.ConvertToTokenVisitor;
import io.parsingdata.metal.select.Selector;
import io.parsingdata.metal.token.Token;

public class ConvertToTokenVisitorTest {

    @Test
    public void testSimpleConversion() {
        final Token token = cho(def("a", 1), def("b", 1));
        final MSTNode mst = TokenNode.wrap(token);
        final Token rebuiltToken = mst.accept(new ConvertToTokenVisitor());
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

        final Token newToken = TokenNode.wrap(originalToken).accept(new SeqToChoTransformer());
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

        final Token newToken = TokenNode.wrap(originalToken).accept(new SeqToChoTransformer());
        final ParseResult newResult = newToken.parse(environment, encoding);
        final Selector newStruct = Selector.on(newResult.environment.order);
        assertTrue(newStruct.contains("a"));
        assertTrue(newStruct.contains("b"));
        assertTrue(newStruct.contains("c"));
    }

    private static class SeqToChoTransformer extends ConvertToTokenVisitor {

        @Override
        public Token visit(final ChoiceNode node) {
            return seq(node.children().stream().map(n -> n.accept(this)).toArray(Token[]::new));
        }
    }
}
