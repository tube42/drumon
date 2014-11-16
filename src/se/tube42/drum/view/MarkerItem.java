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


public class MarkerItem extends SpriteItem
{
    private int last;
    
    public MarkerItem()
    {
        super(World.tex_tiles);        
        
        super.setIndex(TILE_MARKER);
        last = -1;
    }
    
    public void setBeat(int b)
    {
        if(b == last) return;
        
        last = b;
        
        final BaseItem t = World.tiles[b];
        
        setPosition(0.07f, t.getX(), t.getY());
        
        
        set(ITEM_S, 0.9f).configure(0.1f, null)
              .tail(1.1f).configure(0.1f, null)              
              .tail(1f).configure(0.1f, null);
        
        set(ITEM_A, 0.9f).configure(0.1f, null)
              .tail(1f).configure(0.2f, null);
    }     
}
