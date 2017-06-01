/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.shine.vlcmultiscreen.libvlcandroidsample;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.MediaPlayer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class StapleGLSurfaceView extends GLSurfaceView {
    SHOpenGlRenderer mSHOpenGlFramework = null;

    public StapleGLSurfaceView(Context context) {
        this(context, null);
    }

    public StapleGLSurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void init(MediaPlayer Player, boolean backPlayer) {
        setEGLContextClientVersion(2);
        mSHOpenGlFramework = new SHOpenGlRenderer(Player);
        setRenderer(mSHOpenGlFramework);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}

class StapleSHMainVideo {
    static public int mTextureID;
    private final int GL_TEXTURE_EXTERNAL_OES = 0x8D65;
    public float[] mMVPMatrix = {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};//new float[16];
    public float[] mProjMatrix = {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};//new float[16];
    public float[] mMMatrix = {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};//new float[16];
    public float[] mVMatrix = {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};//new float[16];
    public float[] mSTMatrix = {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};//new float[16];
    public int mProgram;
    private int muMVPMatrixHandle;
    private int muSTMatrixHandle;
    private int maPositionHandle;
    private int maTextureHandle;
    private FloatBuffer mVertices;
    private int VERTICES_DATA_UV_OFFSET = 3;
    private int FLOAT_SIZE_BYTES = 4;
    private int VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;
    private float[] mVerticesData = {
            -0.5f, -1.0f, 0, 0.f, 0.f,
            0.5f, -1.0f, 0, 1.f, 0.f,
            -0.5f, 1.0f, 0, 0.f, 1.0f,
            0.5f, 1.0f, 0, 1.f, 1.0f,
    };

    public void SHMainVideoInit() {
        mVertices = ByteBuffer.allocateDirect(mVerticesData.length
                * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertices.put(mVerticesData).position(0);
        reset();
    }

    public void draw() {
        GLES20.glUseProgram(mProgram);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureID);
        mVertices.position(0);
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false,
                VERTICES_DATA_STRIDE_BYTES, mVertices);
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        mVertices.position(VERTICES_DATA_UV_OFFSET);
        GLES20.glVertexAttribPointer(maTextureHandle, 3, GLES20.GL_FLOAT, false,
                VERTICES_DATA_STRIDE_BYTES, mVertices);
        GLES20.glEnableVertexAttribArray(maTextureHandle);
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mMMatrix, 0);

        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(muSTMatrixHandle, 1, false, mSTMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glFinish();
    }

    public void reset() {
        Matrix.setIdentityM(mSTMatrix, 0);
        Matrix.setIdentityM(mMMatrix, 0);
        Matrix.translateM(mMMatrix, 0, -0.1f, -0.0f, 0);
        Matrix.scaleM(mMMatrix, 0, 1.3f, 0.35f, 0);
        // Matrix.rotateM(mMMatrix,  0, 45.0f, 0.0f, 1.0f, 0);
        Matrix.rotateM(mMMatrix, 0, 45.0f, 0.1f, 0.0f, 0);
       /* Matrix.setLookAtM(mVMatrix, 0,
                0f, 0, 6f,
        		0f, 0f, 0f,
        		0f, 0.1f, 0.0f);*/
        Matrix.setLookAtM(mVMatrix, 0,
                0f, 0, 6f,
                0f, 0f, 0f,
                0.0f, 0.1f, 0.0f);
    }

    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        float mRatio = 1.0f;
        mRatio = (float) width / height;
        Matrix.frustumM(mProjMatrix, 0, -1, mRatio, -mRatio, 1, 5, 6);
    }

    synchronized public void onSurfaceCreated(GL10 glUnused, EGLConfig config, String mVertexShader, String mFragmentShader) {
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        mProgram = createProgram(mVertexShader, mFragmentShader);
        if (mProgram == 0) {

            return;
        }
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        if (maPositionHandle == -1) {
            System.exit(0);
            throw new RuntimeException("Could not get attrib location for aPosition");
        }
        maTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");
        if (maTextureHandle == -1) {
            System.exit(0);
            throw new RuntimeException("Could not get attrib location for aTextureCoord");
        }
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        if (muMVPMatrixHandle == -1) {
            System.exit(0);
            throw new RuntimeException("Could not get attrib location for uMVPMatrix");
        }
        muSTMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uSTMatrix");
        if (muSTMatrixHandle == -1) {
            System.exit(0);
            throw new RuntimeException("Could not get attrib location for uSTMatrix");
        }
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        mTextureID = textures[0];
        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
    }

    private int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    private int createProgram(String vertexSource, String fragmentSource) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }
        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0) {
            return 0;
        }
        int program = GLES20.glCreateProgram();
        if (program != 0) {
            GLES20.glAttachShader(program, vertexShader);
            GLES20.glAttachShader(program, pixelShader);
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }
}

class SHOpenGlRenderer implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {
    static private final String mVertexShader =
            "uniform mat4 uMVPMatrix;\n" +
                    "uniform mat4 uSTMatrix;\n" +
                    "attribute vec4 aPosition;\n" +
                    "attribute vec4 aTextureCoord;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "void main() {\n" +
                    "  gl_Position = uMVPMatrix * aPosition;\n" +
                    "  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n" +
                    "}\n";
    static private final String mFragmentShader =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform samplerExternalOES sTexture;\n" +
                    "void main() {\n" +
                    "  gl_FragColor = texture2D(sTexture,vTextureCoord);\n" +
                    "}\n";
    public StapleSHMainVideo mSHMainVideo;
    boolean updateSurface = false;
    private MediaPlayer mMediaPlayer = null;
    private SurfaceTexture mSurface;

    public SHOpenGlRenderer(MediaPlayer player) {
        mMediaPlayer = player;
        mSHMainVideo = new StapleSHMainVideo();
        mSHMainVideo.SHMainVideoInit();
    }

    public void onDrawFrame(GL10 glUnused) {
        synchronized (this) {
            if (updateSurface) {
                mSurface.updateTexImage();
                mSurface.getTransformMatrix(mSHMainVideo.mSTMatrix);
                updateSurface = false;
            }
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            mSHMainVideo.draw();
        }
    }

    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        mSHMainVideo.onSurfaceChanged(glUnused, width, height);
    }

    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        mSHMainVideo.onSurfaceCreated(glUnused, config, mVertexShader, mFragmentShader);
        mSurface = new SurfaceTexture(StapleSHMainVideo.mTextureID);
        if (mSurface == null)
            System.exit(0);
        mSurface.setDefaultBufferSize(720, 480);
        //        mSurface.setDefaultBufferSize(480,720);
        mSurface.setOnFrameAvailableListener(this);
        synchronized (this) {
            IVLCVout vlcVout = mMediaPlayer.getVLCVout();
            vlcVout.setVideoSurface(mSurface);
            vlcVout.attachViews();
        }
    }

    synchronized public void onFrameAvailable(SurfaceTexture surface) {
        updateSurface = true;
    }

}
