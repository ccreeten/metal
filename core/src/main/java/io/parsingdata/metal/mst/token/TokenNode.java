package io.parsingdata.metal.mst.token;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.mst.MSTNode;
import io.parsingdata.metal.token.Cho;
import io.parsingdata.metal.token.Def;
import io.parsingdata.metal.token.Nod;
import io.parsingdata.metal.token.Opt;
import io.parsingdata.metal.token.Pre;
import io.parsingdata.metal.token.Rep;
import io.parsingdata.metal.token.RepN;
import io.parsingdata.metal.token.Seq;
import io.parsingdata.metal.token.Sub;
import io.parsingdata.metal.token.Tie;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.token.TokenRef;
import io.parsingdata.metal.token.While;

public abstract class TokenNode implements MSTNode {

    private static final List<Wrapper<?>> wrappers =
        Arrays.asList(new Wrapper<>(Cho.class, Choice::new),
                      new Wrapper<>(Def.class, Definition::new),
                      new Wrapper<>(Nod.class, NoData::new),
                      new Wrapper<>(Opt.class, OptionalToken::new),
                      new Wrapper<>(Pre.class, Predicate::new),
                      new Wrapper<>(Rep.class, Repetition::new),
                      new Wrapper<>(RepN.class, BoundedRepetition::new),
                      new Wrapper<>(Seq.class, Sequence::new),
                      new Wrapper<>(Sub.class, SubStructure::new),
                      new Wrapper<>(Tie.class, TokenInToken::new),
                      new Wrapper<>(TokenRef.class, TokenReference::new),
                      new Wrapper<>(While.class, DoWhile::new));

    private final String name;
    private final Encoding encoding;

    protected TokenNode(final String name, final Encoding encoding) {
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
        for (final Wrapper<?> wrapper : wrappers) {
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
