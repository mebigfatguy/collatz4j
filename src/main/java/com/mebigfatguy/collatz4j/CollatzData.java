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
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class CollatzData {

    private static final float[] DEFAULT_LOCATION = new float[3];

    private Map<CollatzValue, CollatzValue> relationships = new ConcurrentHashMap<>();
    private Map<Sector, Set<CollatzValue>> universe = new ConcurrentHashMap<>();

    public void addRelationship(BigInteger from, BigInteger to) {

        CollatzValue fromCV = new CollatzValue(from);
        CollatzValue toCV = new CollatzValue(to);

        relationships.put(fromCV, toCV);

        Sector fromSector = Sector.fromValue(DEFAULT_LOCATION);
        Set<CollatzValue> values = universe.get(fromSector);
        if (values == null) {
            values = Collections.newSetFromMap(new ConcurrentHashMap<>());
            universe.put(fromSector, values);
        }
        values.add(fromCV);

        Sector toSector = Sector.fromValue(DEFAULT_LOCATION);
        values = universe.get(toSector);
        if (values == null) {
            values = Collections.newSetFromMap(new ConcurrentHashMap<>());
            universe.put(toSector, values);
        }
        values.add(toCV);
    }

    public Iterator<CollatzValue> getSectorIterator(Sector sector) {
        Set<CollatzValue> values = universe.get(sector);
        if (values == null) {
            return Collections.emptyIterator();
        }

        return values.iterator();
    }

    public CollatzValue getRelationship(CollatzValue value) {
        return relationships.get(value);
    }
}
