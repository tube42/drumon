package se.tube42.lib.item;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

import se.tube42.lib.tweeny.*;
import se.tube42.lib.ks.*;
import se.tube42.lib.scene.*;

public abstract class BaseItem extends Item
{

    public static final int
          ITEM_S = 0,
          ITEM_R = 1,
          ITEM_X = 2,
          ITEM_Y = 3,
          ITEM_A = 4,
          ITEM_V = 5
          ;
    public static final int
          FLAG_VISIBLE    = 1,
          FLAG_TOUCHABLE  = 2,
          FLAG_TOUCH_STOP = 4,
          FLAG_DEAD       = 8
          ;


    // --------------------------------------------------------------
    
    protected float w, h;
    public float cr, cg, cb;
    public int flags;
    public int x2, y2;
    
    public BaseItem()
    {
        super(6);

        // temporary for now
        setColor(null);
        this.w = this.h = 64;
        this.flags = FLAG_VISIBLE /* | FLAG_TOUCHABLE */;
        reset();
    }

    public void reset()
    {
        setImmediate(ITEM_V, 0);
        setScale(1);
        setAlpha(1);
        setRotation(0);
        setPosition(0, 0);
    }

    public void setColor(float [] c)
    {
        if(c == null)
            cr = cb = cg = 1;
        else
            setColor(c[0], c[1], c[2]);
    }

    public void setColor(float r, float g, float b)
    {
        this.cr = r;
        this.cg = g;
        this.cb = b;
    }

    public void setColor(int rgb)
    {
        this.cr = ((rgb & 0xFF0000) >> 16) / 255f;
        this.cg = ((rgb & 0x00FF00) >>  8) / 255f;
        this.cb = ((rgb & 0x0000FF) >>  0) / 255f;

    }

    public void update(float dt)
    {
        // EMPTY
    }


    // --------------------------------------------

    public void setAlpha(float a)
    {
        setImmediate(ITEM_A, a);
    }

    public void setAlpha(float dur, float a)
    {
        set(ITEM_A, a).configure(dur, TweenEquation.LINEAR);
    }

    public void setScale(float s)
    {
        setImmediate(ITEM_S, s);
    }

    public void setScale(float dur, float s)
    {
        set(ITEM_S, s).configure(dur, TweenEquation.BACK_OUT);
    }

    public void setRotation(float r)
    {
        setImmediate(ITEM_R, r);
    }
    public void setRotation(float dur, float r)
    {
        set(ITEM_R, r).configure(dur, TweenEquation.LINEAR);
    }

    public void setPosition(float x, float y)
    {
        setImmediate(ITEM_X, x);
        setImmediate(ITEM_Y, y);
    }

    public void setPosition(float dur, float x, float y)
    {
        set(ITEM_X, x).configure(dur, TweenEquation.BACK_OUT);
        set(ITEM_Y, y).configure(dur, TweenEquation.BACK_OUT);
    }

    public float getScale() { return get(ITEM_S); }
    public float getRotation() { return get(ITEM_R); }
    public float getX() { return get(ITEM_X); }
    public float getY() { return get(ITEM_Y); }
    public float getAlpha() { return get(ITEM_A); }
    public float getW() { return w; }
    public float getH() { return h; }

    public void setSize(float w, float h)
    {
        this.w = w;
        this.h = h;
    }

    public void align(BaseItem bs, float align, float pad,
              boolean horiz, boolean vert)
    {
        float x = getX();
        float y = getY();

        if(horiz) x = bs.getX() + pad + (bs.w - w) * align;
        if(vert) y = bs.getY() + pad + (bs.h - h) * align;
        setPosition(x, y);
    }

    public void calcBounds()
    {
        // EMPTY
    }

    // --------------------------------------------

    public boolean hit(float x, float y)
    {
        // note: this function ignores current scale
        final float x0 = getX();
        final float y0 = getY();
        final float x1 = x0 + w;
        final float y1 = y0 + h;

        return x >= x0 && x <= x1 && y >= y0 && y <= y1;
    }

    public abstract void draw(SpriteBatch sb);
}
