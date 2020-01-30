package com.example.myapplication;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

enum GeneralColors{
    RED, YELLOW, GREEN, BLUE, PURPLE
}

public class ColorDetectionPalette {
    private ArrayList<GeneralColors> colors = new ArrayList<GeneralColors>();

    public void setDetectColor(GeneralColors color){
        colors.add(color);
    }

    public boolean shouldDetectColor(GeneralColors color){
        if(colors.contains(color)){
            return true;
        }
        return false;
    }


    static public ColorDetectionPalette parse(String input){
        ColorDetectionPalette result = new ColorDetectionPalette();
        List<String> words = Arrays.asList(input.split(","));
        for (String word: words) {
                GeneralColors color = GeneralColors.valueOf(word);
                result.setDetectColor(color);
        }
        return result;
    }
}

