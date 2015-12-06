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

        if(first) {
            first = false;
            reposition(true);

            // set beat to 0 on the first screen
            World.marker.setBeat(0);
        } else {
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

        for(int y = 0; y < 8; y++) {
            int y0 = World.tile_y0 + World.tile_stripe * (7-y) +
                  ((y < 4) ? World.tile_y0 / 4 : -World.tile_y0 / 4);

            for(int x = 0; x < 4; x++) {
                final int index = x + y * 4;
                final float x0 = World.tile_x0 + World.tile_stripe * x;
                final float x1 = (x0 < w / 2) ? x0 - w : x0 + w;
                final float y1 = (y0 < h / 2) ? y0 - h : y0 + h;

                World.tiles[index].setSize(World.tile_size, World.tile_size);

                if(animate) {
                    final float p = 0.8f + (8-y) * 0.05f;
                    final float t = ServiceProvider.getRandom(0.35f, 0.5f);
                    World.tiles[index].pause(BaseItem.ITEM_X, x1, p)
                          .tail(x0).configure(t, TweenEquation.QUAD_OUT);

                    World.tiles[index].pause(BaseItem.ITEM_Y, y1, p)
                          .tail(y0).configure(t, TweenEquation.QUAD_OUT);
                } else {
                    World.tiles[index].setPosition(x0, y0);
                }
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

    private void select_pad(int pad)
    {
        final int voice =  World.prog.getVoice();
        World.prog.set(voice, pad, !World.prog.get(voice, pad));
        World.tile_pads[pad].mark0();
        update_pad(pad);
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
            // mode = 0, timing
        case 0:
            int n = World.prog.getTempoMultiplier() * 2;
            if(n > 4) n = 1;
            World.prog.setTempoMultiplier(n);
            break;

        case 1:
            if(World.td.add()) {
                World.prog.setTempo( World.td.get() );
                msg_show("" + World.td.get(), 0, -1);
            }
            break;

        case 2:
            get_choice(World.prog, CHOICE_TEMPO, 0);
            break;

            // mode = 1, sequence
        case 4:
            World.seq.setPause(! World.seq.isPaused());
            break;

        case 5:
            World.prog.setBank(voice, !World.prog.getBank(voice));
            break;

        case 6:
            // shuffle:
            for(int i = 0; i < PADS * 5; i++) {
                int a = ServiceProvider.getRandomInt(PADS);
                int b = ServiceProvider.getRandomInt(PADS);

                if(World.prog.get(voice, a) && !World.prog.get(voice, b)) {
                    select_pad(a);
                    select_pad(b);
                }
            }
            break;

        case 7:
            // clear one
            for(int i = 0; i < PADS; i++)
                World.prog.set(voice, i, false);
            break;

            // mode = 2, waveform
        case 8:
            World.mixer.getEffectChain().toggle(0);
            break;

        case 9:
            World.mixer.getEffectChain().toggle(1);
            break;

        case 10:
            World.mixer.getEffectChain().toggle(2);
            break;

        case 11:
            World.mixer.getEffectChain().toggle(3);
            break;

            // mode = 3, settings
        case 12:
            get_choice2(World.prog, CHOICE2_VOLUME, voice);
            break;
        case 13:
            get_choice2(World.mixer.getEffectChain().getEffect(FX_COMP),
                      CHOICE2_COMPRESS, -1);
            break;
        case 15:
            // save
            World.mgr.setScene(World.scene_save, 120);
            break;
        }

        update(false, false, true, false);
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
        World.marker.setSize(World.tile_size, World.tile_size);
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
        if(idx == -1) return false;

        if(last_hit[p] != idx) {
            last_hit[p] = idx;

            if(idx < PADS) {
                select_pad(idx);
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
