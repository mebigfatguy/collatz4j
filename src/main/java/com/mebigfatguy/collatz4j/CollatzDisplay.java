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

import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

import com.jogamp.newt.Display;
import com.jogamp.newt.NewtFactory;
import com.jogamp.newt.Screen;
import com.jogamp.newt.event.KeyAdapter;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.awt.TextRenderer;

public final class CollatzDisplay {

    private static final float[] ORIGIN = { 0.0f, 0.0f, 0.0f };
    private static final float STEP_SIZE = 10.0f;
    private static final float ROTATION_SIZE = (float) (Math.PI / 180.0f);

    private static final float[] AMBIENT = { 0.7f, 0.7f, 0.7f, 1 };
    private static final float[] SPECULAR = { 0.5f, 0.5f, 0.5f, 1 };
    private static final float[] DIFFUSE = { 1, 1, 1, 1 };
    private static final float[] LIGHT_POSITION = { 0, 3000, 2000, 1 };

    private final Set<TerminationListener> listeners = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private GLWindow glWindow;
    private Animator animator;
    private TextRenderer textRenderer;
    private CollatzPositioner positioner;
    private float[] eyeLocation = { 0, 0, 500 };

    private CollatzData data;

    public CollatzDisplay(CollatzData collatzData) {
        data = collatzData;
    }

    public void addTerminationListener(TerminationListener terminationListener) {
        listeners.add(terminationListener);
    }

    public void display() {
        GLProfile profile = GLProfile.getDefault();
        final GLCapabilities caps = new GLCapabilities(profile);
        caps.setBackgroundOpaque(true);
        Display display = NewtFactory.createDisplay(null);
        Screen screen = NewtFactory.createScreen(display, 0);
        glWindow = GLWindow.create(screen, caps);
        glWindow.setTitle("Collatz Conjecture");
        glWindow.setSize(1000, 800);

        glWindow.addGLEventListener(new CDEvents());
        glWindow.addKeyListener(new CDKeyListener());
        glWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowDestroyed(WindowEvent arg0) {
                for (TerminationListener l : listeners) {
                    l.terminate();
                }
            }
        });
        centerWindow(glWindow);

        animator = new Animator();
        animator.setModeBits(false, AnimatorBase.MODE_EXPECT_AWT_RENDERING_THREAD);
        animator.setExclusiveContext(false);

        animator.add(glWindow);
        animator.start();

        glWindow.setVisible(true);
        animator.setUpdateFPSFrames(20, null);

        positioner = new CollatzPositioner(data);
        positioner.beginPositioning();
    }

    private static void centerWindow(GLWindow window) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();
        Rectangle screenBounds = gc.getBounds();

        int w = window.getWidth();
        int h = window.getHeight();

        window.setPosition((screenBounds.width - w) / 2, (screenBounds.height - h) / 3);
    }

    private static float[] unitVector(float[] pos1, float[] pos2) {
        float[] uv = { pos2[0] - pos1[0], pos2[1] - pos1[1], pos2[2] - pos1[2] };
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

    void render(GLAutoDrawable drawable, GL2 gl, Map.Entry<CollatzValue, CollatzValue> entry) {
        CollatzValue from = entry.getKey();
        float[] fromLocation = from.getLocation();

        CollatzValue to = entry.getValue();
        float[] toLocation = to.getLocation();

        try {
            textRenderer.beginRendering(drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
            textRenderer.setColor(1.0f, 0.2f, 0.2f, 0.8f);

            textRenderer.draw3D(from.getValue().toString(), fromLocation[0], fromLocation[1], fromLocation[2], 1.0f);

            textRenderer.draw3D(to.getValue().toString(), toLocation[0], toLocation[1], toLocation[2], 1.0f);
        } finally {
            textRenderer.endRendering();
        }
    }

    class CDEvents implements GLEventListener {

        private GLU glu;

        @Override
        public void display(GLAutoDrawable drawable) {
            GL2 gl = drawable.getGL().getGL2();

            gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

            for (Map.Entry<CollatzValue, CollatzValue> entry : data) {
                render(drawable, gl, entry);
            }
        }

        @Override
        public void dispose(GLAutoDrawable drawable) {
            positioner.endPositioning();
        }

        @Override
        public void init(GLAutoDrawable drawable) {
            glu = new GLU();

            GL2 gl = drawable.getGL().getGL2();

            gl.glEnable(GL.GL_DEPTH_TEST);
            gl.glDepthFunc(GL.GL_LEQUAL);
            gl.glShadeModel(GLLightingFunc.GL_SMOOTH);

            gl.glEnable(GLLightingFunc.GL_LIGHTING);
            gl.glEnable(GLLightingFunc.GL_LIGHT0);

            gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_AMBIENT, AMBIENT, 0);
            gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_SPECULAR, SPECULAR, 0);
            gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_DIFFUSE, DIFFUSE, 0);
            gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_POSITION, LIGHT_POSITION, 0);

            textRenderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 24));
        }

        @Override
        public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
            GL2 gl = drawable.getGL().getGL2();
            gl.glViewport(0, 0, width, height);

            gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
            gl.glLoadIdentity();

            float widthHeightRatio = (float) width / (float) height;
            glu.gluPerspective(45, widthHeightRatio, 1, 1000);
            glu.gluLookAt(eyeLocation[0], eyeLocation[1], eyeLocation[2], 0, 0, 0, 0, 1, 0);

            gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
            gl.glLoadIdentity();
        }
    }

    class CDKeyListener extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            GL2 gl = glWindow.getGL().getGL2();
            gl.getContext().makeCurrent();

            try {

                gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
                gl.glLoadIdentity();

                int keyCode = e.getKeyCode();
                if (keyCode == KeyEvent.VK_UP) {
                    float[] uv = unitVector(ORIGIN, eyeLocation);
                    for (int i = 0; i < 3; ++i) {
                        uv[i] *= STEP_SIZE;
                        eyeLocation[i] -= uv[i];
                    }
                } else if (keyCode == KeyEvent.VK_DOWN) {
                    float[] uv = unitVector(ORIGIN, eyeLocation);
                    for (int i = 0; i < 3; ++i) {
                        uv[i] *= STEP_SIZE;
                        eyeLocation[i] += uv[i];
                    }
                } else if (keyCode == KeyEvent.VK_LEFT) {
                    float x = (float) ((Math.cos(ROTATION_SIZE) * eyeLocation[0]) - (Math.sin(ROTATION_SIZE) * eyeLocation[2]));
                    float z = (float) ((Math.sin(ROTATION_SIZE) * eyeLocation[0]) + (Math.cos(ROTATION_SIZE) * eyeLocation[2]));
                    eyeLocation[0] = x;
                    eyeLocation[2] = z;
                } else if (keyCode == KeyEvent.VK_RIGHT) {
                    float x = (float) ((Math.cos(-ROTATION_SIZE) * eyeLocation[0]) - (Math.sin(-ROTATION_SIZE) * eyeLocation[2]));
                    float z = (float) ((Math.sin(-ROTATION_SIZE) * eyeLocation[0]) + (Math.cos(-ROTATION_SIZE) * eyeLocation[2]));
                    eyeLocation[0] = x;
                    eyeLocation[2] = z;
                }
                GLU glu = new GLU();
                float widthHeightRatio = (float) glWindow.getWidth() / (float) glWindow.getHeight();
                glu.gluPerspective(45, widthHeightRatio, 1, 1000);
                glu.gluLookAt(eyeLocation[0], eyeLocation[1], eyeLocation[2], 0, 0, 0, 0, 1, 0);

                gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
            } finally {
                gl.getContext().release();
            }
        }
    }
}
