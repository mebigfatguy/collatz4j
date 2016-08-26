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

        }
    }
}