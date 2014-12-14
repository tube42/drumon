package se.tube42.lib.util;

import com.badlogic.gdx.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;

/**
 * UI constants
 */
public class UIC
{
    // DPI and stuff
    public static final int [] DPU_TO_CLASS = {
        80, 160, 240, 320, 480, 640
    };
    public static final int
          CLASS_LDPI = 0,
          CLASS_MDPI = 1,
          CLASS_HDPI = 2,
          CLASS_XHDPI = 3,
          CLASS_XXHDPI = 4,
          CLASS_XXXXHDPI = 5
          ;

    // general stuff
    public static int wanted_w, wanted_h;
    public static int dpi = -1, dpi_class, dpi_scale;
    public static int sw, sh, s_scale, s_scale_bin;
    public static float halfpixel;

    // -----------------------------------------------------

    public static void init(int wanted_w, int wanted_h)
    {
        UIC.wanted_w = wanted_w;
        UIC.wanted_h = wanted_h;

    }

    public static void resize(int w, int h)
    {
        if(dpi == -1) {
            dpi = (int)(160 * Gdx.graphics.getDensity());
            dpi_scale = (int)Math.min(4, Math.max(1, dpi / 240));

            dpi_class = DPU_TO_CLASS[DPU_TO_CLASS.length-1];
            for(int i = 0; i < DPU_TO_CLASS.length; i++)
                if( dpi_class < DPU_TO_CLASS[i] * 1.2f)
                    dpi_class = i;
                System.out.println("DPI: " + dpi + "/" + dpi_class + " DPI_SCALE=" + dpi_scale);
        }

        if(wanted_w > 2 && wanted_h > 1) {
            s_scale = Math.max(1, Math.min(w / wanted_w, h / wanted_h));
            s_scale_bin = Math.min(4, s_scale);
            if(s_scale_bin == 3)
                s_scale_bin = 2;

        } else {
            s_scale = 1;
        }


        w /= s_scale;
        h /= s_scale;

        UIC.sw = w;
        UIC.sh = h;
        halfpixel = 1f / s_scale;

        System.out.println("resize (" + w + ", " + h + ") => (" + sw + ", " + sh + " ) * " + s_scale
                  + " halfpixel=" + halfpixel);

    }
}
