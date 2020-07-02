package se.tube42.drum.view;

import com.badlogic.gdx.Input.*;

import se.tube42.lib.tweeny.*;
import se.tube42.lib.scene.*;
import se.tube42.lib.item.*;

import se.tube42.drum.data.*;
import se.tube42.drum.logic.*;

import static se.tube42.drum.data.Constants.*;

public class SettingsScene extends Scene
{
    private static final int
        BT_BACKGROUND = 0,
        BT_PAUSE_RESTART = 1,        
        BT_CLOSE = 2,       
        BT_COUNT = 3        
        ;
        
    private Layer layer;
    private ButtonItem [] buttons;

    public SettingsScene()
    {
        super("settings");

        buttons = new ButtonItem[BT_COUNT];
        buttons[BT_BACKGROUND] = new ButtonItem("");
        buttons[BT_PAUSE_RESTART] = new ButtonItem("");
        buttons[BT_CLOSE] = new ButtonItem("Close");
        buttons[BT_CLOSE].setColor(COLOR_BUTTON_CLOSE);

        layer = getLayer(0);        
        layer.add(buttons);
    }

    // ------------------------------------------------

    public void onShow()
    {
        super.onShow();
        update_buttons();   // initial update
        animate(true);        
    }

    public void onHide()
    {
        super.onHide();        
        animate(false);
    }

    private void animate(boolean in_)
    {
        for(int i = 0; i < buttons.length; i++) {
            final ButtonItem bi = buttons[i];
            final float t = ServiceProvider.getRandom(0.3f, 0.5f);
            bi.set(BaseItem.ITEM_A, in_, 0.0f, 1f, t, null);
            bi.set(BaseItem.ITEM_S, in_, 0.8f, 1f, t / 2, TweenEquation.BACK_OUT);
        }
    }
    // ------------------------------------------------

    public void resize(int w, int h)
    {
        int gap = World.size_button / 2;
        int h0 = World.size_tile;        
        int w0 = w > h ? (w / 2 ) - 2 * gap : w - 2 * gap;
        int x0 = gap;
        int y0 = h - h0 -gap;

        for(int i = 0; i < buttons.length; i++) {
            if(i == BT_CLOSE) continue;            
            buttons[i].setSize(w0, h0);
            buttons[i].setPosition(x0, y0);
     
            // all this mess is so we can arrange icons nicely in landscape & portrait
            if( w > h) {
                if((i & 1) == 0) {
                    x0 = w / 2 + gap;
                } else {
                    x0 = gap;
                    y0 -= gap + h0;
                }
            } else {
                y0 -= gap + h0;
            }
        }

        // close button has its own logic
        h0 = World.size_button;        
        w0 = w - 2 * gap;
        buttons[BT_CLOSE].setSize(w0, h0);
		buttons[BT_CLOSE].setPosition(gap, gap);

    }

    // ----------------------------------------------------------
    // button items

    private void update_buttons()
    {
        buttons[BT_BACKGROUND].setText(Settings.bg_play ? "Background play" : "Background pause");     
        buttons[BT_PAUSE_RESTART].setText(Settings.pause_restart ? "Pause/restart" : "Pause/continue");
    }

    private void press_button(int index)
    {
        if(!buttons[index].isActive())
            return;

        buttons[index].animPress();

        switch(index) {
        case BT_BACKGROUND:
            Settings.bg_play = ! Settings.bg_play;
            break;
        case BT_PAUSE_RESTART:
            Settings.pause_restart = !Settings.pause_restart;
            break;
        case BT_CLOSE:
            go_back();
            return;
        }
    
        update_buttons();    
    }

    public void go_back()
    {
        SettingsService.save();
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
        if(down && !drag) {
            BaseItem hit = layer.hit(x, y);
            if(hit != null) {
                for(int i = 0; i < buttons.length; i++) {
                    if(hit == buttons[i]) {
                        press_button(i);
                        return true;
                    }
                }                
            }                
        }
        return false;
    }

}
