package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLUtils;

/**
 * This class defines the OES Texture that can be attached to SurfaceTexture
 * which is updated to the most recent camera frame image when requested.
 * @author yulu
 *
 */
public class OESTexture {
	private int mTextureHandle;

	public OESTexture() {
		// TODO Auto-generated constructor stub
		
	}
	
	public int getTextureId(){
		return mTextureHandle;
	}


	public void init(int WrapParameter, int FilterParameter) throws Exception {
		int[] mTextureHandles = new int[1];
		GLES20.glGenTextures(1, mTextureHandles, 0);
		mTextureHandle = mTextureHandles[0];

		/*if(mTextureHandle==0){
			int error = GLES20.glGetError();
			throw new Exception("GLES20 error "+String.valueOf(error));
		}
*/
		GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureHandles[0]);
		GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, WrapParameter);
		GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, WrapParameter);
		GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, FilterParameter);
		GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, FilterParameter);

	}

	public int loadFromResource(int resourceId, Context context) throws Exception {
		final int[] textureHandle = new int[1];

		GLES20.glGenTextures(1, textureHandle, 0);

		if (textureHandle[0] != 0)
		{
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inScaled = false;   // No pre-scaling

			// Read in the resource
			final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

			// Bind to the texture in OpenGL
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

			// Set filtering
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

			// Load the bitmap into the bound texture.
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

			// Recycle the bitmap, since its data has been loaded into OpenGL.
			bitmap.recycle();
		}

		if (textureHandle[0] == 0)
		{
			throw new RuntimeException("Error loading texture.");
		}

		return textureHandle[0];
    }

}