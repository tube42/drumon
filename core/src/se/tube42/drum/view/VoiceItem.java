package se.tube42.drum.view;

import com.badlogic.gdx.graphics.g2d.*;

import se.tube42.drum.data.*;
import se.tube42.drum.logic.*;
import se.tube42.lib.tweeny.*;

import static se.tube42.drum.data.Constants.*;


public class VoiceItem extends PressItem
{
	public static final int
		ANIM_NORMAL = 0,
		ANIM_SCALE = 1,
		ANIM_DOWN = 2,
		ANIM_UP = 3
		;

    private int dec0, dec1;

    public VoiceItem(int icon, int color)
    {
        super(TILE_BUTTON0, icon, color);

		dec0 = dec1 = -1;
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
        dec0 = sample == 0 ? -1 : ICON_DECALS_1 + sample;
        dec1 = bank == 0 ? -1 : ICON_DECALS_A + 1;
    }


    public void draw(SpriteBatch sb)
    {
        super.draw(sb);
        final float hp = World.halfpixel;

        if(dec0 != -1 || dec1 != -1) {
            final float a = getAlpha();
            final float s = getScale();
            final float x = getX() + get(ITEM_V);
            final float y = getY() + get(ITEM_U);
            final float r = getRotation();
            final float w2 = w / 2;
            final float h2 = h / 2;
            final float w4 = w / 4;
            final float h4 = h / 4;

            if(dec0 != -1) {
                sb.draw(World.tex_icons[dec0],
                        x + hp, h2 + y + hp,
                        w4, h4,
                        w2, h2,
                        s, s, r);
            }

            if(dec1 != -1) {
                sb.draw(World.tex_icons[dec1],
                        w2 + x + hp, y + hp,
                        w4, h4,
                        w2, h2,
                        s, s, r);
            }

        }
    }


}
