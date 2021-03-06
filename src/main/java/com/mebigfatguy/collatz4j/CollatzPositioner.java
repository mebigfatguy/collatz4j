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

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CollatzPositioner implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(CollatzPositioner.class);
    private static final float RADIUS = 18.0f;

    private static final float LONG_REPEL_DISTANCE = RADIUS * 80.0f;
    private static final float LONG_REPEL_DISTANCE_SQUARED = LONG_REPEL_DISTANCE + LONG_REPEL_DISTANCE + LONG_REPEL_DISTANCE;

    private static final float SHORT_REPEL_DISTANCE = RADIUS * 40.0f;
    private static final float SHORT_REPEL_DISTANCE_SQUARED = SHORT_REPEL_DISTANCE + SHORT_REPEL_DISTANCE + SHORT_REPEL_DISTANCE;

    private static final float ATTRACTION_DISTANCE = RADIUS * 120.0f;
    private static final float ATTRACTION_DISTANCE_SQUARED = ATTRACTION_DISTANCE + ATTRACTION_DISTANCE + ATTRACTION_DISTANCE;

    private static final float SHORT_REPEL_MOVEMENT = 1.5f;
    private static final float LONG_REPEL_MOVEMENT = 2.0f;
    private static final float ATTRACTION_MOVEMENT = 4.0f;

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
        positionerThread.setPriority(Thread.MIN_PRIORITY + 1);
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
        try {
            while (!Thread.interrupted()) {
                Iterator<Pair<CollatzValue, CollatzValue>> it = data.getPositioningIterator();
                while (it.hasNext()) {
                    Pair<CollatzValue, CollatzValue> fromTo = it.next();

                    CollatzValue fromCV = fromTo.getKey();
                    float[] fromLocation = fromCV.getLocation();

                    CollatzValue toCV = fromTo.getValue();
                    if (toCV != null) {
                        float[] toLocation = toCV.getLocation();

                        float distanceSq = distanceSquared(fromLocation, toLocation);
                        if (distanceSq < SHORT_REPEL_DISTANCE_SQUARED) {
                            repel(fromLocation, toLocation, SHORT_REPEL_MOVEMENT);
                        } else {
                            if (isRelated(fromCV, toCV)) {
                                if (distanceSq > ATTRACTION_DISTANCE_SQUARED) {
                                    attract(fromLocation, toLocation, ATTRACTION_MOVEMENT);
                                }
                            } else {
                                if (distanceSq < LONG_REPEL_DISTANCE_SQUARED) {
                                    repel(fromLocation, toLocation, LONG_REPEL_MOVEMENT);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("CollatzPositioner has exited", e);
        }
    }

    private static boolean isRelated(CollatzValue fromCV, CollatzValue toCV) {

        if (toCV.equals(fromCV.getEvenValueNode()) || (fromCV.equals(toCV.getEvenValueNode()))) {
            return true;
        }
        if (toCV.equals(fromCV.getOddValueNode()) || fromCV.equals(toCV.getOddValueNode())) {
            return true;
        }

        return false;
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
            float adjust = repelSpeed * uv[i];
            fromLocation[i] -= adjust;
            toLocation[i] += adjust;
        }
    }

    private void attract(float[] fromLocation, float[] toLocation, float attractSpeed) {

        float[] uv = unitVector(fromLocation, toLocation);

        for (int i = 0; i < 3; ++i) {
            float adjust = attractSpeed * uv[i];
            fromLocation[i] += adjust;
            toLocation[i] -= adjust;
        }
    }

    private static float distanceSquared(float[] pos1, float[] pos2) {
        float x = pos1[0] - pos2[0];
        float y = pos1[1] - pos2[1];
        float z = pos1[2] - pos2[2];

        return (x * x) + (y * y) + (z * z);
    }
}
