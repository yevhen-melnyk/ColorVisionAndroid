package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatCallback;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Preferences.setSharedPreferences(getPreferences(MODE_PRIVATE));
        if(Preferences.appPreferences==null)
        Preferences.Load();
    }


    public void gotoSettings(View view) {
        Intent intent = new Intent (this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * If camera permission is granted, opens vision activity which displays augmented camera preview.
     * Tries to get permission if it's not granted yet, triggers {@link com.example.myapplication.MainActivity#onRequestPermissionsResult(int, String[], int[])}
     * to open vision activity after permission is granted.
     * {@link com.example.myapplication.MainActivity#tryGotoVision} calls this but asks for permission before doing so.
     */
    public void tryGotoVision(View view) {

        // If not permitted to use camera...
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED)
        {
                // ... ask for it.
                // This would trigger
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        1);

        }
        else {
            gotoVision();
        }
    }


    /**
     * Actually opening vision. Only works if camera permission is granted.
     * {@link com.example.myapplication.MainActivity#tryGotoVision} calls this but asks for permission before doing so.
     */
    private void gotoVision(){
        Intent intent = new Intent(this, VisionActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(Manifest.permission.CAMERA)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        gotoVision();
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                R.string.noPermissionToast, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }


    }

    }
