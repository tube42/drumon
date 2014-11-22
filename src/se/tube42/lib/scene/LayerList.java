
package se.tube42.lib.scene;

import java.util.*;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

import se.tube42.lib.tweeny.*;
import se.tube42.lib.item.*;

public class LayerList
{
    private ArrayList<Layer> list;
    private Layer list_hit;

    public LayerList()
    {
        this.list = new ArrayList<Layer>();
        this.list_hit = null;
    }

    public int getSize()
    {
        return list.size();
    }

    public Layer get(int index)
    {
        while(index >= list.size())
            list.add( new Layer());

        return list.get(index);
    }

    public Layer add(Layer l)
    {
        list.add(l);
        return l;
    }

    // ---------------------------------------

    public void update(float dt)
    {
        final int len = getSize();
        for(int i = 0; i < len; i++)
            get(i).update(dt);
    }

    public void draw(SpriteBatch sb)
    {
        final int len = getSize();
        draw(sb, 0, len);
    }

    public void draw(SpriteBatch sb, int start, int count)
    {
        final int end = Math.min(getSize(), start + count);
        for(int i = start; i < end; i++)
            get(i).draw(sb);
    }

    public BaseItem hit(float x, float y)
    {
        final int len = getSize();
        for(int i = 0; i < len; i++) {
            final Layer l = get(len - i - 1); // reverse order
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
