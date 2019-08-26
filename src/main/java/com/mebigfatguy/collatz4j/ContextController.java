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

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLException;

public class ContextController implements AutoCloseable {

    private GLContext context;

    public ContextController(GL2 gl) {
        context = gl.getContext();
        context.makeCurrent();
    }

    @Override
    public void close() {
        try {
            context.release();
        } catch (GLException e) {
            // nothing to do
        } finally {
            context = null;
        }

    }

}
