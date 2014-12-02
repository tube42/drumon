package se.tube42.drum.view;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.Input.*;

import se.tube42.lib.tweeny.*;
import se.tube42.lib.ks.*;
import se.tube42.lib.scene.*;
import se.tube42.lib.util.*;
import se.tube42.lib.item.*;

import se.tube42.drum.data.*;
import se.tube42.drum.logic.*;

import static se.tube42.drum.data.Constants.*;


public class VoiceItem extends PressItem
{

    public VoiceItem(int variant, int icon, int color)
    {
        super(0, icon, color);
        setVariant(variant);
    }

    public void mark0()
    {
        final float r = ServiceProvider.getRandom(0.05f, 0.1f);
        set(ITEM_S, 1.2f).configure(1 * r, null)
              .tail(1f).configure(1 * r, null);
    }


    public void mark1()
    {
        final float r = ServiceProvider.getRandom(0.05f, 0.1f);
        set(ITEM_R, -5).configure(1 * r, null)
              .tail(+5).configure(2 * r, null)
              .tail(-3).configure(1 * r, null)
              .tail(+3).configure(1 * r, null)
           .tail(0f).configure(1 * r, null);

    }

    public void setVariant(int v)
    {
        setTile(TILE_BUTTON0_1 + v);
    }
}
