
package se.tube42.drum.logic;

import static se.tube42.drum.data.World.h_pad1;
import static se.tube42.drum.data.World.h_pad2;
import static se.tube42.drum.data.World.h_tile;
import static se.tube42.drum.data.World.size_pad1;
import static se.tube42.drum.data.World.size_pad2;
import static se.tube42.drum.data.World.size_tile;
import static se.tube42.drum.data.World.stripe_pad1;
import static se.tube42.drum.data.World.stripe_pad2_x;
import static se.tube42.drum.data.World.stripe_pad2_y;
import static se.tube42.drum.data.World.stripe_tile;
import static se.tube42.drum.data.World.ui_gap;
import static se.tube42.drum.data.World.ui_portrait;
import static se.tube42.drum.data.World.ui_scale;
import static se.tube42.drum.data.World.w_pad1;
import static se.tube42.drum.data.World.w_pad2;
import static se.tube42.drum.data.World.w_tile;
import static se.tube42.drum.data.World.win1_h;
import static se.tube42.drum.data.World.win1_w;
import static se.tube42.drum.data.World.win1_x0;
import static se.tube42.drum.data.World.win1_y0;
import static se.tube42.drum.data.World.win2_h;
import static se.tube42.drum.data.World.win2_w;
import static se.tube42.drum.data.World.win2_x0;
import static se.tube42.drum.data.World.win2_y0;
import static se.tube42.drum.data.World.x0_pad1;
import static se.tube42.drum.data.World.x0_pad2;
import static se.tube42.drum.data.World.x0_tile;
import static se.tube42.drum.data.World.y0_pad1;
import static se.tube42.drum.data.World.y0_pad2;
import static se.tube42.drum.data.World.y0_tile;

public class LayoutService {
    // clear the lowest n bits of a number, used to minimize sub-pixel madness
    private static int rlsb(int v, int bits) {
        return v & ~((1 << bits) - 1);
    }

    private static void layout_win1(int x0, int y0, int w0, int h0) {
        win1_x0 = x0;
        win1_y0 = y0;
        win1_w = w0;
        win1_h = h0;

        // pad1
        size_pad1 = rlsb(Math.min(w0 / 4, h0 / 4), 3);
        stripe_pad1 = Math.min(w0 / 4, h0 / 4);
        w_pad1 = h_pad1 = size_pad1 * 4;
        x0_pad1 = x0 + (w0 - 3 * stripe_pad1 - size_pad1) / 2;
        y0_pad1 = y0 + (h0 - 3 * stripe_pad1 - size_pad1) / 2;

        // pad2
        size_pad2 = rlsb(Math.min(w0 / 8, h0 / 8), 1);
        stripe_pad2_x = w0 / 8;
        stripe_pad2_y = h0 / 4;
        w_pad2 = stripe_pad2_x * 8;
        h_pad2 = stripe_pad2_y * 4;
        x0_pad2 = x0 + (w0 - size_pad2 - 7 * stripe_pad2_x) / 2;
        y0_pad2 = y0 + (h0 - size_pad2 - 3 * stripe_pad2_y) / 2;
    }

    private static void layout_win2(int x0, int y0, int w0, int h0) {
        win2_x0 = x0;
        win2_y0 = y0;
        win2_w = w0;
        win2_h = h0;

        size_tile = rlsb(Math.min(w0 / 4, h0 / 4), 3);
        stripe_tile = size_tile;
        w_tile = h_tile = size_tile * 4;
        x0_tile = x0 + (w0 - w_tile - size_tile + stripe_tile) / 2;
        y0_tile = y0 + (h0 - h_tile - size_tile + stripe_tile) / 2;
    }

    public static void resize(int w, int h) {
        ui_scale = Math.max(1, Math.min(w, h) / 200);
        ui_gap = Math.min(8, 4 * ui_scale);

        if (w < h) {
            // portrait:
            // [ pads  ]
            // [ tiles ]
            ui_portrait = true;
            layout_win1(ui_gap, h / 2 + ui_gap, w - 2 * ui_gap, h / 2 - 2 * ui_gap);
            layout_win2(ui_gap, ui_gap, w - 2 * ui_gap, h / 2 - 2 * ui_gap);
        } else {
            // landscape:
            // [ pads  ] [tiles]
            ui_portrait = false;
            layout_win1(ui_gap, ui_gap, w / 2 - 2 * ui_gap, h - 2 * ui_gap);
            layout_win2(w / 2 + ui_gap, ui_gap, w / 2 - 2 * ui_gap, h - 2 * ui_gap);
        }
    }
}
