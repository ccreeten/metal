package io.parsingdata.metal.mst.token;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.mst.MSTNode;
import io.parsingdata.metal.token.Cho;
import io.parsingdata.metal.token.Def;
import io.parsingdata.metal.token.Rep;
import io.parsingdata.metal.token.Seq;
import io.parsingdata.metal.token.Token;

public abstract class TokenNode implements MSTNode {

    private static final List<Wrapper<?>> _wrappers =
        Arrays.asList(new Wrapper<>(Cho.class, ChoiceNode::new),
                      new Wrapper<>(Rep.class, RepNode::new),
                      new Wrapper<>(Seq.class, SequenceNode::new),
                      new Wrapper<>(Def.class, DefinitionNode::new));

    private final String name;
    private final Encoding encoding;

    TokenNode(final String name, final Encoding encoding) {
        this.name = name;
        this.encoding = encoding;
    }

    public String name() {
        return name;
    }

    public Encoding encoding() {
        return encoding;
    }

    public static MSTNode wrap(final Token token) {
        for (final Wrapper<?> wrapper : _wrappers) {
            if (wrapper.matches(token)) {
                return wrapper.wrap(token);
            }
        }
        throw new RuntimeException("wrapper node doesn't exist for token of type: " + token.getClass());
    }

    private static class Wrapper<T extends Token> {

        private final Class<T> clazz;
        private final Function<T, MSTNode> constructor;

        public Wrapper(final Class<T> clazz, final Function<T, MSTNode> constructor) {
            this.clazz = clazz;
            this.constructor = constructor;
        }

        public boolean matches(final Token token) {
            return clazz.isInstance(token);
        }

        public MSTNode wrap(final Token token) {
            if (matches(token)) {
                return constructor.apply(clazz.cast(token));
            }
            throw new RuntimeException(String.format("token [%s] isn't an instance of registered class [%s]", token, clazz));
        }
    }
}
