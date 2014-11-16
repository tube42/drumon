package se.tube42.drum.view;

import com.badlogic.gdx.*;
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

public class DrumScene extends Scene
{
    private Layer layer_tiles;
    private BaseText item_msg;
    private int last_hit, first_hit;    
    private int mode;
    
    public DrumScene()
    {
        super("drum");
        
        ServiceProvider.setColorItem(COLOR_BG, World.bgc, 
                  0f, 1f, 2f);
        
        // PADS
        World.tile_pads = new PadItem[PADS];     
        for(int i = 0; i < PADS; i++) {
            World.tile_pads[i] = new PadItem(TILE_PAD0, COLOR_PADS);
        }
        
        // VOICES
        World.tile_voices = new VoiceItem[VOICES];
        for(int i = 0; i < VOICES; i++) {
            World.tile_voices[i] = new VoiceItem(TILE_BUTTON0, 
                      VOICE_ICONS[i], COLOR_VOICES );
        }
        
        // tools
        World.tile_tools = new PressItem[TOOLS];
        for(int i = 0; i < TOOLS; i++) {
            World.tile_tools[i] = new PressItem(TILE_BUTTON0, 0, 0);
        }
        
        // selectors
        World.tile_selectors = new PressItem[SELECTORS];
        for(int i = 0; i < SELECTORS; i++) {
            World.tile_selectors[i] = new PressItem(TILE_BUTTON0,
                      SELECTOR_ICONS[i], COLOR_SELECTORS[i]
                      );
        }
        
        // put them all into Worl.tiles
        World.tiles = new BaseItem[PADS + VOICES + TOOLS + SELECTORS];
        int index = 0;
        for(BaseItem bi : World.tile_pads) World.tiles[index++] = bi;
        for(BaseItem bi : World.tile_voices) World.tiles[index++] = bi;
        for(BaseItem bi : World.tile_tools) World.tiles[index++] = bi;
        for(BaseItem bi : World.tile_selectors) World.tiles[index++] = bi;
        
        
        layer_tiles = getLayer(1);
        layer_tiles.add(World.tiles);
        
        World.marker = new MarkerItem();
        World.marker.setColor(COLOR_MARKER);
        getLayer(2).add(World.marker);
        
        item_msg = new BaseText(World.font);
        item_msg.setAlignment(-0.5f, -0.5f);
        getLayer(2).add(item_msg);
        
        // init
        sel_set_mode(0);
        voice_set_voice(1);
        voice_set_voice(0);
        voice_tile_update_all();        
        msg_show("", 0, 0);
    }
    
    // ------------------------------------------------
    private void msg_show(String str, int sx, int sy)
    {
        final int x0 = World.sw / 2;
        final int y0 = World.sh / 2;
        final int dx = sx * World.sw / 4;
        final int dy = sy * World.sh / 4;
        
        item_msg.setText(str);
        item_msg.setImmediate(BaseItem.ITEM_Y, y0);
        
        item_msg.set(BaseItem.ITEM_X, x0 + dx, x0).configure(0.2f, null)
              .pause(1)
              .tail(x0 - dx).configure(0.2f, null);
        
        item_msg.set(BaseItem.ITEM_Y, y0 + dy, y0).configure(0.2f, null)
              .pause(1)
              .tail(y0 - dy).configure(0.2f, null);
        
        item_msg.set(BaseItem.ITEM_A, 0, 1).configure(0.2f, null)
              .pause(1)              
              .tail(0).configure(0.2f, null);
    }
    // ------------------------------------------------
    
    private void all_update()
    {        
        voice_tile_update_all();        
        tools_update_all();        
        voice_tile_update_all();        
    }
    
    private void sel_set_mode(int mode)
    {
        this.mode = mode;
        
        final int color = COLOR_SELECTORS[mode];
        for(int i = 0; i < TOOLS; i++) 
            World.tile_tools[i].setColor(color);
        
        for(int i = 0; i < SELECTORS; i++) 
            World.tile_selectors[ i].setActive(i == mode);
        
        tools_update_all();
    }
    
    private void tools_update_all()
    {
        final int voice = World.seq.getVoice();
        
        boolean v0, v1, v2, v3;
        int t0, t1, t2, t3, i0, i1, i2, i3;
        
        v0 = v1 = v2 = v3 = false;
        t0 = t1 = t2 = t3 = TILE_BUTTON0;
        
        i0 = TOOL_ICONS[SELECTORS * mode + 0];
        i1 = TOOL_ICONS[SELECTORS * mode + 1];
        i2 = TOOL_ICONS[SELECTORS * mode + 2];
        i3 = TOOL_ICONS[SELECTORS * mode + 3];
        
        
        switch(mode) {
        case 0:            
            //            v2 = World.seq.getTempoMultiplier() == 1;
            //            v3 = World.seq.getTempoMultiplier() != 1;            
            
            if(World.seq.getTempoMultiplier() == 4) {
                i3 = ICON_NOTE16;
            } else if(World.seq.getTempoMultiplier() == 2) {
                i3 = ICON_NOTE8;                
            } else {
                i3 = ICON_NOTE4;                
            }
            break;
        case 1:
            i0 = World.seq.isPaused() ? ICON_PLAY : ICON_PAUSE;
            i1 = World.seq.getBank(voice) == 0 ? ICON_A : ICON_B;
            break;
        case 2:
            i0 = World.seq.getSample(voice) == 0 ? ICON_A : ICON_B;
            v1 = World.chain.isEnabled(0);
            v2 = World.chain.isEnabled(1);
            v3 = World.chain.isEnabled(2);
            break;
        case 3:
            break;
        }
        
        
        World.tile_tools[0].setIcon(i0);
        World.tile_tools[1].setIcon(i1);
        World.tile_tools[2].setIcon(i2);
        World.tile_tools[3].setIcon(i3);
        
        World.tile_tools[0].setTile(v0 ? TILE_BUTTON1 : TILE_BUTTON0);        
        World.tile_tools[1].setTile(v1 ? TILE_BUTTON1 : TILE_BUTTON0);
        World.tile_tools[2].setTile(v2 ? TILE_BUTTON1 : TILE_BUTTON0);
        World.tile_tools[3].setTile(v3 ? TILE_BUTTON1 : TILE_BUTTON0);
        
    }
    
    private void voice_alt_toggle(int voice)
    {        
        World.seq.setSample(voice, 1 ^ World.seq.getSample(voice));
        World.tile_voices[voice].setTile(
                  World.seq.getSample(voice) == 0 
                  ? TILE_BUTTON0 : TILE_BUTTON0_ALT);             
    }
    
    private void voice_set_voice(int voice)
    {
        final int old_voice = World.seq.getVoice();
        if(old_voice == voice) return;
        
        World.tile_voices[voice].mark0();
        
        World.seq.setVoice(voice);
        
        for(int i = 0; i < VOICES ; i++) {
            World.tile_voices[i].setAlpha(i == voice ? 1f : 0.4f);
        }
        
        /*
        for(int i = 0; i < PADS; i++)         
            World.tile_pads[i].setColorIndex( col_index);
        
           ServiceProvider.setColor(col_rgb, World.marker, 1f, -1f);
         */
        voice_tile_update_all();        
    }
    
    private void voice_tile_toggle(int tile)
    {
        final int voice =  World.seq.getVoice();
        World.seq.set(voice, tile, World.seq.get(voice, tile) ^ 1);
        World.tile_pads[tile].mark0();
        voice_tile_update(tile);
    }
    
    private void voice_tile_update(int tile)
    {
        final int voice =  World.seq.getVoice();        
        World.tile_pads[tile].setTile( World.seq.get(voice, tile) );
    }
    
    private void voice_tile_update_all()
    {
        for(int i = 0; i < PADS; i++) 
            voice_tile_update(i);
    }
    
    // ------------------------------------------------
    
    private void button_sound_select(int id)
    {
        voice_set_voice(id);
        all_update();
    }
    
    private void button_selector_select(int id)
    {
        sel_set_mode(id);
        all_update();        
    }
    
    private void button_tool_select(int id)
    { 
        final int voice = World.seq.getVoice();        
        final int op = TOOLS * mode + id;
        
        World.tile_tools[id].mark0();
        
        switch(op) {            
            // mode = 0, timing
        case 0:            
            World.seq.setTempo( World.seq.getTempo() - 2);
            msg_show("" + World.seq.getTempo(), -1, 0);            
            break;                            
        case 1:
            if(World.td.add()) {
                World.seq.setTempo( World.td.get() );
                msg_show("" + World.td.get(), 0, -1);
            }
            break;
        case 2:
            World.seq.setTempo( World.seq.getTempo() + 2);
            msg_show("" + World.seq.getTempo(), +1, 0);
            break;
            
        case 3:
            int n = World.seq.getTempoMultiplier() * 2;
            if(n > 4) n = 1;
            World.seq.setTempoMultiplier(n);
            break;
            
            // mode = 1, sequence
        case 4:
            World.seq.setPause(! World.seq.isPaused());
            break;
            
        case 5:
            World.seq.setBank(voice, 1 ^ World.seq.getBank(voice));
            break;
            
        case 6:
            // shuffle:
            for(int i = 0; i < PADS * 5; i++) {
                int a = ServiceProvider.getRandomInt(PADS);
                int b = ServiceProvider.getRandomInt(PADS);
                
                if(World.seq.get(voice, a) != 0 && 
                   World.seq.get(voice, b) == 0) {
                    voice_tile_toggle(a);
                    voice_tile_toggle(b);
                }
            }
            break;
            
        case 7:
            // clear one
            for(int i = 0; i < PADS; i++)
                World.seq.set(voice, i, 0);
            break;
            
            // mode = 2, waveform
        case 8:
            voice_alt_toggle(voice);            
            break;
            
        case 9:
            World.chain.toggle(0);
            break;
            
        case 10:
            World.chain.toggle(1);
            break;
            
        case 11:
            World.chain.toggle(2);
            break;
            
            // mode = 3, settings
        }
        
        all_update();
    }
    
    // ------------------------------------------------
    
    public void resize(int w, int h)
    {
    	super.resize(w, h);
        
        for(int y = 0; y < 8; y++) {     
            int y0 = World.tile_y0 + World.tile_stripe * (7-y);
            if(y < 4)
                y0 += World.tile_y0 / 4;
            else
                y0 -= World.tile_y0 / 4;
            
            for(int x = 0; x < 4; x++) {
                final int index = x + y * 4;
                World.tiles[index].setSize(World.tile_size, World.tile_size);
                World.tiles[index].setPosition(
                          World.tile_x0 + World.tile_stripe * x, y0);
            }
        }
        
        World.marker.setSize(World.tile_size, World.tile_size);
    }
    
    
    // ----------------------------------------------------------
    
    public void onUpdate(float dt)
    {
        // update the marker
        int beat = World.seq.getBeat();        
        World.marker.setBeat(beat);
        
        for(int i = 0; i < VOICES; i++) {
            if(World.seq.checkStarted(i)) {
                World.tile_voices[i].mark1();
            }
        }
    }
    
    public boolean type(int key, boolean down)
    {
        if (key == Keys.BACK || key == Keys.ESCAPE) {
            // TODO
            Gdx.app.exit();            
            return true;
        }        
        return false;
    }
    
    
    public boolean touch(int x, int y, boolean down, boolean drag)
    {                       
        int idx = get_item_at(x, y);
        if(down && !drag) {
            last_hit = -1;
            first_hit = idx;
        }
        if(idx == -1) return false;
        
        if(last_hit != idx) {
            last_hit = idx;
            
            if(idx < PADS) {
                voice_tile_toggle(idx);
            }
        }
        
        if(!down && idx == first_hit) {
            final int i1 = idx - PADS;
            final int i2 = i1 - VOICES;
            final int i3 = i2 - TOOLS;
            
            if(i1 >= 0 && i1 < VOICES) 
                button_sound_select(i1);
            else if(i2 >= 0 && i2 < TOOLS) 
                button_tool_select(i2);
            else if(i3 >= 0 && i3 < SELECTORS) 
                button_selector_select(i3);
            
        }
        
        return true;
    }
    
    // --------------------------------------------------
    private int get_item_at(int x, int y)
    {
        for(int i = 0; i < World.tiles.length; i++) {
            if(World.tiles[i].hit(x, y))
                return i;               
        }
        return -1;
    }
    
    
}
