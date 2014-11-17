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
    public static int dpi, dpi_class, dpi_scale;
    public static int sw, sh, s_scale, s_scale_bin;

    // -----------------------------------------------------

    public static void init()
    {
        dpi = (int)(160 * Gdx.graphics.getDensity());
        dpi_scale = (int)Math.min(4, Math.max(1, dpi / 240));
        
        dpi_class = DPU_TO_CLASS[DPU_TO_CLASS.length-1];
        for(int i = 0; i < DPU_TO_CLASS.length; i++)
            if( dpi_class < DPU_TO_CLASS[i] * 1.2f)
                dpi_class = i;
        
        System.out.println("DPI: " + dpi + "/" + dpi_class + " DPI_SCALE=" + dpi_scale);

    }    
}
