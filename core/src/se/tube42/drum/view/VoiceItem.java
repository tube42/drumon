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
	public static final int
		ANIM_NORMAL = 0,
		ANIM_SCALE = 1,
		ANIM_DOWN = 2,
		ANIM_UP = 3
		;
    private TextureRegion [] tex_decals;
    private int dec0, dec1, dec2;
    private boolean active;

    public VoiceItem(TextureRegion [] tex_decals, int variations, int icon, int color)
    {
        super(TILE_BUTTON0, icon, color);

        this.tex_decals = tex_decals;
        this.dec0 = ICON_SOUND_SELECT_TEMPLATE + variations - 1;
        dec1 = dec2 = -1;
        active = false;
        setAnimType(ANIM_NORMAL);
    }

    public void animPress()
    {
        final float r = ServiceProvider.getRandom(0.05f, 0.1f);
        set(ITEM_S, 1.2f).configure(1 * r, null)
              .tail(1f).configure(1 * r, null);
    }


    public void animAction(float amnt)
    {
		final float t = ServiceProvider.getRandom(0.2f, 0.3f);
        amnt = Math.abs(amnt);
        amnt = Math.min(1.4f, amnt);
		amnt = Math.max(0.2f, amnt);


		switch(animtype) {
		case ANIM_NORMAL:
			amnt *= 8;
			set(ITEM_R, +amnt, -amnt / 2).configure(t, null)
				.tail(0).configure(t, TweenEquation.BACK_OUT);
			break;

		case ANIM_SCALE:
			amnt = (amnt + 3) / 4;
			set(ITEM_S, amnt).configure(t / 2, TweenEquation.BACK_OUT)
				.tail(1).configure(t, TweenEquation.BACK_OUT);
			break;
		case ANIM_DOWN:
		case ANIM_UP:
			amnt = getH() * (amnt + 1) / 32;
			amnt = animtype == ANIM_UP ? amnt : - amnt;
			set(ITEM_U, amnt).configure(t / 2, TweenEquation.BACK_OUT)
				.tail(0).configure(t, TweenEquation.BACK_OUT);
			break;
		}
    }

    public void setVariant(int sample, int bank)
    {
        dec1 = ICON_SOUND_SELECT + sample;
        dec2 = bank == 0 ? -1 : ICON_PATTERN_SELECT + bank;
    }

    public void setActive(boolean active) {
        super.setActive(active);
        this.active = active;
    }


    public void draw(SpriteBatch sb)
    {
        super.draw(sb);

        // draw decals if this voice is active
        if(active) {
            final float a = getAlpha();
            final float s = getScale();
            final float x = getX() + get(ITEM_V);
            final float y = getY() + get(ITEM_U);
            final float r = getRotation();
            final float w2 = w / 2;
            final float h2 = h / 2;
            final float hp = World.halfpixel;

            sb.setColor(1, 1, 1, a);

            if(dec0 != -1)
                sb.draw(tex_decals[dec0], x + hp, y + hp, w2, h2, w, h, s, s, r);
            if(dec1 != -1)
                sb.draw(tex_decals[dec1], x + hp, y + hp, w2, h2, w, h, s, s, r);
            if(dec2 != -1)
                sb.draw(tex_decals[dec2], x + hp, y + hp, w2, h2, w, h, s, s, r);
        }
    }
}
