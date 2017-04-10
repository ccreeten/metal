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

package io.parsingdata.metal.token.util;

import java.io.IOException;

public interface Trampoline<T> {

    T result();
    boolean hasNext();
    Trampoline<T> next() throws IOException;

    default T computeResult() throws IOException {
        Trampoline<T> current = this;
        while(current.hasNext()) {
            current = current.next();
        }
        return current.result();
    }
    
    static <E> FinalTrampoline<E> base(final FinalTrampoline<E> trampoline) {
    	return trampoline;
    }

    static <E> IntermediateTrampoline<E> recurse(final IntermediateTrampoline<E> trampoline) {
    	return trampoline;
    }
}
