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


public class SaveItem extends PadItem
{
    private String data;
    private boolean active;
    
    public SaveItem()
    {        
        super(-1);
        setActive(false);
        setData(null);
        setColor(COLOR_SAVE);
    }
    
    
    // --------------------------------------
        
    public boolean getActive()
    {
        return active;
    }
    
    public void setActive(boolean active)
    {
        this.active = active;
        setTile( active ? TILE_BUTTON1 : TILE_BUTTON0);
    }
    
    public void setData(String data)
    {
        this.data = data;  
        
        setAlpha(data != null ? 1 : ALPHA_INACTIVE);
    }
    
    public String getData()
    {
        return data;
    }        

}
