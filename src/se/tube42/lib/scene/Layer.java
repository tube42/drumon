
package se.tube42.lib.scene;

import java.util.*;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

import se.tube42.lib.tweeny.*;

import se.tube42.lib.item.*;
import se.tube42.lib.service.*;

public class Layer
{
    public static final int
          FLAG_VISIBLE = 1,
          FLAG_TOUCHABLE = 2,
          FLAG_TOUCH_STOP = 4,
          FLAG_UPDATE = 8
          ;

    private BaseItem [] list;
    private int list_cnt;

    public int flags;

    public Layer()
    {
        this.list = new BaseItem[16];
        this.list_cnt = 0;
        this.flags = FLAG_VISIBLE | FLAG_TOUCHABLE;
    }



    // --------------------------------------------

    public void hide()
    {

    }

    public void show()
    {
    }


    // --------------------------------------------

    public Layer add(BaseItem [] bi)
    {
        for(BaseItem b : bi)
            add(b);
        return this;
    }

    public Layer add(BaseItem bi)
    {
        if(list_cnt == list.length)
            grow();
        list[list_cnt++] = bi;
        return this;
    }

    public int getSize()
    {
        return list_cnt;
    }

    public BaseItem get(int index)
    {
        return list[index];
    }

    private void grow()
    {
        BaseItem [] tmp = new BaseItem[list.length * 2 + 4];
        for(int i = 0; i < list.length; i++)
            tmp[i] = list[i];
        list = tmp;
    }

    // -----------------------------------------

    public void update(float dt)
    {
        if( (flags & FLAG_UPDATE) == 0 || (flags & FLAG_VISIBLE) == 0)
            return;

        final int len = list_cnt;
        for(int i = 0; i < len; i++) {
            final BaseItem ni = list[i];
            if(ni != null)
                ni.update(dt);
        }

    }

    public void draw(SpriteBatch sb)
    {
        if( (flags & FLAG_VISIBLE) == 0)
            return;

        final int len = list_cnt;
        for(int i = 0; i < len; i++) {
            final BaseItem ni = list[i];
            if( (ni == null) || (ni.flags & BaseItem.FLAG_VISIBLE) == 0)
                continue;

            ni.draw(sb);
        }
    }

    public BaseItem hit(float x, float y)
    {
        if( (flags & FLAG_TOUCHABLE) == 0)
            return null;

        if( (flags & FLAG_VISIBLE) == 0)
            return null;

        for(int i = list_cnt; i > 0; ) {
            final BaseItem ni = list[--i]; // reverse order
            if( (ni == null)
                || (ni.flags & BaseItem.FLAG_TOUCHABLE) == 0
                || (ni.flags & BaseItem.FLAG_VISIBLE) == 0)
                continue;
            if(ni.hit(x, y))
               return ni;
        }
        return null;
    }

    public void clear()
    {
        list_cnt = 0;
    }

    /** crappy O(n) remove, because we don't do this very often */
    public boolean remove(BaseItem item)
    {
        boolean found = false;
        for(int w = 0, r = 0; r < list_cnt; r++) {
            if(w != r) list[w] = list[r];
            if(list[r] != item) w++;
            else found = true;
        }
        return found;
    }


    public void moveLast(BaseItem bi)
    {
        if( remove(bi))
            add(bi);
    }
}
