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

package io.parsingdata.metal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Util.inflate;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;

import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.expression.value.Value;

@RunWith(Parameterized.class)
public class UtilHexTest {

    @Parameter
    public byte[] input;

    @Parameter(1)
    public String output;

    @Parameterized.Parameters(name = "{1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { new byte[] { 0x01, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0xab, (byte) 0xcd, (byte) 0xef}, "0123456789ABCDEF" },
                { new byte[] { -1 }, "FF" },
                { new byte[0], "" }
        });
    }

    @Test
    public void byteToHex() {
        assertThat(Util.bytesToHexString(input), is(equalTo(output)));
    }

    @Test
    public void inflateDataFormatError() {
        final ImmutableList<Optional<Value>> result = inflate(con(0xffffffff)).eval(stream().order, enc());
        assertEquals(1, result.size);
        assertFalse(result.head.isPresent());
    }

}
