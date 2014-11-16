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


public class PressItem extends BaseItem
{
    private int tile, icon;
    
    public PressItem(int tile, int icon, int color)
    {
        super(null);
        
        setTile(tile);
        setIcon(icon);
        setColor(color);
    }
    
    // 
        
    public void setActive(boolean v)
    {
        setAlpha(0.3f, v ? 1.0f : 0.5f);
    }
    

    public void mark0()
    {
        final float r = ServiceProvider.getRandom(0.1f, 0.2f);
        
        set(ITEM_S, 1.05f).configure(r, null)
              .tail(1.00f).configure(r, null);
    }
        
    //     
    public void setTile(int tile)
    {
        this.tile = tile;
    }
    
    public void setIcon(int icon)
    {
        this.icon = icon;
    }
    
        
    // 
    
    public void draw(SpriteBatch sb)
    {
        final float a = getAlpha();
        final float s = getScale();
        final float x = getX();
        final float y = getY();
        final float r = getRotation();                
        final float w2 = w / 2;
        final float h2 = h / 2;               
        
        
        // draw tile                
        if(tile != -1) {
            final TextureRegion tr = World.tex_tiles[tile];            
            sb.setColor(cr, cg, cb, a);
            sb.draw(tr,
                    x + 0.5f, y + 0.5f, 
                    w2, h2,
                    w, h, 
                    s, s, r);
        }
        
        // draw icon
        if(icon != -1) {
            final TextureRegion tr = World.tex_icons[icon];            
            final float w4 = w / 4;
            final float h4 = h / 4;
                        
            sb.setColor(1, 1, 1, a);            
            sb.draw(tr,
                    w4 + x + 0.5f, h4 + y + 0.5f, 
                    w4, h4,
                    w2, h2, 
                    s, s, r);            
            
        }

    }
    
}
