package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceGroup;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class VisionActivity extends Activity {
	private CameraRenderer mRenderer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);



		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_vision);


		mRenderer = (CameraRenderer)findViewById(R.id.renderer_view);
		//Set colors to be detected from preferences
		mRenderer.setPalette(Preferences.appPreferences.colorPalette);


	}

	@Override
	public void onStart(){
		super.onStart();

	}
	
	
	@Override
	public void onPause(){
		super.onPause();
		mRenderer.onDestroy();
		
	}
	
	@Override
	public void onResume(){
		super.onResume();
		mRenderer.onResume();
	}

}
