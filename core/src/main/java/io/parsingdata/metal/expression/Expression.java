/*
 * Copyright 2013-2016 Netherlands Forensic Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.parsingdata.metal.expression;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.encoding.Encoding;

/**
 * Interface for all Expression implementations.
 * <p>
 * An Expression is evaluated by calling the {@link #eval(ParseGraph, Encoding)} method. Given an {@link Environment} and an {@link Encoding}, the
 * evaluation either succeeds or fails. Main use of expressions is to define predicates that are evaluated during parsing.
 *
 * @see io.parsingdata.metal.token.Def
 * @see io.parsingdata.metal.token.Pre
 * @see io.parsingdata.metal.token.While
 */
@FunctionalInterface
public interface Expression {

    List<Boolean> eval(ParseGraph graph, Encoding encoding);

    default boolean evalAndReduce(final ParseGraph graph, final Encoding encoding) {
        return evalWith((l, r) -> l && r, graph, encoding);
    }

    default boolean evalWith(final BinaryOperator<Boolean> reduceOp, final ParseGraph graph, final Encoding encoding) {
        return eval(graph, encoding).stream().reduce(reduceOp::apply).orElse(false);
    }

    default <T> List<Boolean> map(final Function<? super T, Boolean> func, final List<T> values) {
        return values.stream().map(func::apply).collect(Collectors.toList());
    }

    default <T, E> List<Boolean> zipWith(final BiFunction<? super T, ? super E, Boolean> func, final List<T> left, final List<E> right) {
        return IntStream.range(0, Math.min(left.size(), right.size())).mapToObj(i -> func.apply(left.get(i), right.get(i))).collect(Collectors.toList());
    }
}
