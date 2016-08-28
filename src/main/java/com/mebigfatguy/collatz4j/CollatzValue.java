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

public final class CollatzValue {

    private BigInteger value;
    private float[] location;

    public CollatzValue(BigInteger v) {
        value = v;
        float pos = Sector.SECTOR_SIZE / 2;
        location = new float[3];
    }

    public float[] getLocation() {
        return location;
    }

    public BigInteger getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CollatzValue)) {
            return false;
        }

        CollatzValue that = (CollatzValue) o;

        return value.equals(that.value);
    }
}
