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

public final class Sector {

    public static final int SECTOR_SIZE = 1000;

    private int x;
    private int y;
    private int z;

    private Sector(int sectorX, int sectorY, int sectorZ) {
        x = sectorX;
        y = sectorY;
        z = sectorZ;
    }

    public static Sector fromValue(float[] location) {
        return new Sector((int) location[0] / SECTOR_SIZE, (int) location[1] / SECTOR_SIZE, (int) location[2] / SECTOR_SIZE);
    }

    @Override
    public int hashCode() {
        return (x << 8) ^ (y << 4) ^ z;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Sector)) {
            return false;
        }

        Sector that = (Sector) o;
        return (x == that.x) && (y == that.y) && (z == that.z);
    }
}
