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

package nl.minvenj.nfi.ddrx;

import static nl.minvenj.nfi.ddrx.util.TokenDefinitions.env;

import static nl.minvenj.nfi.ddrx.util.TokenDefinitions.any;
import static nl.minvenj.nfi.ddrx.util.TokenDefinitions.equalsRef;
import static nl.minvenj.nfi.ddrx.util.TokenDefinitions.stream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import nl.minvenj.nfi.ddrx.token.Sequence;
import nl.minvenj.nfi.ddrx.token.Token;

@RunWith(JUnit4.class)
public class NameBind {
    
    private Token sequenceMatch2 = new Sequence(
                                                any("a"),
                                                equalsRef("b", "a"));
    private Token sequenceMatch3 = new Sequence(
                                                sequenceMatch2,
                                                equalsRef("c", "a"));
    private Token sequenceMatchTransitive3 = new Sequence(
                                                          sequenceMatch2,
                                                          equalsRef("c", "b"));

    @Test
    public void sequenceMatch2() {
        Assert.assertTrue(sequenceMatch2.eval(stream(42, 42), env()));
    }
    
    @Test
    public void sequenceNoMatch2() {
        Assert.assertFalse(sequenceMatch2.eval(stream(42, 21), env()));
    }
    
    @Test
    public void sequenceMatch3() {
        Assert.assertTrue(sequenceMatch3.eval(stream(42, 42, 42), env()));
    }
    
    @Test
    public void sequenceNoMatch3() {
        Assert.assertFalse(sequenceMatchTransitive3.eval(stream(42, 42, 21), env()));
        Assert.assertFalse(sequenceMatchTransitive3.eval(stream(42, 21, 42), env()));
        Assert.assertFalse(sequenceMatchTransitive3.eval(stream(21, 42, 42), env()));
        Assert.assertFalse(sequenceMatchTransitive3.eval(stream(21, 42, 63), env()));
    }
    
    @Test
    public void sequenceMatchTransitive3() {
        Assert.assertTrue(sequenceMatchTransitive3.eval(stream(42, 42, 42), env()));
    }
    
    @Test
    public void sequenceNoMatchTransitive3() {
        Assert.assertFalse(sequenceMatchTransitive3.eval(stream(42, 42, 21), env()));
        Assert.assertFalse(sequenceMatchTransitive3.eval(stream(42, 21, 42), env()));
        Assert.assertFalse(sequenceMatchTransitive3.eval(stream(21, 42, 42), env()));
        Assert.assertFalse(sequenceMatchTransitive3.eval(stream(21, 42, 63), env()));
    }
    
}