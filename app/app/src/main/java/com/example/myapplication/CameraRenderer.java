package com.example.myapplication;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Build;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CameraRenderer extends GLSurfaceView implements
        GLSurfaceView.Renderer,
        SurfaceTexture.OnFrameAvailableListener {
    private Context mContext;

    /**
     * Device camera and corresponding SurfaceTexture
     */
    private Camera mCamera;
    private SurfaceTexture mSurfaceTexture;
    private SeekBar zoomSeekBar;
    long startTime = System.currentTimeMillis();

    //Texture to feed into shader as camera image
    private final OESTexture mCameraTexture = new OESTexture();
    //Main shader
    private final Shader mOffscreenShader = new Shader();
    private int mWidth, mHeight;
    //Should the camera  preview texture be updated when openGl can *draw*
    private boolean updateTexture = false;

    public ColorDetectionPalette getPalette() {
        return palette;
    }

    public void setPalette(ColorDetectionPalette palette) {
        this.palette = palette;
    }

    private ColorDetectionPalette palette;

    /**
     * OpenGL params
     */
    private ByteBuffer mFullQuadVertices;
    private float[] mTransformM = new float[16];
    private float[] mOrientationM = new float[16];
    private float[] mRatio = new float[2];

    public CameraRenderer(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public CameraRenderer(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    @Override
    public void onPause() {
        super.onPause();
        onDestroy();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    private void init() {
        //Create full scene quad buffer
        final byte FULL_QUAD_COORDS[] = {-1, 1, -1, -1, 1, 1, 1, -1};
        mFullQuadVertices = ByteBuffer.allocateDirect(4 * 2);
        mFullQuadVertices.put(FULL_QUAD_COORDS).position(0);

        setPreserveEGLContextOnPause(true);
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);




    }

    @Override
    public synchronized void onFrameAvailable(SurfaceTexture surfaceTexture) {
        updateTexture = true;
        requestRender();
    }


    @Override
    public synchronized void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //load and compile shader

        try {
            mOffscreenShader.setProgram(R.raw.vshader, R.raw.fshader_varying, mContext);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @SuppressLint("NewApi")
    @Override
    public synchronized void onSurfaceChanged(GL10 gl, int width, int height) {
        mWidth = width;
        mHeight = height;

        //Init camera texture
        try {
            mCameraTexture.init(GLES20.GL_CLAMP_TO_EDGE, GLES20.GL_LINEAR);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //set up surfacetexture
        SurfaceTexture oldSurfaceTexture = mSurfaceTexture;
        mSurfaceTexture = new SurfaceTexture(mCameraTexture.getTextureId());
        mSurfaceTexture.setOnFrameAvailableListener(this);
        if (oldSurfaceTexture != null) {
            oldSurfaceTexture.release();
        }


        //set camera parameters
        int camera_width = 0;
        int camera_height = 0;

        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }

        //open camera and bind previewTexture
        mCamera = Camera.open();
        try {
            mCamera.setPreviewTexture(mSurfaceTexture);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }


        final Camera.Parameters param = mCamera.getParameters();
        List<Size> psize = param.getSupportedPreviewSizes();
        if (psize.size() > 0) {
            int i;
            for (i = 0; i < psize.size(); i++) {
                if (psize.get(i).width < width || psize.get(i).height < height)
                    break;
            }
            if (i > 0)
                i--;
            param.setPreviewSize(psize.get(i).width, psize.get(i).height);
            param.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

            camera_width = psize.get(i).width;
            camera_height = psize.get(i).height;

        }

        //get the camera orientation and display dimension
        if (mContext.getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_PORTRAIT) {
            Matrix.setRotateM(mOrientationM, 0, 90.0f, 0f, 0f, 1f);
            mRatio[1] = camera_width * 1.0f / height;
            mRatio[0] = camera_height * 1.0f / width;
        } else {
            Matrix.setRotateM(mOrientationM, 0, 0.0f, 0f, 0f, 1f);
            mRatio[1] = camera_height * 1.0f / height;
            mRatio[0] = camera_width * 1.0f / width;
        }

        //start camera
        mCamera.setParameters(param);
        mCamera.startPreview();

        zoomSeekBar= ((View)getParent()).findViewById(R.id.zoomSeekBar);
        zoomSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Camera.Parameters params = mCamera.getParameters();
                if(params.isZoomSupported()) {
                    float zoomAMount = (float) zoomSeekBar.getProgress() / (float) zoomSeekBar.getMax() * (float) mCamera.getParameters().getMaxZoom();

                    params.setZoom((int) zoomAMount);
                }
                else
                    Toast.makeText(mContext, "Zoom Not Avaliable", Toast.LENGTH_LONG).show();
                mCamera.setParameters(params);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        //start rendering
        requestRender();
    }

    @Override
    public synchronized void onDrawFrame(GL10 gl) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glEnable(GLES20.GL_TEXTURE);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);


        //render the texture to FBO if new frame is available
        if (updateTexture) {
            mSurfaceTexture.updateTexImage();
            mSurfaceTexture.getTransformMatrix(mTransformM);

            updateTexture = false;

            GLES20.glViewport(0, 0, mWidth, mHeight);

            mOffscreenShader.useProgram();

            float time = (float) Math.abs(System.currentTimeMillis()%1000 - startTime%1000);

            int uTransformM = mOffscreenShader.getHandle("uTransformM");
            int uOrientationM = mOffscreenShader.getHandle("uOrientationM");
            int uRatioV = mOffscreenShader.getHandle("ratios");
            int uTimeF = mOffscreenShader.getHandle("uTime");

            //Color specification uniforms (insert 0 to ignore, non-zero to detect the color)
            //GLES20 can't use bools, so that's the way it has to be i guess
            int uDetectRed = mOffscreenShader.getHandle("uDetectRed");
            int uDetectYellow = mOffscreenShader.getHandle("uDetectYellow");
            int uDetectGreen = mOffscreenShader.getHandle("uDetectGreen");
            int uDetectBlue = mOffscreenShader.getHandle("uDetectBlue");
            int uDetectPurple = mOffscreenShader.getHandle("uDetectPurple");

            //Passing in color parameters from preferences
            if (Preferences.appPreferences.colorPalette.shouldDetectColor(GeneralColors.RED))
                GLES20.glUniform1i(uDetectRed, 1);
            if (Preferences.appPreferences.colorPalette.shouldDetectColor(GeneralColors.YELLOW))
                GLES20.glUniform1i(uDetectYellow, 1);
            if (Preferences.appPreferences.colorPalette.shouldDetectColor(GeneralColors.GREEN))
                GLES20.glUniform1i(uDetectGreen, 1);
            if (Preferences.appPreferences.colorPalette.shouldDetectColor(GeneralColors.BLUE))
                GLES20.glUniform1i(uDetectBlue, 1);
            if (Preferences.appPreferences.colorPalette.shouldDetectColor(GeneralColors.PURPLE))
                GLES20.glUniform1i(uDetectPurple, 1);

            //Vertex support uniforms - transformation and orientation matrices
            GLES20.glUniformMatrix4fv(uTransformM, 1, false, mTransformM, 0);
            GLES20.glUniformMatrix4fv(uOrientationM, 1, false, mOrientationM, 0);

            //Screen ratio vector
            GLES20.glUniform2fv(uRatioV, 1, mRatio, 0);
            //Time uniform
            GLES20.glUniform1f(uTimeF, time);


            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mCameraTexture.getTextureId());

            renderQuad(mOffscreenShader.getHandle("aPosition"));


        }

    }

    private void renderQuad(int aPosition) {
        GLES20.glVertexAttribPointer(aPosition, 2, GLES20.GL_BYTE, false, 0, mFullQuadVertices);
        GLES20.glEnableVertexAttribArray(aPosition);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void onDestroy() {
        try {
            updateTexture = false;
            mSurfaceTexture.release();
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.setPreviewCallback(null);
                mCamera.release();
            }

            mCamera = null;
        } catch (Exception E) {
            E.printStackTrace();
        }
    }

}
