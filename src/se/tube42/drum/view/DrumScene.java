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
import se.tube42.lib.service.*;

import se.tube42.drum.data.*;
import se.tube42.drum.audio.*;
import se.tube42.drum.logic.*;

import static se.tube42.drum.data.Constants.*;

public class DrumScene extends Scene implements SequencerListener
{
    private final static int MAX_TOUCH = 10;
    private int []last_hit = new int[MAX_TOUCH];
    private int []first_hit = new int[MAX_TOUCH];

    // long press
    private long last_time;
    private int last_index;

    private Layer layer_tiles;
    private BaseText item_msg;
    private int mode;
    private boolean first;
    private volatile int mb_beat, mb_sample; // seq state posted from the other thread

    public DrumScene()
    {
        super("drum");

		World.seq.setListener(this);

        ServiceProvider.setColorItem(COLOR_BG, World.bgc, 0f, 1f, 2f);

        // PADS
        World.tile_pads = new PadItem[PADS];
        for(int i = 0; i < PADS; i++) {
            World.tile_pads[i] = new PadItem(TILE_PAD0);
        }

        // VOICES
        World.tile_voices = new VoiceItem[VOICES];
        for(int i = 0; i < VOICES; i++) {
            World.tile_voices[i] = new VoiceItem(VOICE_ICONS[i], COLOR_VOICES );
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
        World.marker.flags &= ~BaseItem.FLAG_VISIBLE;
        getLayer(2).add(World.marker);

        item_msg = new BaseText(World.font1);
        item_msg.setAlignment(-0.5f, -0.5f);
        getLayer(2).add(item_msg);


        // if available, load the latest saved program
        ServiceProvider.autoLoad();

        // first time init
        this.first = true; // to signal onShow()
        select_mode(0, true);
        select_sound(0, true);
        msg_show("", 0, 0);
        update(true, true, true, true);

    }


    public void onShow()
    {
        this.mb_beat = -1;
        this.mb_sample = 0;
        this.last_time = -1;

        if(first) {
            first = false;
            reposition(true);

            // set beat to 0 on the first screen
            World.marker.setBeat(0);
        } else {
            reposition(false);
            for(int i = 0; i < World.tiles.length; i++) {
                final float t = ServiceProvider.getRandom(0.2f, 0.3f);
                World.tiles[i].set(BaseItem.ITEM_A, 0, 1).configure(t, null);
            }

            // update beat right away, dont wait until the next one
            World.marker.setBeat( World.seq.getBeat() );
        }

        // this is needed since the animation code above has removed or alpha change:
        select_sound(World.prog.getVoice(), true);


    }

    public void onHide()
    {
        for(int i = 0; i < World.tiles.length; i++) {
            final float t = ServiceProvider.getRandom(0.2f, 0.3f);
            World.tiles[i].set(BaseItem.ITEM_A, 1, 0).configure(t, null);
        }
    }

    private void reposition(boolean animate)
    {
        System.out.println("POSITION " + animate);
        final int w = World.sw;
        final int h = World.sh;
        final int flags = World.prog.getFlags();
        
        // position pads:        
        if( (flags & FLAG_48) != 0) {
            // 4 / 8
            World.marker.setSize(World.size_pad2, World.size_pad2);
            
            for(int y = 0; y < 4; y++) {
                for(int x = 0; x < 8; x++) {
                    final PadItem pad = World.tile_pads[x + y * 8];
                    pad.flags |= BaseItem.FLAG_VISIBLE;
                    pad.setSize(World.size_pad2, World.size_pad2);                    
                    pad.x2 = World.x0_pad2 + World.stripe_pad2_x * x;
                    pad.y2 = World.y0_pad2 + World.stripe_pad2_y * (3 - y);
                }
            }            
        } else {
            // 4 / 4
            World.marker.setSize(World.size_pad1, World.size_pad1);
            
            for(int y = 0; y < 4; y++) {
                for(int x = 0; x < 8; x++) {
                    final PadItem pad = World.tile_pads[x + y * 8];
                    if( (x & 1) != 0)
                        pad.flags &= ~BaseItem.FLAG_VISIBLE;                   
                    pad.setSize(World.size_pad1, World.size_pad1);
                    pad.x2 = World.x0_pad1 + World.stripe_pad1 * (x / 2);
                    pad.y2 = World.y0_pad1 + World.stripe_pad1 * (3 - y);
                }
            }
        }
        
        // position the rest
        for(int y = 0; y < 4; y++) {
            for(int x = 0; x < 4; x++) {
                final BaseItem bi = World.tiles[PADS + x + y * 4];
                bi.setSize(World.size_tile, World.size_tile);                
                bi.x2 = World.x0_tile + World.stripe_tile * x;
                bi.y2 = World.y0_tile + World.stripe_tile * (3 - y);
            }
        }
        
        // animate entering the screen, or just animate to position
        for(int i = 0; i < World.tiles.length; i++) {
            final BaseItem bi = World.tiles[i];
            final float x1 = bi.x2;
            final float y1 = bi.y2;
            
            if(animate) {
                final float x0 = x1 + (x1 < World.sw / 2 ? -World.sw : World.sw);
                final float y0 = x1 + (y1 < World.sh / 2 ? -World.sh : World.sh); 
                final float p = 0.8f + (8-i / 4) * 0.05f;
                final float t = ServiceProvider.getRandom(0.35f, 0.5f);
                
                bi.pause(BaseItem.ITEM_X, x0, p)
                      .tail(x1).configure(t, TweenEquation.QUAD_OUT);
                bi.pause(BaseItem.ITEM_Y, y0, p)
                      .tail(y1).configure(t, TweenEquation.QUAD_OUT);
            } else {
                final float t = ServiceProvider.getRandom(0.35f, 0.5f);
                bi.setPosition(t,x1, y1);
            }
        }
    }

    // ------------------------------------------------

    private void msg_show(String str, int sx, int sy)
    {
        final int x0 = World.sw / 2;
        final int y0 = World.sh * 2 / 3;
        final int dx = sx * World.sw / 4;
        final int dy = sy * World.sh / 3;

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
    // PADS
    private void update_pad(int pad)
    {
        final int voice =  World.prog.getVoice();
        World.tile_pads[pad].setTile( World.prog.get(voice, pad)
                  ? TILE_PAD1 : TILE_PAD0 );
    }

    private void update_pads()
    {
        for(int i = 0; i < PADS; i++)
            update_pad(i);
    }

    private void clear_pads(int voice)
    {
        for(int i = 0; i < PADS; i++)
            World.prog.set(voice, i, false);
    }

    private void shuffle_pads(int voice)
    {
        for(int i = 0; i < PADS * 5; i++) {
            int a = ServiceProvider.getRandomInt(PADS);
            int b = ServiceProvider.getRandomInt(PADS);
            if(World.prog.get(voice, a) && !World.prog.get(voice, b)) {
                select_pad(voice, a);
                select_pad(voice, b);
            }
        }
    }

    private void select_pad(int voice, int pad)
    {
        World.prog.set(voice, pad, !World.prog.get(voice, pad));
        World.tile_pads[pad].mark0();
        if(voice == World.prog.getVoice())
            update_pad(pad);
    }

    private void longpress_pad(int pad)
    {
        // empty for now
    }

    // ------------------------------------------------
    // SOUNDS

    private void update_sounds()
    {
        for(int i = 0; i < VOICES; i++) {
            World.tile_voices[i].setVariant(
                      World.prog.getSampleVariant(i),
                      World.prog.getBank(i) );
        }
    }

    private void select_sound(int voice, boolean force)
    {
        final int old_voice = World.prog.getVoice();

        if(!force && old_voice == voice) {
            final int max = World.sounds[voice].getNumOfVariants();
            int next = 1 + World.prog.getSampleVariant(voice);
            if(next >= max) next = 0;
            World.prog.setSampleVariant(voice, next);
        } else {
            World.prog.setVoice(voice);
            for(int i = 0; i < VOICES ; i++)
                World.tile_voices[i].setAlpha(i == voice ? 1 : ALPHA_INACTIVE);

            final int c = COLOR_PADS[voice];
            ServiceProvider.setColorItem(c, World.bgc, 0f, 0.4f, 0.7f);

            for(int i = 0; i < PADS; i++)
                World.tile_pads[i].setColor(c);
        }

        World.tile_voices[voice].mark0();
        update(false, true, false, false);
    }

    private void longpress_sound(int voice)
    {
        // empty for now
    }

    // ------------------------------------------------
    // TOOLS

    private void update_tools(boolean modechange)
    {
        final Sequencer seq = World.seq;
        final Program prog = World.prog;
        final int voice = prog.getVoice();

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
            if(prog.getTempoMultiplier() == 4) {
                i0 = ICON_NOTE16;
            } else if(prog.getTempoMultiplier() == 2) {
                i0 = ICON_NOTE8;
            } else {
                i0 = ICON_NOTE4;
            }
            break;
        case 1:
            i0 = seq.isPaused() ? ICON_PLAY : ICON_PAUSE;
            i1 = prog.getBank(voice) ? ICON_B : ICON_A;
            i3 = (prog.getFlags() & FLAG_48) == 0 ? ICON_44 : ICON_48;
            break;
        case 2:
            v0 = World.mixer.getEffectChain().isEnabled(0);
            v1 = World.mixer.getEffectChain().isEnabled(1);
            v2 = World.mixer.getEffectChain().isEnabled(2);
            v3 = World.mixer.getEffectChain().isEnabled(3);
            break;
        case 3:
            break;
        }

        final int color = COLOR_SELECTORS[mode];
        World.tile_tools[0].change(color, i0, v0, modechange);
        World.tile_tools[1].change(color, i1, v1, modechange);
        World.tile_tools[2].change(color, i2, v2, modechange);
        World.tile_tools[3].change(color, i3, v3, modechange);
    }

    private void select_tool(int id)
    {
        final int voice = World.prog.getVoice();
        final int op = TOOLS * mode + id;
        
        World.tile_tools[id].mark0();

        switch(op) {
        case TOOL_TEMPO_MUL:
            int n = World.prog.getTempoMultiplier() * 2;
            if(n > 4) n = 1;
            World.prog.setTempoMultiplier(n);
            break;

        case TOOL_TEMPO_DETECT:
            if(World.td.add()) {
                World.prog.setTempo( World.td.get() );
                msg_show("" + World.td.get(), 0, -1);
            }
            break;

        case TOOL_TEMPO_SET:
            get_choice(World.prog, CHOICE_TEMPO, 0);
            break;

        case TOOL_SEQ_PAUSE:
            World.seq.setPause(! World.seq.isPaused());
            break;

        case TOOL_SEQ_AB:
            World.prog.setBank(voice, !World.prog.getBank(voice));
            break;

        case TOOL_SEQ_SHUFFLE:
            shuffle_pads(voice);
            break;

        case TOOL_SEQ_44_48:
            World.prog.setFlags(World.prog.getFlags() ^ FLAG_48);
            update(false, false, true, false);
            reposition(false);
            return;

        case TOOL_FX_LOFI:
            World.mixer.getEffectChain().toggle(0);
            break;

        case TOOL_FX_LOWPASS:
            World.mixer.getEffectChain().toggle(1);
            break;

        case TOOL_FX_ECHO:
            World.mixer.getEffectChain().toggle(2);
            break;

        case TOOL_FX_COMP:
            World.mixer.getEffectChain().toggle(3);
            break;

        case TOOL_MISC_VOL:
            get_choice2(World.prog, CHOICE2_VOLUME, voice);
            break;
        case TOOL_MISC_COMP:
            get_choice2(World.mixer.getEffectChain().getEffect(FX_COMP),
                      CHOICE2_COMPRESS, -1);
            break;
        case TOOL_MISC_CLEAR:
            clear_pads(voice);
            break;
        case TOOL_MISC_SAVE:
            World.mgr.setScene(World.scene_save, 120);
            break;
        }

        update(false, false, true, false);
    }

    private void longpress_tool(int id)
    {
        final int op = TOOLS * mode + id;

        World.tile_tools[id].mark0();

        switch(op) {
        case TOOL_TEMPO_DETECT:
            World.prog.setTempo( 120 );
            msg_show("120", 0, -1);
            break;

        case TOOL_SEQ_SHUFFLE:
            for(int i = 0; i < VOICES; i++)
                shuffle_pads(i);
            break;

        case TOOL_MISC_CLEAR:
            for(int i = 0; i < VOICES; i++)
                clear_pads(i);
            break;
        }

        update(true, true, true, false);
    }

    // ------------------------------------------------
    // MODE

    private void update_mode()
    {
        for(int i = 0; i < SELECTORS; i++)
            World.tile_selectors[ i].setActive(i == this.mode);
    }

    private void select_mode(int mode, boolean force)
    {
        if(force || this.mode != mode) {
            this.mode = mode;
            update(false, false, false, true);
        }
    }

    private void longpress_mode(int mode)
    {
        // empty for now
    }

    // ------------------------------------------------
    // ALL
    private void update(boolean pads, boolean sounds,
              boolean tools, boolean mode)
    {
        if(pads || sounds || tools) update_pads();
        if(sounds || tools) update_sounds();
        if(tools || sounds || mode) update_tools(mode);
        if(mode) update_mode();
    }

    private void onLongPress(int idx)
    {
        final int i1 = idx - PADS;
        final int i2 = i1 - VOICES;
        final int i3 = i2 - TOOLS;

        if(idx >= 0 && idx < PADS)
            longpress_pad(idx);
        else if(i1 >= 0 && i1 < VOICES)
            longpress_sound(i1);
        else if(i2 >= 0 && i2 < TOOLS)
            longpress_tool(i2);
        else if(i3 >= 0 && i3 < SELECTORS)
            longpress_mode(i3);
    }

    // ------------------------------------------------
    // Choices

    private void get_choice(Object target, int choice, int id)
    {
        World.scene_choice.setChoice(target, choice, id);
        World.mgr.setScene(World.scene_choice, 120);
    }

    private void get_choice2(Object target, int choice, int id)
    {
        World.scene_choice2.setChoice(target, choice, id);
        World.mgr.setScene(World.scene_choice2, 120);
    }



    // ------------------------------------------------
    // SequencerListener interface:
    //
    // to avoid multi-thread madness we will just copy it variables in audio
    // thread and handle them in our own thread
    
    
    public void onBeatStart(int beat)
    {
        mb_beat = beat;
    }
    
    public void onSampleStart(int beat, int sample)
    {
        mb_sample |= 1 << sample;
    }
    
    public void onUpdate(float dt)
    {
        // detect long press
        if(last_time != -1) {
            long now = System.currentTimeMillis();
            if(now - last_time > LONGPRESS_DELAY) {
                last_time = -1;
                onLongPress(last_index);
            }
        }

        // beat update
    	if(mb_beat != -1) {
            // copy it and reset the source
            final int beat = mb_beat;
            final int samples = mb_sample;
            mb_beat = -1;
            mb_sample = 0;
            
            // udpate beat marker
            World.marker.setBeat(beat);
            World.marker.flags |= BaseItem.FLAG_VISIBLE;
            
            // mark played pad
            if(samples != 0) {
                final int mask = 1 << World.prog.getVoice();
                World.tile_pads[beat].mark1((samples & mask) == 0 ? 1.1f : 1.2f);
            }
            
            // mark played sound
            for(int i = 0; i < VOICES; i++) {
                if( (samples & (1 << i)) != 0)
                    World.tile_voices[i].mark1();
            }
        }
    }


	// ----------------------------------------------------------

    public void resize(int w, int h)
    {
    	super.resize(w, h);        
        reposition(false);
    }

    public boolean type(int key, boolean down)
    {
        if(down) {
            if (key == Keys.BACK || key == Keys.ESCAPE) {
                Gdx.app.exit();
                return true;
            }
        }

        return false;
    }


    public boolean touch(int p, int x, int y, boolean down, boolean drag)
    {
        /* multi-touch limit */
        if(p < 0 || p >= MAX_TOUCH)
            return false;

        int idx = get_tile_at(x, y);
        if(down && !drag) {
            last_hit[p] = -1;
            first_hit[p] = idx;
        }
        if(idx == -1) {
            last_time = -1;
            return false;
        }

        if(down && !drag) {
            last_time = System.currentTimeMillis();
            last_index = idx;
        } else if(!down) {
            last_time = -1;
        }

        if(last_hit[p] != idx) {
            last_hit[p] = idx;

            if(idx < PADS) {
                select_pad(World.prog.getVoice(), idx);
            }
        }

        if(!down && idx == first_hit[p]) {
            final int i1 = idx - PADS;
            final int i2 = i1 - VOICES;
            final int i3 = i2 - TOOLS;

            if(i1 >= 0 && i1 < VOICES)
                select_sound(i1, false);
            else if(i2 >= 0 && i2 < TOOLS)
                select_tool(i2);
            else if(i3 >= 0 && i3 < SELECTORS)
                select_mode(i3, false);
        }

        return true;
    }

    // --------------------------------------------------
    private int get_tile_at(int x, int y)
    {
        for(int i = 0; i < World.tiles.length; i++) {
            if(World.tiles[i].hit(x, y))
                return i;
        }
        return -1;
    }


}
