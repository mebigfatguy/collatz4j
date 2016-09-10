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
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class CollatzData implements Iterable<Pair<CollatzValue, CollatzValue>> {

    private static BigInteger TWO = BigInteger.valueOf(2L);

    private Map<BigInteger, CollatzValue> reverseLookup = new ConcurrentHashMap<>();
    private Map<CollatzValue, CollatzValue> universe = new ConcurrentHashMap<>();
    private CollatzValue root;
    private BigInteger randomValue;

    public CollatzData() {
        root = new CollatzValue(BigInteger.ONE);
        reverseLookup.put(BigInteger.ONE, root);
        randomValue = BigInteger.ONE;
    }

    public void addRelationship(BigInteger from, BigInteger to) {

        CollatzValue fromCV = reverseLookup.get(from);
        if (fromCV == null) {
            fromCV = new CollatzValue(from);
            reverseLookup.put(from, fromCV);
        }

        CollatzValue toCV = reverseLookup.get(to);
        if (toCV == null) {
            toCV = new CollatzValue(to);
            reverseLookup.put(to, toCV);
        }

        if (fromCV.isOdd()) {
            toCV.setOddValueNode(fromCV);
        } else {
            toCV.setEvenValueNode(fromCV);
        }

        universe.put(fromCV, toCV);
    }

    public Pair<CollatzValue, CollatzValue> getRandomPair() {
        CollatzValue one = reverseLookup.get(randomValue);
        if ((one == null) || (one == root)) {
            // This condition is to avoid the 1, 2 pairing, as well as just non existent values
            one = root;
            randomValue = TWO;
        }

        randomValue = randomValue.add(BigInteger.ONE);
        CollatzValue two = reverseLookup.get(randomValue);

        if (two == null) {
            randomValue = BigInteger.ONE;
            return null;
        }

        return new Pair<>(one, two);
    }

    @Override
    public Iterator<Pair<CollatzValue, CollatzValue>> iterator() {
        return new CollatzIterator();
    }

    public class CollatzIterator implements Iterator<Pair<CollatzValue, CollatzValue>> {

        private Deque<CollatzValue> roots = new ArrayDeque<>();

        public CollatzIterator() {
            roots.add(root);
        }

        @Override
        public boolean hasNext() {
            return !roots.isEmpty();
        }

        @Override
        public Pair<CollatzValue, CollatzValue> next() {
            CollatzValue current = roots.removeFirst();

            CollatzValue oddValue = current.getOddValueNode();
            if (oddValue != null) {
                if (!oddValue.equals(root)) {
                    roots.addLast(oddValue);
                }
            }

            CollatzValue evenValue = reverseLookup.get(current.getValue().multiply(TWO));
            if (evenValue != null) {
                roots.addLast(evenValue);
            }

            return new Pair<>(current, universe.get(current));
        }
    }
}
