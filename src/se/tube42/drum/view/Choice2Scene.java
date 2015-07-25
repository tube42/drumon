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
    private int x_min, x_max, x;
    private int y_min, y_max, y;

    private SpriteItem canvas, mark;
    private SpriteItem icon;

    private boolean hit_canvas;
    
    private Object target;
    private int choice, id;
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
        mark = new SpriteItem(World.tex_rect);
        mark.setColor(0xA01010);

        getLayer(0).add(canvas);
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
    }

    public void onHide()
    {
        super.onShow();

        canvas.set(BaseItem.ITEM_A, 1, 0).configure(0.2f, null);
        canvas.set(BaseItem.ITEM_S, 1, 1.4f).configure(0.1f, null);

        mark.set(BaseItem.ITEM_A, 1, 0).configure(0.4f, null);
    }


    // ------------------------------------------------

    private void configure(
              int x_min, int x_max, int x,
              int y_min, int y_max, int y)
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
        final int xc = Math.max(x_min, Math.min(x_max, (int)(xm + 0.5f)));
        final int yc = Math.max(y_min, Math.min(y_max, (int)(ym + 0.5f)));
        
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
        final int s = Math.min(w, h) - World.tile_size;
        mark.setSize(World.tile_size, World.tile_size);
        canvas.setSize(s, s);
        canvas.setPosition((w - s) / 2, (h - s) / 2);
        icon.setSize(World.tile_size / 2, World.tile_size / 2);

        x0 = canvas.getX() + mark.getW() / 2 + 1;
        y0 = canvas.getY() + mark.getH() / 2 + 1;
        xd = Math.max(1, canvas.getW() - mark.getW() - 2);
        yd = Math.max(1, canvas.getH() - mark.getH() - 2);

        choice_update();
    }

    // ----------------------------------------------------------
    public void setChoice(Object target, int choice, int id)
    {
        this.target = target;;
        this.choice = choice;
        this.id = id;

        // update UI
        int t0 = choice_get_icon();
        if(t0 != -1) {
            icon.setIndex(t0);
            icon.flags |= BaseItem.FLAG_VISIBLE;
        } else {
            icon.flags &= ~BaseItem.FLAG_VISIBLE;
        }
        
        // set initial values
        choice_init();
    }
    
    // ----------------------------------------------------------
    
    private int choice_get_icon()
    {
        switch(choice) {
        case CHOICE2_COMPRESS:
            return ICON_COMPRESS;
        case CHOICE2_VOLUME:
            return ICON_VOLUME;
        default:
            return -1;
        }
    }
    
    private void choice_init()
    {
        // get initial configuration and values
        switch(choice) {
        case CHOICE2_VOLUME:
            Program prog = (Program) target;
            // note 1: mirror x axis => the no variation point is in middle of screen => happy user
            configure(-MAX_VARIATION, MAX_VARIATION, prog.getVolumeVariation(id),
                      MIN_VOLUME, MAX_VOLUME, (int)(100f * prog.getVolume(id))
                      );
            break;
            
        case CHOICE2_COMPRESS:
            Effect comp = (Effect) target;
            configure(0, 100, (int)(100 * comp.getConfig(Compressor.CONFIG_SRC) + 0.5f),
                      0, 100, (int)(100 * comp.getConfig(Compressor.CONFIG_DST) + 0.5f));
            break;
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
        switch(choice) {
        case CHOICE2_VOLUME:
            Program prog = (Program) target;
            prog.setVolumeVariation(id, Math.abs(x)); // see note 1
            prog.setVolume(id, y / 100f);
            break;
        case CHOICE2_COMPRESS:
            Effect comp = (Effect) target;
            comp.setConfig(Compressor.CONFIG_SRC, x / 100f);
            comp.setConfig(Compressor.CONFIG_DST, y / 100f);
            break;

        }
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


    public boolean touch(int x, int y, boolean down, boolean drag)
    {
        if(down && !drag) {
            hit_canvas = canvas.hit(x, y);
        }

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
