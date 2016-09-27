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
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.math.BigInteger;
import java.util.Collections;
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

    private static final int FONT_SIZE = 18;
    private static final int HALF_FONT_HEIGHT = FONT_SIZE / 2;

    private static final float[] ORIGIN = { 0.0f, 0.0f, 0.0f };
    private static final float STEP_SIZE = 10.0f;
    private static final float ROTATION_SIZE = (float) (Math.PI / 180.0f);

    private final Set<TerminationListener> listeners = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private GLWindow glWindow;
    private Animator animator;
    private TextRenderer textRenderer;
    private CollatzPositioner positioner;
    private float[] eyeLocation = { 0, 0, 500 };
    private float[] digitWidths = new float[10];

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

    void render(@SuppressWarnings("unused") GLAutoDrawable drawable, GL2 gl, Pair<CollatzValue, CollatzValue> entry) {

        CollatzValue to = entry.getValue();
        if (to != null) {
            CollatzValue from = entry.getKey();
            float[] fromLocation = from.getLocation();
            float[] toLocation = to.getLocation();

            try {
                BigInteger fromValue = from.getValue();
                BigInteger toValue = to.getValue();

                String fromNum = fromValue.toString();
                String toNum = toValue.toString();

                if (from.equals(to.getOddValueNode()) || to.equals(from.getOddValueNode())) {
                    gl.glColor4f(0.8f, 0.8f, 0.0f, 1.0f);
                } else {
                    gl.glColor4f(0.8f, 0.8f, 0.8f, 1.0f);
                }
                gl.glLineWidth(2.0f);

                gl.glBegin(GL2.GL_LINES);
                gl.glVertex3f(fromLocation[0] + (width(fromNum) / 2.0f), fromLocation[1] + HALF_FONT_HEIGHT, fromLocation[2]);
                gl.glVertex3f(toLocation[0] + (width(toNum) / 2.0f), toLocation[1] + HALF_FONT_HEIGHT, toLocation[2]);
                gl.glEnd();

                textRenderer.begin3DRendering();
                textRenderer.setColor(1.0f, 0.2f, 0.2f, 1.0f);

                textRenderer.draw3D(fromNum, fromLocation[0], fromLocation[1], fromLocation[2], 1.0f);
                textRenderer.draw3D(toNum, toLocation[0], toLocation[1], toLocation[2], 1.0f);
            } finally {
                textRenderer.end3DRendering();
            }
        }
    }

    private float width(String num) {
        float width = 0.0f;
        int len = num.length();
        for (int i = 0; i < len; i++) {
            width += digitWidths[(char) (num.charAt(i) - '0')];
        }

        return width;
    }

    class CDEvents implements GLEventListener {

        private GLU glu;

        @Override
        public void display(GLAutoDrawable drawable) {
            GL2 gl = drawable.getGL().getGL2();

            gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
            gl.glEnable(GL.GL_BLEND);

            for (Pair<CollatzValue, CollatzValue> entry : data) {
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

            gl.glDisable(GLLightingFunc.GL_LIGHTING);

            Font f = new Font("SansSerif", Font.BOLD, FONT_SIZE);
            textRenderer = new TextRenderer(f);

            FontRenderContext frc = new FontRenderContext(null, false, false);

            char[] digit = new char[1];
            for (int i = 0; i < 10; i++) {
                digit[0] = (char) ('0' + i);

                Rectangle2D r = f.getStringBounds(digit, 0, 1, frc);
                digitWidths[i] = (float) r.getWidth();
            }
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

            try (ContextController cc = new ContextController(gl)) {

                gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
                gl.glLoadIdentity();

                int keyCode = e.getKeyCode();
                if (keyCode == KeyEvent.VK_UP) {
                    float[] uv = CollatzPositioner.unitVector(ORIGIN, eyeLocation);
                    for (int i = 0; i < 3; ++i) {
                        uv[i] *= STEP_SIZE;
                        eyeLocation[i] -= uv[i];
                    }
                } else if (keyCode == KeyEvent.VK_DOWN) {
                    float[] uv = CollatzPositioner.unitVector(ORIGIN, eyeLocation);
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
            }
        }
    }
}
