package se.tube42.lib.item;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

import se.tube42.lib.tweeny.*;
import se.tube42.lib.scene.*;
import se.tube42.lib.util.*;

public class SpriteItem extends BaseItem
{
    protected TextureRegion [] textures;
    protected int index;


    public SpriteItem(TextureRegion [] textures)
    {
        this(textures, 0);
    }

    public SpriteItem(TextureRegion [] textures, int index)
    {
        this.textures = textures;
        this.index = index;
        w = textures[index].getRegionWidth();
        h = textures[index].getRegionHeight();
    }

    public int getIndex() { return index; }

    public void setIndex(int index)
    {
        this.index = index;
    }

    public void draw(SpriteBatch sb)
    {
        final float a = getAlpha();
        final float s = getScale();
        final float x = getX();
        final float y = getY();
        final float r = getRotation();
        final float w2 = w / 2;
        final float h2 = h / 2;
        final float hp = UIC.halfpixel;

        sb.setColor( cr, cg, cb, a);

        sb.draw(textures[index],
                x + hp, y + hp,
                w2, h2,
                w, h,
                s, s, r);
    }
}
