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
          BT_DEL = 0,
          BT_CANCEL = 1,
          BT_IMPORT = 2,
          BT_EXPORT = 3,
          BT_LOAD = 4,
          BT_SAVE = 5
          ;


    private Layer layer;
    private SaveItem [] saves;
    private ButtonItem [] buttons;
    private String restore_data, temp_data, clipboard_data;
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

        animate(true);
        
        // initial update
        update_saves();
        update_buttons();
        update_clipboard();
    }


    public void onHide()
    {
        super.onHide();
        animate(false);
    }

    
    public void onResume() 
    { 
        update_clipboard();
    }

    private void animate(boolean in_)
    {
        // in animation:
        for (final SaveItem si : saves) {
            final float t = ServiceProvider.getRandom(0.3f, 0.4f);
            si.set(BaseItem.ITEM_Y, in_, World.sh, si.y2, t, TweenEquation.BACK_OUT);
            si.setImmediate(BaseItem.ITEM_X, si.x2);
        }

        for (final ButtonItem bi : buttons) {
            final float t = ServiceProvider.getRandom(0.4f, 0.5f);
            bi.set(BaseItem.ITEM_A, in_, 0.0f, 1f, t / 2, null);
            bi.set(BaseItem.ITEM_Y, in_, -bi.getH(), bi.y2, t, TweenEquation.BACK_OUT);
            bi.setImmediate(BaseItem.ITEM_X, bi.x2);
        }
    }
    // ------------------------------------------------

    public void resize(int w, int h)
    {
        // 4x4
        for(int i = 0; i < NUM_SAVES; i++) {
            final int x = i & 3;
            final int y = i >> 2;
            final SaveItem si = saves[i];
            final float t = ServiceProvider.getRandom(0.2f, 0.4f);
            si.setSize(World.size_pad1, World.size_pad1);
            si.setPosition(t,
                      si.x2 = World.x0_pad1 + World.stripe_pad1 * x,
                      si.y2 = World.y0_pad1 + World.stripe_pad1 * y);
        }

        // 2x3
        final int y0 = World.y0_tile + World.stripe_tile / 2;
        final int gap = Math.max( 2, World.size_tile / 8);
        for(int i = 0; i < buttons.length; i++) {
            final int x = i & 1;
            final int y = i >> 1;
            final ButtonItem bi = buttons[i];
            final float t = ServiceProvider.getRandom(0.2f, 0.4f);
            bi.setSize(World.size_tile * 2 - gap, World.size_tile - gap);
            bi.setPosition(t,
                      bi.x2 = World.x0_tile  + gap / 2 + World.stripe_tile * x * 2,
                      bi.y2 = gap / 2 + y0 + World.stripe_tile * y);
        }
    }
    
    // ------------------------------------------------
    
    // is any text available in the clipboard and is it a valid save?
    private void update_clipboard()
    {
        final SystemService sys = SystemService.getInstance();
        
        clipboard_data = sys.getClipboard();
        if(clipboard_data != null) {
            clipboard_data = clipboard_data.trim();
            if(!SaveService.isValidSave(clipboard_data)) {
                clipboard_data = null;
            }
        }
        update_buttons();
    }
    
    private void export_to_clipboard(String str)
    {
        final SystemService sys = SystemService.getInstance();
        
        if(sys.setClipboard(str)) {
            sys.showMessage("Data was copied to clipboard. You can now email it to your friends and enemies...");
        } else {
            sys.showMessage("Could not copy data to clipboard");
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
        buttons[BT_SAVE].setColor(temp_data != null
                  ? COLOR_BUTTON_WARN : COLOR_BUTTON); // overwriting!
        
        buttons[BT_IMPORT].setActive(clipboard_data != null);
        buttons[BT_EXPORT].setActive(true);
        
        // not implemented:
        buttons[BT_DEL].setActive(false);

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
            
        case BT_IMPORT:
            if(clipboard_data != null) {
                restore_data = clipboard_data;
                go_back();
            }
            break;
            
        case BT_EXPORT:
            export_to_clipboard(restore_data);
            update_clipboard(); // for testing...
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


    public boolean touch(int p, int x, int y, boolean down, boolean drag)
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
