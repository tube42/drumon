package se.tube42.drum.view;


import com.badlogic.gdx.Input.*;


import se.tube42.lib.tweeny.*;
import se.tube42.lib.scene.*;
import se.tube42.lib.item.*;

import se.tube42.drum.data.*;
import se.tube42.drum.audio.*;

import static se.tube42.drum.data.Constants.*;

public class Choice2Scene extends Scene
{
    private Parameters params;
    private float x_min, x_max, x;
    private float y_min, y_max, y;
    private int idx1, idx2;
    private BaseText label1, label2;
    
    private SpriteItem canvas, mark;
    private SpriteItem icon;

    private boolean hit_canvas, seen_down;
    private float x0, y0, xd, yd;

    public Choice2Scene()
    {
        super("choice2");

        // default value to avoid div by zero for now
        this.yd = 1;
        this.xd = 1;

        canvas = new SpriteItem(World.tex_rect);
        canvas.setColor(0xFFFFFF);
        canvas.setIndex(0);
        icon = new SpriteItem(World.tex_icons);
        mark = new SpriteItem(World.tex_tiles);
        mark.setColor(0xA01010);
        mark.setIndex(TILE_CIRCLE);

        label1 = new BaseText(World.font2);
        label2 = new BaseText(World.font2);
        label1.setColor(0x808080);
        label2.setColor(0x808080);
        label1.setAlignment(-0.5f, +1.5f);
        label2.setAlignment(-0.5f, -0.5f);

        getLayer(0).add(canvas);
        getLayer(0).add(label1);
        getLayer(0).add(label2);
        getLayer(0).add(mark);
        getLayer(1).add(icon);
    }

    // ------------------------------------------------

    public void onShow()
    {
        super.onShow();

        canvas.set(BaseItem.ITEM_A, 0, 1).configure(0.2f, null);
        canvas.set(BaseItem.ITEM_S, 1.4f, 1).configure(0.1f, null);

        mark.set(BaseItem.ITEM_A, 0, 1).configure(0.5f, null);
        seen_down = false;
    }

    public void onHide()
    {
        super.onHide();

        canvas.set(BaseItem.ITEM_A, 1, 0).configure(0.2f, null);
        canvas.set(BaseItem.ITEM_S, 1, 1.4f).configure(0.1f, null);

        mark.set(BaseItem.ITEM_A, 1, 0).configure(0.4f, null);
    }


    // ------------------------------------------------

    private void configure(
              float x_min, float x_max, float x,
              float y_min, float y_max, float y)
    {
        // avoid div by zero
        if(x_min == x_max) x_max++;
        if(y_min == y_max) y_max++;

        this.x_min = x_min;
        this.x_max = x_max;
        this.y_min = y_min;
        this.y_max = y_max;

        this.x = Math.min(x_max, Math.max(x_min, x));
        this.y = Math.min(y_max, Math.max(y_min, y));
        choice_update();
    }


    private boolean set(int x, int y)
    {
        if(!canvas.hit(x, y))
            return false;

        final float xn = (x - x0) / xd;
        final float yn = (y - y0) / yd;
        final float xm = x_min + xn * (x_max - x_min);
        final float ym = y_min + yn * (y_max - y_min);
        final float xc = Math.max(x_min, Math.min(x_max, xm));
        final float yc = Math.max(y_min, Math.min(y_max, ym));

        if(xc != this.x || yc != this.y) {
            this.x = xc;
            this.y = yc;
            choice_update();
        }
        return true;
    }

    // ------------------------------------------------

    public void resize(int w, int h)
    {
        final int gap = World.ui_portrait ? World.size_tile / 2 : 2;
        final int s = Math.min(w, h) - 2 * gap;

        mark.setSize(World.size_tile, World.size_tile);
        canvas.setSize(s, s);
        canvas.setPosition((w - s) / 2, (h - s) / 2);
        icon.setSize(World.size_tile / 2, World.size_tile / 2);

        x0 = canvas.getX() + mark.getW() / 2 + 1;
        y0 = canvas.getY() + mark.getH() / 2 + 1;
        xd = Math.max(1, canvas.getW() - mark.getW() - 2);
        yd = Math.max(1, canvas.getH() - mark.getH() - 2);

        label1.setPosition( w / 2, canvas.getY());
        label2.setPosition( w / 2, canvas.getY() + canvas.getH());

        choice_update();
    }

    // ----------------------------------------------------------
    public void set(Parameters params, int idx1, int idx2, int iconnr)
    {
        this.params = params;
        this.idx1 = idx1;
        this.idx2 = idx2;

        if(iconnr == -1) {
            icon.flags &= ~BaseItem.FLAG_VISIBLE;
        } else {
            icon.setIndex(iconnr);
            icon.flags |= BaseItem.FLAG_VISIBLE;
        }

        configure(params.getMin(idx1), params.getMax(idx1), 
                  params.get(idx1), params.getMin(idx2),
                  params.getMax(idx2), params.get(idx2)
                  );

        configure_label(label1, "x=", params.getLabel(idx1));
        configure_label(label2, "y=", params.getLabel(idx2));
    }

    // ----------------------------------------------------------
    private void configure_label(BaseText item,
              String prefix, String label)
    {
        if(label != null && label.length() > 0) {
            item.setText(prefix + label);
            item.flags |= BaseItem.FLAG_VISIBLE;
        } else {
            item.flags &= ~BaseItem.FLAG_VISIBLE;
        }
    }

    private void choice_update()
    {
        // uppdate view
        final float xn = (x - x_min) / (float) (x_max - x_min);
        final float yn = (y - y_min) / (float) (y_max - y_min);
        final float x1 = x0 + xd * xn;
        final float y1 = y0 + yd * yn;
        mark.setImmediate(BaseItem.ITEM_X, x1 - mark.getW() / 2);
        mark.setImmediate(BaseItem.ITEM_Y, y1 - mark.getH() / 2);

        icon.setPosition(
                  mark.getX() - (icon.getW() - mark.getW()) / 2,
                  mark.getY() - (icon.getH() - mark.getH()) / 2
                  );

        // update world
        params.set(idx1, x);
        params.set(idx2, y);
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
            seen_down = true;
            hit_canvas = canvas.hit(x, y);
        }

        /* probably remaining touches from another scene */
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
