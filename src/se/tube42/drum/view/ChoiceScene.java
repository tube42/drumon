package se.tube42.drum.view;


import com.badlogic.gdx.Input.*;


import se.tube42.lib.tweeny.*;
import se.tube42.lib.scene.*;
import se.tube42.lib.item.*;

import se.tube42.drum.data.*;

import static se.tube42.drum.data.Constants.*;

public class ChoiceScene extends Scene
{
    private int min, max, curr;

    private SpriteItem canvas, mark;
    private SpriteItem desc0, desc1;

    private BaseText text;
    private boolean hit_canvas;
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

        text = new BaseText(World.font);
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

        canvas.set(BaseItem.ITEM_A, 0, 1).configure(0.3f, null);
        canvas.set(BaseItem.ITEM_S, 1.4f, 1).configure(0.1f, null);

        mark.set(BaseItem.ITEM_A, 0, 1).configure(0.5f, null);
        text.set(BaseItem.ITEM_A, 0, 1).configure(0.8f, null);
    }


    public void onHide()
    {
        super.onShow();

        canvas.set(BaseItem.ITEM_A, 1, 0).configure(0.3f, null);
        canvas.set(BaseItem.ITEM_S, 1, 1.4f).configure(0.1f, null);

        mark.set(BaseItem.ITEM_A, 1, 0).configure(0.5f, null);
        text.set(BaseItem.ITEM_A, 1, 0).configure(0.8f, null);
    }

    // ------------------------------------------------

    private void configure(int min, int max, int curr)
    {
        if(min == max) max++; // avoid div by zero
        this.min = min;
        this.max = max;
        this.curr = Math.min(max, Math.max(min, curr));
        update();
    }

    public int getValue()
    {
        return curr;
    }

    private boolean set(int x, int y)
    {
        if(!canvas.hit(x, y))
            return false;

        final float n = (y - y0) / yd;
        final float m = min + n * (max - min);
        final int c = Math.max(min, Math.min(max, (int)(m + 0.5f)));

        if(c != curr) {
            curr = c;
            update();
        }
        return true;
    }

    // ------------------------------------------------

    public void resize(int w, int h)
    {

        mark.setSize(w - World.tile_size - 2, World.tile_size);
        mark.setPosition(World.tile_size / 2 + 1, w / 2);

        canvas.setSize(w - World.tile_size, h - World.tile_size);
        canvas.setPosition(World.tile_size / 2, World.tile_size / 2);

        desc0.setSize(World.tile_size / 2, World.tile_size / 2);
        desc1.setSize(World.tile_size / 2, World.tile_size / 2);

        text.setPosition( w / 2, h / 2);

        y0 = canvas.getY() + mark.getH() / 2 + 1;
        yd = Math.max(1, canvas.getH() - mark.getH() - 2);

        update();
    }

    // ----------------------------------------------------------
    public void setChoice(int choice, int id)
    {
        this.choice = choice;
        this.id = id;

        // update UI
        int t0 = -1, t1 = -1;

        switch(choice) {
        case CHOICE_TEMPO:
            t0 = ICON_METRONOME;
            break;
        case CHOICE_VOLUME:
            t0 = ICON_VOLUME;
            t1 = VOICE_ICONS[id];
            break;
        case CHOICE_VARIATION:
            t0 = ICON_VARIATION;
            t1 = VOICE_ICONS[id];
            break;
        }

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



        // get initial configuration and values
        switch(choice) {
        case CHOICE_TEMPO:
            configure(MIN_TEMPO, MAX_TEMPO, World.prog.getTempo());
            break;
        case CHOICE_VOLUME:
            configure(MIN_VOLUME, MAX_VOLUME, (int)(100f *
                      World.prog.getVolume(id)));
            break;
        case CHOICE_VARIATION:
            configure(MIN_VARIATION, MAX_VARIATION,
                      World.prog.getVolumeVariation(id));
            break;
        }
    }

    private void update()
    {
        // uppdate view
        final float n = (curr - min) / (float) (max - min);
        float y = y0 + yd * n;

        mark.setImmediate(BaseItem.ITEM_Y, y - mark.getH() / 2);
        text.setImmediate(BaseItem.ITEM_Y, y);
        text.setText("" + curr);

        final float yc = mark.getY() + (mark.getH() - desc0.getH()) / 2;
        desc0.setPosition(mark.getX() + desc0.getW() / 2, yc);
        desc1.setPosition(mark.getX() + mark.getW() - 1.5f * desc0.getW(), yc);

        // update world
        switch(choice) {
        case CHOICE_TEMPO:
            World.prog.setTempo(curr);
            break;
        case CHOICE_VOLUME:
            World.prog.setVolume(id, curr / 100f);
            break;
        case CHOICE_VARIATION:
            World.prog.setVolumeVariation(id, curr);
            break;
        }
    }

    // ----------------------------------------------------------

    public void go_back()
    {
        World.mgr.setScene(World.scene_drum, 200);
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

        /* klicked outside ? */
        if(!down) {
            if(!hit_canvas && !canvas.hit(x, y))
                go_back();
        }

        return true;
    }

}
