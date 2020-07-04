package se.tube42.drum.view;


import com.badlogic.gdx.Input.*;


import se.tube42.lib.tweeny.*;
import se.tube42.lib.scene.*;
import se.tube42.lib.item.*;

import se.tube42.drum.data.*;
import se.tube42.drum.audio.*;
import static se.tube42.drum.data.Constants.*;

public class ChoiceScene extends Scene
{
    private Parameters params;
    private float y_min, y_max, y;
    private int idx1;
    
    private SpriteItem canvas, mark;
    private SpriteItem desc0;

    private BaseText text_val, text_label;
    private boolean hit_canvas, seen_down;
    private float y0, yd;

    public ChoiceScene()
    {
        super("choice");

        this.yd = 1; // default value to avoid div by zero for now

        canvas = new SpriteItem(World.tex_rect);
        canvas.setColor(0xFFFFFF);
        canvas.setIndex(0);

        desc0 = new SpriteItem(World.tex_icons);

        mark = new SpriteItem(World.tex_rect);
        mark.setColor(0xA01010);

        text_val = new BaseText(World.font1);
        text_val.setColor(0x000000);
        text_val.setAlignment(-0.5f, +0.5f);

        text_label = new BaseText(World.font2);
        text_label.setColor(0x808080);
        text_label.setAlignment(-0.5f, 1.5f);

        getLayer(0).add(canvas);
        getLayer(0).add(text_label);
        getLayer(0).add(mark);
        getLayer(0).add(text_val);
        getLayer(1).add(desc0);
    }

    // ------------------------------------------------

    public void onShow()
    {
        super.onShow();

        canvas.set(BaseItem.ITEM_A, 0, 1).configure(0.2f, null);
        canvas.set(BaseItem.ITEM_S, 1.4f, 1).configure(0.1f, null);

        mark.set(BaseItem.ITEM_A, 0, 1).configure(0.3f, null);
        text_val.set(BaseItem.ITEM_A, 0, 1).configure(0.5f, null);
        text_label.set(BaseItem.ITEM_A, 0, 1).configure(0.5f, null);

        seen_down = false;
    }


    public void onHide()
    {
        super.onHide();

        canvas.set(BaseItem.ITEM_A, 1, 0).configure(0.2f, null);
        canvas.set(BaseItem.ITEM_S, 1, 1.4f).configure(0.1f, null);

        mark.set(BaseItem.ITEM_A, 1, 0).configure(0.3f, null);
        text_val.set(BaseItem.ITEM_A, 1, 0).configure(0.5f, null);
        text_label.set(BaseItem.ITEM_A, 1, 0).configure(0.5f, null);
    }

    // ------------------------------------------------

    private boolean set(int x, int y)
    {
        final float yn = (y - y0) / yd;
        final float ym = y_min + yn * (y_max - y_min);
        final float yc = Math.max(y_min, Math.min(y_max, ym));

        if(this.y != yc) {
            this.y = yc;
            choice_update();
        }
        return true;
    }

    // ------------------------------------------------

    public void resize(int w, int h)
    {
    	// try to get the same size as the pads:
        final int gap = World.ui_portrait ? World.size_tile / 2 : 2;
        final int s = Math.min(w, h) - 2 * gap;
        final int x0 = (w - s) / 2;
        final int y0 = (h - s) / 2;
        final int w1 = s;
        final int h1 = s;

        mark.setSize(w1 - World.size_tile / 4, World.size_tile);
        mark.setPosition((w - mark.getW()) / 2, 0);

        canvas.setSize(w1, h1);
        canvas.setPosition(x0, y0);

        desc0.setSize(World.size_tile / 2, World.size_tile / 2);

        text_val.setPosition( w / 2, h / 2);
        text_label.setPosition( w / 2, canvas.getY());

        this.y0 = y0 + mark.getH() / 2 + 1;
        this.yd = Math.max(1, canvas.getH() - mark.getH() - 2);

        choice_update();
    }

    // ----------------------------------------------------------
    public void set(Parameters params, int idx1, int icon0)
    {
        this.idx1 = idx1;
        this.params = params;

        if(icon0 != -1) {
            desc0.setIndex(icon0);
            desc0.flags |= BaseItem.FLAG_VISIBLE;
        } else {
            desc0.flags &= ~BaseItem.FLAG_VISIBLE;
        }

        y_min = params.getMin(idx1);
        y_max = params.getMax(idx1);
        if(y_min == y_max) y_max ++; // Avoid div by zero
        this.y = Math.min(y_max, Math.max(y_min, params.get(idx1)));

        final String label = params.getLabel(idx1);
        if(label != null && label.length() > 0) {
            text_label.setText(label);
            text_label.flags |= BaseItem.FLAG_VISIBLE;
        } else {
            text_label.flags &= ~BaseItem.FLAG_VISIBLE;
        }

        choice_update();
    }

    private void choice_update()
    {

        final float yn = (y - y_min) / (y_max - y_min);
        final float y1 = y0 + yd * yn;
        mark.setImmediate(BaseItem.ITEM_Y, y1 - mark.getH() / 2);

        text_val.setImmediate(BaseItem.ITEM_Y, y1);
        text_val.setText("" + (int)(0.5f + y));

        final float yc = mark.getY() + (mark.getH() - desc0.getH()) / 2;
        desc0.setPosition(mark.getX() + desc0.getW() / 2, yc);

        // update world
        params.set(idx1, y);
    }

    // ----------------------------------------------------------

    public void go_back()
    {
        World.mgr.setScene(World.scene_drum, 160);
    }

    public boolean type(int key, boolean down)
    {
        if(down) {
            if(key == Keys.BACK || key == Keys.ESCAPE) {
                go_back();
                return true;
            }
        }
        return false;
    }


    public boolean touch(int p, int x, int y, boolean down, boolean drag)
    {
        if(down && !drag) {
            hit_canvas = canvas.hit(x, y);
            seen_down = true;
        }

        // we are seeing another scenes remaining touches)
        if(!seen_down)
            return false;

        if(hit_canvas) {
            set(x, y);
        }

        /* clicked outside ? */
        if(!down) {
            if(!hit_canvas && !canvas.hit(x, y))
                go_back();
        }

        return true;
    }

}
