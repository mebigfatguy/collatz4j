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

public class CollatzPositioner implements Runnable {

    private static final float RADIUS = 10.0f;

    private static final float LONG_REPEL_DISTANCE = RADIUS * 8.0f;
    private static final float LONG_REPEL_DISTANCE_SQUARED = LONG_REPEL_DISTANCE * LONG_REPEL_DISTANCE;

    private static final float SHORT_REPEL_DISTANCE = RADIUS * 3.0f;
    private static final float SHORT_REPEL_DISTANCE_SQUARED = SHORT_REPEL_DISTANCE * SHORT_REPEL_DISTANCE;

    private static final float ATTRACTION_DISTANCE = RADIUS * 4.0f;
    private static final float ATTRACTION_DISTANCE_SQUARED = ATTRACTION_DISTANCE * ATTRACTION_DISTANCE;

    private CollatzData data;
    private Thread positionerThread;

    public CollatzPositioner(CollatzData collatzData) {
        data = collatzData;
    }

    public void beginPositioning() {
        if (positionerThread != null) {
            return;
        }

        positionerThread = new Thread(this);
        positionerThread.setDaemon(true);
        positionerThread.setPriority(Thread.MIN_PRIORITY);
        positionerThread.start();
    }

    public void endPositioning() {
        if (positionerThread == null) {
            return;
        }

        try {
            positionerThread.interrupt();
            positionerThread.join(2000);
        } catch (InterruptedException e) {
            // nothing to do
        } finally {
            positionerThread = null;
        }
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            for (CollatzValue from : data.getRandomSector()) {
                CollatzValue to = data.getRelationship(from);
                if (to != null) {
                    float[] fromLocation = from.getLocation();
                    float[] toLocation = to.getLocation();

                    if (isCloseTo(fromLocation, toLocation, SHORT_REPEL_DISTANCE_SQUARED)) {
                        repel(fromLocation, toLocation, SHORT_REPEL_DISTANCE / 3);
                    } else if (isFarAwayFrom(fromLocation, toLocation)) {
                        attract(fromLocation, toLocation);
                    }
                }
            }
        }
    }

    private static boolean isCloseTo(float[] fromLocation, float[] toLocation, float distance) {
        float distanceSq = distanceSquared(fromLocation, toLocation);
        return distanceSq < distance;
    }

    public static float[] unitVector(float[] fromLocation, float[] toLocation) {
        float[] uv = { toLocation[0] - fromLocation[0], toLocation[1] - fromLocation[1], toLocation[2] - fromLocation[2] };
        float denom = (float) Math.sqrt((uv[0] * uv[0]) + (uv[1] * uv[1]) + (uv[2] * uv[2]));

        if (denom == 0.0f) {
            uv[0] = (float) ((Math.random() * 10.0) - 5.0);
            uv[1] = (float) ((Math.random() * 10.0) - 5.0);
            uv[2] = (float) ((Math.random() * 10.0) - 5.0);
            denom = (float) Math.sqrt((uv[0] * uv[0]) + (uv[1] * uv[1]) + (uv[2] * uv[2]));
        }

        uv[0] /= denom;
        uv[1] /= denom;
        uv[2] /= denom;

        return uv;
    }

    private static void repel(float[] fromLocation, float[] toLocation, float repelSpeed) {

        float[] uv = unitVector(fromLocation, toLocation);

        for (int i = 0; i < 3; ++i) {
            fromLocation[i] += -repelSpeed * uv[i];
            toLocation[i] += +repelSpeed * uv[i];
        }
    }

    private void attract(float[] fromLocation, float[] toLocation) {

        float[] uv = unitVector(fromLocation, toLocation);

        for (int i = 0; i < 3; ++i) {
            fromLocation[i] += 0.5 * uv[i];
            toLocation[i] += -0.5 * uv[i];
        }
    }

    private static boolean isFarAwayFrom(float[] fromLocation, float[] toLocation) {

        float distanceSq = distanceSquared(fromLocation, toLocation);
        return distanceSq > ATTRACTION_DISTANCE_SQUARED;
    }

    private static float distanceSquared(float[] pos1, float[] pos2) {
        float x = pos1[0] - pos2[0];
        float y = pos1[1] - pos2[1];
        float z = pos1[2] - pos2[2];

        return (x * x) + (y * y) + (z * z);
    }
}
