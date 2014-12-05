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
    private int dec0, dec1;

    public VoiceItem(int icon, int color)
    {
        super(TILE_BUTTON0, icon, color);

        dec0 = dec1 = -1;
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

    public void setVoiceVariant(int voice, int pattern)
    {
        dec0 = voice == 0 ? -1 : DECALS_NUM + voice;
        dec1 = pattern == 0 ? -1 : DECALS_ALPHA + pattern;
    }


    public void draw(SpriteBatch sb)
    {
        super.draw(sb);

        if(dec0 != -1 || dec1 != -1) {
            final float a = getAlpha();
            final float s = getScale();
            final float x = getX() + get(ITEM_V);
            final float y = getY();
            final float r = getRotation();
            final float w2 = w / 2;
            final float h2 = h / 2;
            final float w4 = w / 4;
            final float h4 = h / 4;

            if(dec0 != -1) {
                sb.draw(World.tex_decals[dec0],
                        x + 0.5f, h2 + y + 0.5f,
                        w4, h4,
                        w2, h2,
                        s, s, r);
            }

            if(dec1 != -1) {
                sb.draw(World.tex_decals[dec1],
                        w2 + x + 0.5f, y + 0.5f,
                        w4, h4,
                        w2, h2,
                        s, s, r);
            }

        }
    }


}
