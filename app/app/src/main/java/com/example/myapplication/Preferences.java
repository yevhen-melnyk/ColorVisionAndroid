package com.example.myapplication;

import android.content.SharedPreferences;

import com.google.gson.Gson;

public class Preferences {
    public CbType selectedCbType;
    public ColorDetectionPalette colorPalette;
    static Preferences appPreferences;

    public int primaryColorTexture;
    public int secondaryColorTexture;
    public int tetriaryColorTexture;

    public Preferences() {
        initDefaultTextures();
    }


    public CbType getSelectedCbType() {
        return selectedCbType;
    }

    public void initDefaultTextures(){
        primaryColorTexture = R.drawable.circle_texture;
        secondaryColorTexture = R.drawable.cross_texture;
        tetriaryColorTexture = R.drawable.diamond_texture;
    }

    public void setSelectedCbType(CbType selectedCbType) {

        this.selectedCbType = selectedCbType;
        colorPalette = ColorDetectionPalette.parse(selectedCbType.colors);

    }


    public static void setSharedPreferences(SharedPreferences sharedPreferences) {
        Preferences.sharedPreferences = sharedPreferences;
    }

    static SharedPreferences sharedPreferences;


    static void Save() {

        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(appPreferences);
        prefsEditor.putString("Preferences", json);
        prefsEditor.commit();
    }

    static void Load() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString("Preferences", "");
        Preferences loadedPreferences = gson.fromJson(json, Preferences.class);
        if (loadedPreferences != null)
            appPreferences = loadedPreferences;
        else
            appPreferences = new Preferences();
    }
}
