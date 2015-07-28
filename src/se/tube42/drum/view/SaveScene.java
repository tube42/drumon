package se.tube42.drum.view;


import com.badlogic.gdx.Input.*;


import se.tube42.lib.tweeny.*;
import se.tube42.lib.scene.*;
import se.tube42.lib.item.*;

import se.tube42.drum.data.*;
import se.tube42.drum.logic.*;

import static se.tube42.drum.data.Constants.*;

public class SaveScene extends Scene
{
    private static final int
          BT_LOAD = 0,
          BT_SAVE = 1,
          BT_IMPORT = 2,
          BT_EXPORT = 3,
          BT_DEL = 4,
          BT_CANCEL = 5
          ;
          
          
    private Layer layer;
    private SaveItem [] saves;
    private ButtonItem [] buttons;    
    private String restore_data, temp_data;
    private int curr_save;
    
    public SaveScene()
    {
        super("save");
        
        saves = new SaveItem[NUM_SAVES];
        for(int i = 0; i < saves.length; i++)
            saves[i] = new SaveItem();
        
        buttons = new ButtonItem[6];
        buttons[BT_LOAD] = new ButtonItem("Load");
        buttons[BT_SAVE] = new ButtonItem("Save");
        buttons[BT_IMPORT] = new ButtonItem("Import");
        buttons[BT_EXPORT] = new ButtonItem("Export");
        buttons[BT_DEL] = new ButtonItem("Delete");        
        buttons[BT_CANCEL] = new ButtonItem("Cancel");
                
        
        layer = getLayer(0);
        layer.add(saves);
        layer.add(buttons);
    }

    // ------------------------------------------------

    public void onShow()
    {
        super.onShow();
        
        // save current data & cleat temp
        restore_data = SaveService.currentToString();
        temp_data = null;
        
        // no save selected for now
        curr_save = -1;        
        
        // set current saves
        for(int i = 0; i < saves.length; i++) {
            saves[i].setData( SaveService.getSave(i));
        }
        
        // in animation: 
        for(int i = 0; i < saves.length; i++) {
            final float t = ServiceProvider.getRandom(0.2f, 0.5f);
            saves[i].set(BaseItem.ITEM_S, 0.2f, 1f).configure(t, TweenEquation.BACK_OUT);
        }
        
        for(int i = 0; i < buttons.length; i++) {
            final float t = ServiceProvider.getRandom(0.2f, 0.5f);            
            buttons[i].set(BaseItem.ITEM_S, 0.8f, 1f).configure(t, TweenEquation.BACK_OUT);
        }
        
        // initial update
        update_saves();
        update_buttons();
    }


    public void onHide()
    {
        super.onHide();
        
    }

    
    // ------------------------------------------------

    public void resize(int w, int h)
    {
        final int size = World.tile_size;
        final int stripe = World.tile_stripe;
        final int x0 = World.tile_x0;
        int x, y;
        
        // position saves
        y = h - World.tile_y0 - stripe; // same gap as bottom, but from top
        x = x0;              
        for(int i = 0; i < NUM_SAVES; i++) {
            if( i != 0 && (i & 3) == 0) {
                y -= stripe;
                x = x0;
            }                      
            saves[i].setSize(size, size);
            saves[i].setPosition(x, y);
            
            x += stripe;
        }
        
        // position buttons
        y = World.tile_y0 + 2 * stripe;
        x = x0;              
        for(int i = 0; i < buttons.length; i++) {
            if( i != 0 && (i & 1) == 0) {
                y -= stripe;
                x = x0;
            }
            buttons[i].setSize(size + stripe, size);
            buttons[i].setPosition(x, y);
            x += stripe * 2;
        }
    }

  
  
    // ----------------------------------------------------------
    // save items
    
    private void update_saves()
    {
        for(int i = 0; i < saves.length; i++) {
            saves[i].setActive( i == curr_save);
        }                
    }
    
    private void press_save(int index)
    {       
        curr_save = index;
        saves[index].mark0();
        
        // temp load song if it has any
        temp_data = SaveService.getSave(index);
        if(temp_data != null)
            if(!SaveService.stringToCurrent(temp_data))
                temp_data = null; // cant load this?
        
        update_saves();
        update_buttons();
    }
    
    
    // ----------------------------------------------------------
    // button items
    
    private void update_buttons()
    {
        buttons[BT_LOAD].setActive(temp_data != null);
        buttons[BT_SAVE].setActive(curr_save != -1);        
        buttons[BT_SAVE].setColor(temp_data != null ? 0xFF8080 : 0xFFFFFF); // overwriting!
        
        // not implemented:
        buttons[BT_DEL].setActive(false);
        buttons[BT_IMPORT].setActive(false);
        buttons[BT_EXPORT].setActive(false);
        
    }
    
    private void press_button(int index)
    {
        if(!buttons[index].isActive())
            return;
        
        
        buttons[index].mark0();
        
        switch(index) {
        case BT_SAVE:
            if(curr_save != -1) {
                SaveService.setSave(curr_save, restore_data);
                go_back();
            }
            break;
        case BT_LOAD:
            if(temp_data != null) {
                restore_data = temp_data;
                go_back();                
            }
            break;
            
        case BT_CANCEL:            
            go_back();
            break;
        }
        
        update_buttons();
    }
    
    private void press(BaseItem bi) 
    {
        
        for(int i = 0; i < saves.length; i++) {
            if(bi == saves[i]) {
                press_save(i);
                return;
            }
        }
        
        for(int i = 0; i < buttons.length; i++) {
            if(bi == buttons[i]) {
                press_button(i);
                return;
            }
        }
        
        
    }
    // ----------------------------------------------------------

    public void go_back()
    {
        // restore data before going back
        SaveService.stringToCurrent(restore_data);
        
        World.mgr.setScene(World.scene_drum, 200);
    }

    public boolean type(int key, boolean down)
    {
        if(down) {
            if(key == Keys.BACK || key == Keys.ESCAPE) {
                go_back();
                return true;
            }
        }
        return false;
    }


    public boolean touch(int x, int y, boolean down, boolean drag)
    {
        // TODO
        if(down && !drag) {
            BaseItem hit = layer.hit(x, y);
            if(hit != null)
                press(hit);            
        } else if(!down) {
            
        }

        return true;
    }

}
