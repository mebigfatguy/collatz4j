/*
 * collatz4j - a visualization of the Collatz Conjecture
 * Copyright 2016-2019 MeBigFatGuy.com
 * Copyright 2016-2019 Dave Brosius
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
import java.util.Arrays;
import java.util.Random;

public final class CollatzValue {

    private static final Random RANDOM = new Random();

    private BigInteger value;
    private CollatzValue oddValueName;
    private CollatzValue evenValueName;
    private float[] location;

    public CollatzValue(BigInteger v) {
        value = v;
        location = new float[] { (RANDOM.nextFloat() * 100) - 50, (RANDOM.nextFloat() * 100) - 50, (RANDOM.nextFloat() * 100) - 50 };
    }

    public CollatzValue(BigInteger v, CollatzValue nearValue) {
        value = v;
        location = nearValue.location.clone();
    }

    public CollatzValue getOddValueNode() {
        return oddValueName;
    }

    public void setOddValueNode(CollatzValue oddValue) {
        oddValueName = oddValue;
    }

    public CollatzValue getEvenValueNode() {
        return evenValueName;
    }

    public void setEvenValueNode(CollatzValue evenValue) {
        evenValueName = evenValue;
    }

    public float[] getLocation() {
        return location;
    }

    public BigInteger getValue() {
        return value;
    }

    public boolean isOdd() {
        return value.testBit(0);
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

    @Override
    public String toString() {
        return "Value: " + value + " Location: " + Arrays.toString(location);
    }
}
