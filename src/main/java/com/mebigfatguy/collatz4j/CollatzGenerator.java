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

public final class CollatzGenerator implements Runnable, TerminationListener {

    private static final long GENERATION_TIME = 500L;
    private CollatzData data;
    private Thread genThread;

    public CollatzGenerator(CollatzData collatzData) {
        data = collatzData;
    }

    @Override
    public void terminate() {
        try {
            genThread.interrupt();
            genThread.join(10000);
        } catch (InterruptedException e) {
            // just ignore
        } finally {
            genThread = null;
        }
    }

    public void generate() {
        if (genThread != null) {
            return;
        }

        genThread = new Thread(this);
        genThread.setDaemon(true);
        genThread.setPriority(Thread.MIN_PRIORITY);
        genThread.start();
    }

    @Override
    public void run() {
        BigInteger three = BigInteger.valueOf(3);

        data.addRelationship(BigInteger.ONE, BigInteger.valueOf(4L));
        data.addRelationship(BigInteger.valueOf(2L), BigInteger.ONE);
        data.addRelationship(three, BigInteger.TEN);
        data.addRelationship(BigInteger.valueOf(4L), BigInteger.valueOf(2L));

        BigInteger nextValue = BigInteger.valueOf(5L);

        try {
            while (!Thread.interrupted()) {
                BigInteger to;

                Thread.sleep(GENERATION_TIME);

                if (nextValue.testBit(0)) {
                    to = nextValue.multiply(three).add(BigInteger.ONE);
                } else {
                    to = nextValue.shiftRight(1);
                }

                data.addRelationship(nextValue, to);

                nextValue = nextValue.add(BigInteger.ONE);
            }
        } catch (InterruptedException e) {

        }
    }
}
