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


public class MyGLSurfaceView extends GLSurfaceView {
    static Context mcontext;
    static private boolean isinit = false;
    SHOpenGlFramework mSHOpenGlFramework;

    public MyGLSurfaceView(Context context) {
        this(context, null);
        mcontext = context;
    }

    public MyGLSurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mcontext = context;
    }

    public void init(MediaPlayer Player, MediaPlayer backPlayer) {
        setEGLContextClientVersion(2);
        mSHOpenGlFramework = new SHOpenGlFramework(Player, backPlayer);
        setRenderer(mSHOpenGlFramework);
        isinit = true;
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void setrect(float x, float y, int w, int h) {
        mSHOpenGlFramework.mSHViceVideo.setrect(x, y, w, h);
    }

    public void playback() {
        mSHOpenGlFramework.mbackMediaPlayer.play();
        //new LooperThread().start();
    }

    public void HiddenDisplayVice(boolean show) {
        mSHOpenGlFramework.HiddenDisplayVice = show;
    }

    public void Setscreen(int w, int h) {
        if ((w == 1920) && (h == 1080)) {
            mSHOpenGlFramework.mSHMainVideo.reset2();
        }
    }

    class LooperThread extends Thread {
        public void run() {
            if (mSHOpenGlFramework.mbackMediaPlayer.isPlaying()) {
                mSHOpenGlFramework.mbackMediaPlayer.setTime(0);
            }
            mSHOpenGlFramework.mbackMediaPlayer.play();
            /*mSHOpenGlFramework.updatebackSurface=true;
    		while(true){
    			if(mSHOpenGlFramework.mbackMediaPlayer.isPlaying()){
    				break;
    			}
    			try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		};
    		try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		mSHOpenGlFramework.updatebackSurface=false;

    		mSHOpenGlFramework.mbackMediaPlayer.stop();
        */
        }
    }
}

class SHMainVideo {
    private static final int GL_TEXTURE_EXTERNAL_OES = 0x8D65;
    private static final int VERTICES_DATA_POS_OFFSET = 0;
    static public float[] mMVPMatrix = new float[16];
    static public float[] mProjMatrix = new float[16];
    static public int mTextureID;
    static public int textureId;
    public static int mProgram;
    static private int muMVPMatrixHandle;
    static private int muSTMatrixHandle;
    static private int maPositionHandle;
    static private int maTextureHandle;
    static private FloatBuffer mVertices;
    public float[] mMMatrix = new float[16];
    public float[] mVMatrix = new float[16];
    public float[] mSTMatrix = new float[16];
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
        for (int i = 0; i < mMVPMatrix.length; i++) {
            mMVPMatrix[i] = 0;
        }
        for (int i = 0; i < mProjMatrix.length; i++) {
            mProjMatrix[i] = 0;
        }
        for (int i = 0; i < mMMatrix.length; i++) {
            mMMatrix[i] = 0;
        }
        for (int i = 0; i < mVMatrix.length; i++) {
            mVMatrix[i] = 0;
        }
        for (int i = 0; i < mSTMatrix.length; i++) {
            mSTMatrix[i] = 0;
        }
        //mMVPMatrix=mVerticesData;
        //mProjMatrix=mVerticesData;
        mVertices = ByteBuffer.allocateDirect(mVerticesData.length
                * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertices.put(mVerticesData).position(0);
        reset();
    }

    public void draw() {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureID);
        mVertices.position(VERTICES_DATA_POS_OFFSET);
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

    }

    public void reset() {
        Matrix.setIdentityM(mSTMatrix, 0);
        Matrix.setIdentityM(mMMatrix, 0);
        Matrix.translateM(mMMatrix, 0, 2.9f, -1.5f, 0);
        Matrix.scaleM(mMMatrix, 0, 2.73f, 2.9f, 0);
        Matrix.setLookAtM(mVMatrix, 0,
                0f, 0, 6f,
                0f, 0f, 0f,
                0f, 0.1f, 0.0f);
    }

    public void reset2() {
        Matrix.setIdentityM(mSTMatrix, 0);
        Matrix.setIdentityM(mMMatrix, 0);
        Matrix.translateM(mMMatrix, 0, 0.166f, -1.5f, 0);
        Matrix.scaleM(mMMatrix, 0, 2.73f, 2.9f, 0);
        Matrix.setLookAtM(mVMatrix, 0,
                0f, 0, 6f,
                0f, 0f, 0f,
                0f, 0.1f, 0.0f);
    }

    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        float mRatio = 1.0f;
        mRatio = (float) width / height;
        Matrix.frustumM(mProjMatrix, 0, -1, mRatio, -mRatio, 1, 5, 6);
    }

    public void onSurfaceCreated(GL10 glUnused, EGLConfig config, String mVertexShader, String mFragmentShader) {
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        SHMainVideo.mProgram = createProgram(mVertexShader, mFragmentShader);
        if (SHMainVideo.mProgram == 0) {
            return;
        }
        maPositionHandle = GLES20.glGetAttribLocation(SHMainVideo.mProgram, "aPosition");
        if (maPositionHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aPosition");
        }
        maTextureHandle = GLES20.glGetAttribLocation(SHMainVideo.mProgram, "aTextureCoord");
        if (maTextureHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aTextureCoord");
        }
        muMVPMatrixHandle = GLES20.glGetUniformLocation(SHMainVideo.mProgram, "uMVPMatrix");
        if (muMVPMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uMVPMatrix");
        }
        muSTMatrixHandle = GLES20.glGetUniformLocation(SHMainVideo.mProgram, "uSTMatrix");
        if (muMVPMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uSTMatrix");
        }
        if (muMVPMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uCRatio");
        }
        int[] textures = new int[2];
        GLES20.glGenTextures(2, textures, 0);
        mTextureID = textures[0];
        textureId = textures[1];
        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);
        //textureId=loadTexture();
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

class SHBackGroudPro {
    private static final int GL_TEXTURE_EXTERNAL_OES = 0x8D65;
    private static final int VERTICES_DATA_POS_OFFSET = 0;
    static public float[] mMVPMatrix = new float[16];
    static public float[] mProjMatrix = new float[16];
    static public int mTextureID;
    public static int mProgram;
    static private int muMVPMatrixHandle;
    static private int muSTMatrixHandle;
    static private int maPositionHandle;
    static private int maTextureHandle;
    static private FloatBuffer mVertices;
    public float[] mMMatrix = new float[16];
    public float[] mVMatrix = new float[16];
    public float[] mSTMatrix = new float[16];
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
        for (int i = 0; i < mMVPMatrix.length; i++) {
            mMVPMatrix[i] = 0;
        }
        for (int i = 0; i < mProjMatrix.length; i++) {
            mProjMatrix[i] = 0;
        }
        for (int i = 0; i < mMMatrix.length; i++) {
            mMMatrix[i] = 0;
        }
        for (int i = 0; i < mVMatrix.length; i++) {
            mVMatrix[i] = 0;
        }
        for (int i = 0; i < mSTMatrix.length; i++) {
            mSTMatrix[i] = 0;
        }
        mVertices = ByteBuffer.allocateDirect(mVerticesData.length
                * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertices.put(mVerticesData).position(0);
        reset();
    }

    public void draw() {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureID);
        mVertices.position(VERTICES_DATA_POS_OFFSET);
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

    }

    public void reset() {
        Matrix.setIdentityM(mSTMatrix, 0);
        Matrix.setIdentityM(mMMatrix, 0);
        Matrix.translateM(mMMatrix, 0, -0.4f, -0.475f, 0);
        Matrix.scaleM(mMMatrix, 0, 1.75f, 1.675f, 0);
        Matrix.setLookAtM(mVMatrix, 0,
                0f, 0, 6f,
                0f, 0f, 0f,
                0f, 0.1f, 0.0f);
    }

    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        float mRatio = 1.0f;
        mRatio = (float) width / height;
        Matrix.frustumM(mProjMatrix, 0, -1, mRatio, -mRatio, 1, 5, 6);
    }

    public void onSurfaceCreated(GL10 glUnused, EGLConfig config, String mVertexShader, String mFragmentShader) {
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        SHMainVideo.mProgram = createProgram(mVertexShader, mFragmentShader);
        if (SHMainVideo.mProgram == 0) {
            return;
        }
        maPositionHandle = GLES20.glGetAttribLocation(SHMainVideo.mProgram, "aPosition");
        if (maPositionHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aPosition");
        }
        maTextureHandle = GLES20.glGetAttribLocation(SHMainVideo.mProgram, "aTextureCoord");
        if (maTextureHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aTextureCoord");
        }
        muMVPMatrixHandle = GLES20.glGetUniformLocation(SHMainVideo.mProgram, "uMVPMatrix");
        if (muMVPMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uMVPMatrix");
        }
        muSTMatrixHandle = GLES20.glGetUniformLocation(SHMainVideo.mProgram, "uSTMatrix");
        if (muMVPMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uSTMatrix");
        }
        if (muMVPMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uCRatio");
        }
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        mTextureID = textures[0];
        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);
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

class SHViceVideo extends SHMainVideo {
    float x, y;
    int w, h;

    public void reset() {
        Matrix.setIdentityM(mSTMatrix, 0);
        Matrix.setIdentityM(mMMatrix, 0);

        Matrix.translateM(mMMatrix, 0, 0f, 0f, 0);
        Matrix.scaleM(mMMatrix, 0, 1f, 1f, 0);
        Matrix.setLookAtM(mVMatrix, 0,
                0f, 0, 6f,
                0f, 0f, 0f,
                0f, 0.1f, 0.0f);
        Matrix.rotateM(mMMatrix, 0, 180f, 0, 0f, 1f);
        Matrix.rotateM(mMMatrix, 0, 180f, 0, 1f, 0f);
    }

    public void setrect(float x, float y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        Matrix.setIdentityM(mMMatrix, 0);

        Matrix.translateM(mMMatrix, 0, 0f, 0f, 0);
        Matrix.scaleM(mMMatrix, 0, 1f, 1f, 0);
        Matrix.setLookAtM(mVMatrix, 0,
                0f, 0, 6f,
                0f, 0f, 0f,
                0f, 0.1f, 0.0f);
        Matrix.rotateM(mMMatrix, 0, 180f, 0, 0f, 1f);
        Matrix.rotateM(mMMatrix, 0, 180f, 0, 1f, 0f);
        //0.17f-(2.75f/2.0f)+(w/1920.0f)
        //Matrix.translateM(mMMatrix,0,0.17f-(2.725f/2.0f)+(w/1920.0f)+(x/1920.0f*2.725f),0, 0);
        //Matrix.translateM(mMMatrix,0,0,1.5f-(2.9f/2.0f)+(h/1080.0f)+(y/1080.0f*2.9f), 0);
        Matrix.translateM(mMMatrix, 0, 0.1675f + x, 0, 0);
        Matrix.translateM(mMMatrix, 0, 0, 1.35f + y, 0);
        //2.75 2.9
        Matrix.scaleM(mMMatrix, 0, 2.725f * (w / 1920.0f), 2.9f * (h / 1080.0f), 0);
    }
}

class SHOpenGlFramework implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {
    private static final String TAG = "MyRenderer";
    private final String mVertexShader =
            "uniform mat4 uMVPMatrix;\n" +
                    "uniform mat4 uSTMatrix;\n" +
                    "attribute vec4 aPosition;\n" +
                    "attribute vec4 aTextureCoord;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "void main() {\n" +
                    "  gl_Position = uMVPMatrix * aPosition;\n" +
                    "  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n" +
                    "}\n";
    private final String mFragmentShader =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform samplerExternalOES sTexture;\n" +
                    "void main() {\n" +
                    "  gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
                    "}\n";
    public MediaPlayer mbackMediaPlayer;
    public SHMainVideo mSHMainVideo;
    public SHViceVideo mSHViceVideo;
    public boolean HiddenDisplayVice = false;
    public boolean updatebackSurface = false;
    private MediaPlayer mMediaPlayer;
    private SHBackGroudPro mbackSHMainVideo;
    private SurfaceTexture mSurface;
    private SurfaceTexture mSurface2;
    private boolean updateSurface = false;

    public SHOpenGlFramework(MediaPlayer Player, MediaPlayer backPlayer) {
        mMediaPlayer = Player;
        mbackMediaPlayer = backPlayer;
        mSHMainVideo = new SHMainVideo();
        mSHMainVideo.SHMainVideoInit();
        mbackSHMainVideo = new SHBackGroudPro();
        mbackSHMainVideo.SHMainVideoInit();
        mSHViceVideo = new SHViceVideo();
        mSHViceVideo.reset();
    }

    public SHOpenGlFramework() {
        mSHViceVideo.reset();
    }

    public void onDrawFrame(GL10 glUnused) {
        synchronized (this) {
            if (updateSurface) {
                mSurface.updateTexImage();
                mSurface.getTransformMatrix(mSHMainVideo.mSTMatrix);
                updateSurface = false;
            }
            if (updatebackSurface) {
                mSurface2.updateTexImage();
                mSurface2.getTransformMatrix(mbackSHMainVideo.mSTMatrix);
            }
        }
        //GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glUseProgram(SHMainVideo.mProgram);
        mbackSHMainVideo.draw();
        mSHMainVideo.draw();
        if (HiddenDisplayVice)
            mSHViceVideo.draw();
    }

    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        mSHMainVideo.onSurfaceChanged(glUnused, width, height);
        mbackSHMainVideo.onSurfaceChanged(glUnused, 1920, 1080);
    }

    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        mSHMainVideo.onSurfaceCreated(glUnused, config, mVertexShader, mFragmentShader);
        mbackSHMainVideo.onSurfaceCreated(glUnused, config, mVertexShader, mFragmentShader);
        mSurface = new SurfaceTexture(SHMainVideo.mTextureID);
        mSurface.setOnFrameAvailableListener(this);
        mSurface2 = new SurfaceTexture(mbackSHMainVideo.mTextureID);
        //mSurface2.setOnFrameAvailableListener(this);
        synchronized (this) {
            updateSurface = false;
        }
        IVLCVout vlcVout = mMediaPlayer.getVLCVout();
        vlcVout.setVideoSurface(mSurface);
        vlcVout.attachViews();

        IVLCVout vlcVout2 = mbackMediaPlayer.getVLCVout();
        vlcVout2.setVideoSurface(mSurface2);
        vlcVout2.attachViews();
    }

    synchronized public void onFrameAvailable(SurfaceTexture surface) {
        updateSurface = true;
        //updatebackSurface = true;
    }

}
