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
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class CollatzData implements Iterable<Map.Entry<CollatzValue, CollatzValue>> {

    private static final float[] DEFAULT_LOCATION = new float[3];
    private Queue<Sector> sectors = new ConcurrentLinkedQueue<>();
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
            sectors.add(fromSector);
        }
        values.add(fromCV);

        Sector toSector = Sector.fromValue(DEFAULT_LOCATION);
        values = universe.get(toSector);
        if (values == null) {
            values = Collections.newSetFromMap(new ConcurrentHashMap<>());
            universe.put(toSector, values);
            sectors.add(toSector);
        }
        values.add(toCV);
    }

    @Override
    public Iterator<Entry<CollatzValue, CollatzValue>> iterator() {
        return relationships.entrySet().iterator();
    }

    /**
     * figure out what to do about sectors race condition
     *
     * @return
     */
    public Set<CollatzValue> getRandomSector() {
        if (sectors.isEmpty()) {
            return Collections.emptySet();
        }

        Sector sector = sectors.remove();
        sectors.add(sector);

        Set<CollatzValue> values = universe.get(sector);
        if (values == null) {
            return Collections.emptySet();
        }

        return values;
    }

    public CollatzValue getRelationship(CollatzValue value) {
        return relationships.get(value);
    }
}
