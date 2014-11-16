
package se.tube42.lib.scene;

import java.util.*;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

import se.tube42.lib.tweeny.*;
import se.tube42.lib.ks.*;

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
        
    private ArrayList<BaseItem> list;    
    public int flags;
    public String name;
    
    public Layer()
    {
        this( new ArrayList<BaseItem>() );
    }
    
    protected Layer(ArrayList<BaseItem> list)
    {
        this.list = list;
        this.flags = FLAG_VISIBLE | FLAG_TOUCHABLE;
        this.name = null;
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
            list.add(b);
        return this;        
    }
    
    public Layer add(BaseItem bi)
    {
        list.add(bi);
        return this;        
    }
    
    public int getSize()
    {
        return list.size();
    }
    
    public BaseItem get(int index)
    {
        return list.get(index);
    }
    
    // -----------------------------------------    
    
    public void setAllImmediate(int index, float value)
    {
        final int len = getSize();        
        for(int i = 0; i < len; i++)
            get(i).setImmediate(index, value);        
    }
    
    public void setAll(int index, float value, 
              float dur, TweenEquation eq)
    {
        final int len = getSize();        
        for(int i = 0; i < len; i++)
            get(i).set(index, value).configure(dur, eq);
    }
    
    // -----------------------------------------    
    public BaseItem getItem(String name)
    {
        final int len = getSize();        
        for(int i = 0; i < len; i++) {
            final BaseItem ni = get(i);
            if(ni != null && name.equals(ni.name))
                return ni;
        }
        return null;
    }
    
    public void update(float dt)
    {
        if( (flags & FLAG_UPDATE) == 0 || (flags & FLAG_VISIBLE) == 0)
            return;
        
        final int len = getSize();        
        for(int i = 0; i < len; i++) {
            final BaseItem ni = get(i);
            if(ni != null)
                ni.update(dt);
        }
        
    }
    
    public void draw(SpriteBatch sb)
    {
        if( (flags & FLAG_VISIBLE) == 0)
            return;
                
        final int len = getSize();        
        for(int i = 0; i < len; i++) {
            final BaseItem ni = get(i);
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
        
        final int len = getSize();        
        for(int i = 0; i < len; i++) {
            final BaseItem ni = get(len - i - 1); // reverse order            
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
        for(BaseItem it : list)
            it.release();
        
        list.clear();
    }
    
    public void remove(BaseItem item)
    {
        item.release();
        list.remove(item);
    }
}
