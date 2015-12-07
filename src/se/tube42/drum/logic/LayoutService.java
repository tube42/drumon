
package se.tube42.drum.logic;

import java.io.*;

import se.tube42.drum.data.*;
import static se.tube42.drum.data.Constants.*;
import static se.tube42.drum.data.World.*;

public class LayoutService
{
    // clear the lowest n bits of a number, used to minimize sub-pixel madness
    private static int rlsb(int v, int bits)
    {
        return v & ~((1 << bits) - 1);
    }
    
    private static void layout_pad(int x0, int y0, int w0, int h0)
    {
        // pad1
        size_pad1 = rlsb(Math.min(w0 / 4, h0 / 4), 3);
        stripe_pad1 = Math.min(w0 / 4, h0 / 4);
        w_pad1 = h_pad1 = size_pad1 * 4;
        x0_pad1 = x0 + (w0 - w_pad1 - size_pad1 + stripe_pad1) / 2;
        y0_pad1 = y0 + (h0 - h_pad1 - size_pad1 + stripe_pad1) / 2;        
        
        // pad2
        size_pad2 = rlsb(Math.min(w0 / 8, h0 / 8), 1);
        stripe_pad2_x = w0 / 8;
        stripe_pad2_y = h0 / 4;
        w_pad2 = stripe_pad2_x * 8;
        h_pad2 = stripe_pad2_y * 4;
        x0_pad2 = x0 + (w0 - w_pad2 - size_pad2 + stripe_pad2_x) / 2;
        y0_pad2 = y0 + (h0 - h_pad2 - size_pad2 + stripe_pad2_y) / 2;
    }
    
    private static void layout_tile(int x0, int y0, int w0, int h0)
    {
        size_tile = rlsb(Math.min(w0 / 4, h0 / 4), 3);
        stripe_tile = size_tile;
        w_tile = h_tile = size_tile * 4;
        x0_tile = x0 + (w0 - w_tile - size_tile + stripe_tile) / 2;
        y0_tile = y0 + (h0 - h_tile - size_tile + stripe_tile) / 2;
    }
    
    public static void resize(int w, int h)
    {
        ui_scale = Math.max(1, Math.min(World.sw / 200, World.sh / 300));
        ui_gap = Math.min(8, 4 * World.ui_scale);
        
        if(w  < h) {
            // portrait:
            // [ pads  ]
            // [ tiles ]            
            layout_pad(ui_gap, h / 2 + ui_gap, w - 2 * ui_gap, h / 2 - 2* ui_gap);
            layout_tile(ui_gap, ui_gap, w - 2 * ui_gap, h / 2 - 2* ui_gap);                      
        } else {
            // landscape:
            // [ pads  ] [tiles]
            layout_pad(ui_gap,  ui_gap, w / 2 - 2 * ui_gap, h - 2 * ui_gap);
            layout_tile(w / 2 + ui_gap, ui_gap, w / 2 - 2 * ui_gap, h - 2 * ui_gap);
        }
    }
}
