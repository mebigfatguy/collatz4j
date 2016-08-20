/*
 * collatz4j - a visualization of the Collatz Conjecture
 * Copyright 2016 MeBigFatGuy.com
 * Copyright 2016 Dave Brosius
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.mebigfatguy.collatz4j;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CollatzData implements Iterable<Map.Entry<BigInteger, ValueDetails>> {

    private Map<BigInteger, ValueDetails> relationships = new ConcurrentHashMap<>();

    public void addRelationship(BigInteger from, BigInteger to) {
        relationships.put(from, new ValueDetails(to));
    }

    @Override
    public Iterator<Map.Entry<BigInteger, ValueDetails>> iterator() {
        return relationships.entrySet().iterator();
    }
}
