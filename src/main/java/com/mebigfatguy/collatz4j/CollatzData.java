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

public final class CollatzData implements Iterable<Map.Entry<CollatzValue, CollatzValue>> {

    private Map<CollatzValue, CollatzValue> universe = new ConcurrentHashMap<>();

    public void addRelationship(BigInteger from, BigInteger to) {

        CollatzValue fromCV = new CollatzValue(from);
        CollatzValue toCV = new CollatzValue(to);
        if (fromCV.isOdd()) {
            toCV.setOddValue(fromCV);
        } else {
            toCV.setEvenValue(fromCV);
        }

        universe.put(fromCV, toCV);
    }

    @Override
    public Iterator<Map.Entry<CollatzValue, CollatzValue>> iterator() {
        return universe.entrySet().iterator();
    }
}
