package se.tube42.drum.view;


import com.badlogic.gdx.Input.*;


import se.tube42.lib.tweeny.*;
import se.tube42.lib.scene.*;
import se.tube42.lib.item.*;

import se.tube42.drum.data.*;

import static se.tube42.drum.data.Constants.*;

public class ChoiceScene extends Scene
{
    private int y_min, y_max, y;

    private SpriteItem canvas, mark;
    private SpriteItem desc0, desc1;

    private BaseText text;
    private boolean hit_canvas;

    private Object target;
    private int choice, id;
    private float y0, yd;

    public ChoiceScene()
    {
        super("choice");

        this.yd = 1; // default value to avoid div by zero for now

        canvas = new SpriteItem(World.tex_rect);
        canvas.setColor(0xFFFFFF);
        canvas.setIndex(0);

        desc0 = new SpriteItem(World.tex_icons);
        desc1 = new SpriteItem(World.tex_icons);

        mark = new SpriteItem(World.tex_rect);
        mark.setColor(0xA01010);

        text = new BaseText(World.font1);
        text.setColor(0x000000);
        text.setAlignment(-0.5f, +0.5f);


        getLayer(0).add(canvas);
        getLayer(0).add(mark);
        getLayer(0).add(text);

        getLayer(1).add(desc0);
        getLayer(1).add(desc1);
    }

    // ------------------------------------------------

    public void onShow()
    {
        super.onShow();

        canvas.set(BaseItem.ITEM_A, 0, 1).configure(0.2f, null);
        canvas.set(BaseItem.ITEM_S, 1.4f, 1).configure(0.1f, null);

        mark.set(BaseItem.ITEM_A, 0, 1).configure(0.3f, null);
        text.set(BaseItem.ITEM_A, 0, 1).configure(0.5f, null);
    }


    public void onHide()
    {
        super.onHide();

        canvas.set(BaseItem.ITEM_A, 1, 0).configure(0.2f, null);
        canvas.set(BaseItem.ITEM_S, 1, 1.4f).configure(0.1f, null);

        mark.set(BaseItem.ITEM_A, 1, 0).configure(0.3f, null);
        text.set(BaseItem.ITEM_A, 1, 0).configure(0.5f, null);
    }

    // ------------------------------------------------

    private void configure(int min, int max, int curr)
    {
        if(min == max) max++; // avoid div by zero
        this.y_min = min;
        this.y_max = max;
        this.y = Math.min(y_max, Math.max(y_min, curr));
        choice_update();
    }

    /*
    public int getValue()
    {
        return y;
    }*/

    private boolean set(int x, int y)
    {

        final float yn = (y - y0) / yd;
        final float ym = y_min + yn * (y_max - y_min);
        final int yc = Math.max(y_min, Math.min(y_max, (int)(ym + 0.5f)));

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
        final int gap = World.size_tile / 2;
    	final int x0 = gap;
    	final int y0 = gap;
        final int w1 = w - 2 * gap;
        final int h1 = h - 2 * gap;
        
        System.out.println("P0 = " + x0 + "," + y0);
        System.out.println("SIZE = " + w1 + "," + h1);


        mark.setSize(w1 - World.size_tile / 4, World.size_tile);
        mark.setPosition((w - mark.getW()) / 2, 0);

        canvas.setSize(w1, h1);
        canvas.setPosition(x0, y0);

        desc0.setSize(World.size_tile / 2, World.size_tile / 2);
        desc1.setSize(World.size_tile / 2, World.size_tile / 2);

        text.setPosition( w / 2, h / 2);

        this.y0 = y0 + mark.getH() / 2 + 1;
        this.yd = Math.max(1, canvas.getH() - mark.getH() - 2);

        choice_update();
    }

    // ----------------------------------------------------------
    public void setChoice(Object target, int choice, int id)
    {
        this.target = target;
        this.choice = choice;
        this.id = id;

        // update UI
        int t0 = choice_get_icon0();
        int t1 = choice_get_icon1();

        if(t0 != -1) {
            desc0.setIndex(t0);
            desc0.flags |= BaseItem.FLAG_VISIBLE;
        } else {
            desc0.flags &= ~BaseItem.FLAG_VISIBLE;
        }

        if(t1 != -1) {
            desc1.setIndex(t1);
            desc1.flags |= BaseItem.FLAG_VISIBLE;
        } else {
            desc1.flags &= ~BaseItem.FLAG_VISIBLE;
        }


        choice_init();
    }


    // ----------------------------------------------------------

    private int choice_get_icon0()
    {

        switch(choice) {
        case CHOICE_TEMPO:
            return ICON_METRONOME;
        default:
            return -1;
        }
    }

    private int choice_get_icon1()
    {

        switch(choice) {
        default:
            return -1;
        }
    }

    private void choice_init()
    {
        // get initial configuration and values
        switch(choice) {
        case CHOICE_TEMPO:
            Program prog = (Program) target;
            configure(MIN_TEMPO, MAX_TEMPO, prog.getTempo());
            break;
        }
    }

    private void choice_update()
    {

        final float yn = (y - y_min) / (float) (y_max - y_min);
        final float y1 = y0 + yd * yn;
        mark.setImmediate(BaseItem.ITEM_Y, y1 - mark.getH() / 2);

        text.setImmediate(BaseItem.ITEM_Y, y1);
        text.setText("" + y);

        final float yc = mark.getY() + (mark.getH() - desc0.getH()) / 2;
        desc0.setPosition(mark.getX() + desc0.getW() / 2, yc);
        desc1.setPosition(mark.getX() + mark.getW() - 1.5f * desc0.getW(), yc);

        // update world
        switch(choice) {
        case CHOICE_TEMPO:
            Program prog = (Program) target;
            prog.setTempo(y);
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


    public boolean touch(int p, int x, int y, boolean down, boolean drag)
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
