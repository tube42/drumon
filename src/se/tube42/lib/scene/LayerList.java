
package se.tube42.lib.scene;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

import se.tube42.lib.tweeny.*;
import se.tube42.lib.item.*;

public class LayerList
{
    private Layer [] list;
    private int list_cnt;
    private Layer list_hit;

    public LayerList()
    {
        this.list = new Layer[6];
        this.list_cnt = 0;
        this.list_hit = null;
    }

    public int getSize()
    {
        return list_cnt;
    }

    public Layer get(int index)
    {
        while(index >= getSize())
            add( new Layer());

        return list[index];
    }

    public Layer add(Layer l)
    {
        if(list_cnt == list.length)
            grow();

        list[list_cnt++] = l;
        return l;
    }

    private void grow()
    {
        Layer [] tmp = new Layer[list.length * 2 + 4];
        for(int i = 0; i < list.length; i++)
            tmp[i] = list[i];
        list = tmp;
    }

    // ---------------------------------------

    public void update(float dt)
    {
        final int len = list_cnt;
        for(int i = 0; i < len; i++)
            list[i].update(dt);
    }

    public void draw(SpriteBatch sb)
    {
        draw(sb, 0, list_cnt);
    }

    public void draw(SpriteBatch sb, int start, int count)
    {
        final int end = Math.min(getSize(), start + count);
        for(int i = start; i < end; i++)
            list[i].draw(sb);
    }

    public BaseItem hit(float x, float y)
    {
        for(int i = list_cnt; i >= 0; ) { // reverse order
            final Layer l = list[--i];
            BaseItem bi = l.hit(x, y);
            if(bi != null) {
                list_hit = l;
                return bi;
            }

            // dont touch past this item!
            if( (l.flags & Layer.FLAG_TOUCH_STOP) != 0)
                return null;
        }
        return null;
    }


}
