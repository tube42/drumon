package se.tube42.lib.util;

import com.badlogic.gdx.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;

/**
 * UI constants
 */
public class UIC
{
    // general stuff
    public static int sw, sh;
    public static float halfpixel;

    // -----------------------------------------------------

    public static void resize(int w, int h)
    {
        UIC.sw = w;
        UIC.sh = h;
        UIC.halfpixel = 0.5f;
        System.out.println("UIC resize: " + sw + "x" + sh + " " + halfpixel);
    }
}
