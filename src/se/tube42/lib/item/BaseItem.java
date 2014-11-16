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
          ITEM_A = 4
          ;
    public static final int
          FLAG_VISIBLE    = 1,
          FLAG_TOUCHABLE  = 2,
          FLAG_TOUCH_STOP = 4,          
          FLAG_DEAD       = 8
          ;
    
    
    // --------------------------------------------------------------
    
    public KSPool pool;
    public float d0, d1, d2; // extra data
    protected float w, h;    
    public float cr, cg, cb;
    public int flags;    
    public String name;
    
    private float [] saved_pos = null;
    private int saved_index = -1;

    public BaseItem()
    {
        this(null);  
    }
    
    public BaseItem(String name)
    {
        super(6);  
        
        // temporary for now        
        setColor(null); 
        this.name = name;
        this.w = this.h = 64; 
        this.flags = FLAG_VISIBLE /* | FLAG_TOUCHABLE */;
        this.pool = null;
        reset();
    }
    
    public void release()
    {
        if(pool != null) {
            pool.put(this);
            pool = null;
        }
    }
    
    public void reset()
    {
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
    // saved position:
    private float[] get_saved_pos()
    {
        if(saved_pos == null)
            saved_pos = new float[2 * 4];
        return saved_pos;
    }

    public float loadX(int index) { return get_saved_pos()[ index * 2 + 0]; }
    public float loadY(int index) { return get_saved_pos()[ index * 2 + 1]; }
    public int getSavedIndex() { return saved_index; }

    public void savePosition(int index)
    {
        savePosition(index, getX(), getY());
    }
    
    public void savePosition(int index, float x, float y)
    {
        final float [] v = get_saved_pos();
        v[index * 2 + 0] = x;
        v[index * 2 + 1] = y;
    }

    public void addPosition(int from, int to, float dx, float dy)
    {
        savePosition(to, dx + loadX(from), dy  + loadY(from));
    }

    public void loadPosition(int index)
    {
        saved_index = index;
        setPosition(loadX(index), loadY(index));
    }

    public TweenNode loadPosition(int index, float t, TweenEquation eq)
    {
        saved_index = index;
        set(ITEM_X, loadX(index)).configure(t, eq);
        return set(ITEM_Y, loadY(index)).configure(t, eq);
    }

    public TweenNode loadPosition(int from, int to, float t, TweenEquation eq)
    {
        loadPosition(from);
        return loadPosition(to, t, eq);
    }

    public void loadPositionIf(int index, float t, TweenEquation eq)
    {
        if(saved_index != index)
            loadPosition(index, t, eq);
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
