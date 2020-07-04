package se.tube42.drum.view;

import com.badlogic.gdx.*;
import com.badlogic.gdx.Input.*;

import se.tube42.drum.audio.*;
import se.tube42.drum.data.*;
import se.tube42.drum.logic.*;
import se.tube42.lib.item.*;
import se.tube42.lib.scene.*;
import se.tube42.lib.tweeny.*;

import static se.tube42.drum.data.Constants.*;

public class DrumScene extends Scene implements SequencerListener {
	// button classes
	private final int CLASS_PAD = 0, CLASS_VOICE = 1, CLASS_TOOL = 2, CLASS_SELECT = 3;
	private final static int MAX_TOUCH = 3;
	private BaseButton[] hits0 = new BaseButton[MAX_TOUCH];
	private BaseButton[] hits1 = new BaseButton[MAX_TOUCH];

	private BaseButton[] tiles;
	private PadItem[] tile_pads;
	private VoiceItem[] tile_voices;
	private PressItem[] tile_tools;
	private PressItem[] tile_selectors;
	private MarkerItem marker;

	private Layer layer_tiles;
	private BaseText item_msg;
	private int mode;
	private boolean first;
	private volatile int mb_beat, mb_sample; // seq state posted from the other thread

	public DrumScene() {
		super("drum");

		World.seq.setListener(this);

		ServiceProvider.setColorItem(COLOR_BG, World.bgc, 0f, 1f, 2f);

		tiles = new BaseButton[PADS + VOICES + TOOLS + SELECTORS];
		int index = 0;

		// PADS
		tile_pads = new PadItem[PADS];
		for (int i = 0; i < PADS; i++, index++) {
			final PadItem pi = new PadItem(TILE_PAD0);
			pi.register(CLASS_PAD, i, false);
			tiles[index] = tile_pads[i] = pi;
		}

		// VOICES
		tile_voices = new VoiceItem[VOICES];
		for (int i = 0; i < VOICES; i++, index++) {
			final VoiceItem vi = new VoiceItem(ICON_KICK + i, COLOR_VOICES);
			vi.register(CLASS_VOICE, i, true);
			tiles[index] = tile_voices[i] = vi;
		}

		// voice with modified action animation:
		tile_voices[ICON_KICK - ICON_KICK].setAnimType(VoiceItem.ANIM_SCALE);
		tile_voices[ICON_SNARE - ICON_KICK].setAnimType(VoiceItem.ANIM_DOWN);
		tile_voices[ICON_TOM - ICON_KICK].setAnimType(VoiceItem.ANIM_DOWN);
		tile_voices[ICON_HC - ICON_KICK].setAnimType(VoiceItem.ANIM_UP);
		tile_voices[ICON_HO - ICON_KICK].setAnimType(VoiceItem.ANIM_UP);

		// tools
		tile_tools = new PressItem[TOOLS];
		for (int i = 0; i < TOOLS; i++, index++) {
			final PressItem pi = new PressItem(TILE_BUTTON0, 0, 0);
			pi.register(CLASS_TOOL, i, true);
			tiles[index] = tile_tools[i] = pi;
		}

		// selectors
		tile_selectors = new PressItem[SELECTORS];
		for (int i = 0; i < SELECTORS; i++, index++) {
			final PressItem pi = new PressItem(TILE_BUTTON0, SELECTOR_ICONS[i], COLOR_SELECTORS[i]);
			pi.register(CLASS_SELECT, i, false);
			tiles[index] = tile_selectors[i] = pi;
		}

		layer_tiles = getLayer(1);
		layer_tiles.add(tiles);

		marker = new MarkerItem(tile_pads);
		marker.setColor(COLOR_MARKER);
		marker.flags &= ~BaseItem.FLAG_VISIBLE;
		getLayer(2).add(marker);

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

	public void onShow() {
		this.mb_beat = -1;
		this.mb_sample = 0;

		if (first) {
			first = false;
			reposition(true);

			// set beat to 0 on the first screen
			marker.setBeat(1); // force change
			marker.setBeat(0);
		} else {
			reposition(false);
			for (BaseButton tile : tiles) {
				final float t = ServiceProvider.getRandom(0.2f, 0.3f);
				tile.set(BaseItem.ITEM_A, 1).configure(t, null);
			}

			// update beat right away, dont wait until the next one
			marker.setBeat(World.seq.getBeat());
		}

		// this is needed since the animation code above has removed or alpha change:
		select_sound(World.prog.getVoice(), true);
	}

	public void onHide() {
		for (BaseButton tile : tiles) {
			final float t = ServiceProvider.getRandom(0.2f, 0.3f);
			tile.set(BaseItem.ITEM_A, 0).configure(t, null);
		}
	}

	private void reposition(boolean animate) {
		final int w = World.sw;
		final int h = World.sh;
		final int m = World.prog.getMeasure();

		// position pads:
		if (m == MEASURE_88 || m == MEASURE_68) {
			marker.setSize(World.size_pad2, World.size_pad2);
			final int x0 = World.x0_pad2 + (m == MEASURE_68 ? World.stripe_pad2_x : 0);
			final int y0 = World.y0_pad2;
			for (int y = 0; y < 4; y++) {
				for (int x = 0; x < 8; x++) {
					final PadItem pad = tile_pads[x + y * 8];
					pad.setSize(World.size_pad2, World.size_pad2);
					pad.x2 = x0 + World.stripe_pad2_x * x;
					pad.y2 = y0 + World.stripe_pad2_y * (3 - y);
				}
			}
		} else { // MEASURE_44 or MEASURE_34
			marker.setSize(World.size_pad1, World.size_pad1);
			final int x0 = World.x0_pad1 + (m == MEASURE_34 ? World.stripe_pad1 / 2 : 0);
			final int y0 = World.y0_pad1;
			for (int y = 0; y < 4; y++) {
				for (int x = 0; x < 8; x++) {
					final PadItem pad = tile_pads[x + y * 8];
					pad.setSize(World.size_pad1, World.size_pad1);
					pad.x2 = x0 + World.stripe_pad1 * (x / 2);
					pad.y2 = y0 + World.stripe_pad1 * (3 - y);
				}
			}
		}

		// hide the ones that are not visible:
		for (int i = 0; i < PADS; i++) {
			if (!Measure.plays(m, i))
				tile_pads[i].flags &= ~BaseItem.FLAG_VISIBLE;
			else
				tile_pads[i].flags |= BaseItem.FLAG_VISIBLE;
		}

		// position the rest
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				final BaseItem bi = tiles[PADS + x + y * 4];
				bi.setSize(World.size_tile, World.size_tile);
				bi.x2 = World.x0_tile + World.stripe_tile * x;
				bi.y2 = World.y0_tile + World.stripe_tile * (3 - y);
			}
		}

		// animate entering the screen, or just animate to position
		for (int i = 0; i < tiles.length; i++) {
			final BaseItem bi = tiles[i];
			final float x1 = bi.x2;
			final float y1 = bi.y2;

			if (animate) {
				final float x0 = x1 + (x1 < World.sw / 2f ? -World.sw : World.sw);
				final float y0 = x1 + (y1 < World.sh / 2f ? -World.sh : World.sh);
				final float p = 0.8f + (8 - i / 4f) * 0.05f;
				final float t = ServiceProvider.getRandom(0.35f, 0.5f);

				bi.pause(BaseItem.ITEM_X, x0, p).tail(x1).configure(t, TweenEquation.QUAD_OUT);
				bi.pause(BaseItem.ITEM_Y, y0, p).tail(y1).configure(t, TweenEquation.QUAD_OUT);
			} else {
				final float t = ServiceProvider.getRandom(0.35f, 0.5f);
				bi.setPosition(t, x1, y1);
			}
		}
	}

	// ------------------------------------------------

	private void msg_show(String str, int sx, int sy) {
		final int x0 = World.sw / 2;
		final int y0 = World.sh * 2 / 3;
		final int dx = sx * World.sw / 4;
		final int dy = sy * World.sh / 3;

		item_msg.setText(str);
		item_msg.setImmediate(BaseItem.ITEM_Y, y0);

		item_msg.set(BaseItem.ITEM_X, x0 + dx, x0).configure(0.2f, null).pause(1).tail(x0 - dx).configure(0.2f, null);

		item_msg.set(BaseItem.ITEM_Y, y0 + dy, y0).configure(0.2f, null).pause(1).tail(y0 - dy).configure(0.2f, null);

		item_msg.set(BaseItem.ITEM_A, 0, 1).configure(0.2f, null).pause(1).tail(0).configure(0.2f, null);
	}

	// ------------------------------------------------
	// PADS
	private void update_pad(int pad) {
		final int voice = World.prog.getVoice();
		tile_pads[pad].setTile(World.prog.get(voice, pad) ? TILE_PAD1 : TILE_PAD0);
	}

	private void update_pads() {
		for (int i = 0; i < PADS; i++)
			update_pad(i);
	}

	private void clear_pads(int voice, boolean allbanks) {
		World.prog.clear(voice, allbanks);
	}

	private void shuffle_pads(int voice) {
		final int m = World.prog.getMeasure();
		for (int i = 0; i < PADS * 5;) {
			int a = ServiceProvider.getRandomInt(PADS);
			int b = ServiceProvider.getRandomInt(PADS);

			// dont touch them if they are not playable in current metre
			if (!Measure.plays(m, a) || !Measure.plays(m, b))
				continue;

			i++;
			if (World.prog.get(voice, a) && !World.prog.get(voice, b)) {
				select_pad(voice, a);
				select_pad(voice, b);
			}
		}
	}

	private void select_pad(int voice, int pad) {
		World.prog.set(voice, pad, !World.prog.get(voice, pad));
		tile_pads[pad].animPress();
		if (voice == World.prog.getVoice())
			update_pad(pad);
	}

	// ------------------------------------------------
	// SOUNDS

	private void update_sounds() {
		for (int i = 0; i < VOICES; i++) {
			tile_voices[i].setVariant(World.prog.getSampleVariant(i), World.prog.getBank(i));
		}
	}

	private void select_sound(int voice, boolean force) {
		final int old_voice = World.prog.getVoice();

		if (!force && old_voice == voice) {
			final int max = World.sounds[voice].getNumOfVariants();
			int next = 1 + World.prog.getSampleVariant(voice);
			if (next >= max)
				next = 0;
			World.prog.setSampleVariant(voice, next);
		} else {
			World.prog.setVoice(voice);
			for (int i = 0; i < VOICES; i++)
				tile_voices[i].setAlpha(i == voice ? 1 : ALPHA_INACTIVE);

			final int c = COLOR_PADS[voice];
			ServiceProvider.setColorItem(c, World.bgc, 0f, 0.1f, 0.7f);

			// set pad color and make the 4/8 adds a bit darker
			final int c2 = ServiceProvider.mulColor(c, 0.75f);
			for (int i = 0; i < PADS; i++)
				tile_pads[i].setColor((i & 1) == 0 ? c : c2);
		}

		tile_voices[voice].animPress();
		update(false, true, false, false);
	}

	private void longpress_sound(int voice) {
		// long press on voice => change voice volume
		get_choice(World.prog, ICON_KICK + voice, Program.PARAM_VOLUME_VAR_n + voice, Program.PARAM_VOLUME_n + voice);
	}

	// ------------------------------------------------
	// TOOLS

	private void update_tools(boolean modechange) {
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

		switch (mode) {
		case 0:
			i0 = seq.isPaused() ? ICON_PLAY : ICON_PAUSE;

			// assuming the icon order is 1/4, 1/8 and 1/16
			i1 = ICON_NOTE4 + prog.getTempoMultiplier();
			break;
		case 1:
			i0 = prog.getBank(voice) == 0 ? ICON_A : ICON_B;
			i3 = ICON_44 + prog.getMeasure();
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
		tile_tools[0].change(color, i0, v0, modechange);
		tile_tools[1].change(color, i1, v1, modechange);
		tile_tools[2].change(color, i2, v2, modechange);
		tile_tools[3].change(color, i3, v3, modechange);
	}

	private void select_tool(int id) {
		final int voice = World.prog.getVoice();
		final int op = TOOLS * mode + id;
		String msg = null;
		boolean tmp;

		tile_tools[id].animPress();

		switch (op) {
		case TOOL_TEMPO_MUL:
			int n = World.prog.getTempoMultiplier() + 1;
			if (n > MAX_TEMPO_MUL)
				n = MIN_TEMPO_MUL;
			World.prog.setTempoMultiplier(n);
			break;

		case TOOL_TEMPO_DETECT:
			if (World.td.add()) {
				World.prog.setTempo(World.td.get());
				msg_show("" + World.td.get(), 0, -1);
			}
			break;

		case TOOL_TEMPO_SET:
			get_choice(World.prog, ICON_METRONOME, Program.PARAM_TEMPO, -1);
			break;

		case TOOL_TEMPO_PAUSE:
			if(World.seq.isPaused() && Settings.pause_restart)
				World.seq.restart();
			World.seq.setPause(!World.seq.isPaused());
			break;

		case TOOL_SEQ_AB:
			World.prog.setBank(voice, 1 ^ World.prog.getBank(voice));
			break;

		case TOOL_SEQ_SHUFFLE:
			shuffle_pads(voice);
			msg = "Shuffled track";
			break;

		case TOOL_SEQ_MEASURE:
			World.prog.setMeasure(Measure.getNextMeasure(World.prog.getMeasure()));
			update(false, false, true, false);
			reposition(false);
			return;

		case TOOL_FX_LOFI:
			tmp = World.mixer.getEffectChain().toggle(0);
			msg = "lo-fi effect " + (tmp ? "on" : "off");
			break;

		case TOOL_FX_FILTER:
			tmp = World.mixer.getEffectChain().toggle(1);
			msg = "filter effect " + (tmp ? "on" : "off");
			break;

		case TOOL_FX_DELAY:
			tmp = World.mixer.getEffectChain().toggle(2);
			msg = "delay effect " + (tmp ? "on" : "off");
			break;

		case TOOL_FX_COMP:
			tmp = World.mixer.getEffectChain().toggle(3);
			msg = "compressor " + (tmp ? "on" : "off");
			break;

		case TOOL_SEQ_CLEAR:
			clear_pads(voice, false);
			msg = "Cleared track";
			break;
		case TOOL_MISC_SETTINGS:
			World.mgr.setScene(World.scene_settings, 120);
			break;

		case TOOL_MISC_ABOUT:
			World.mgr.setScene(World.scene_about, 120);
			break;

		case TOOL_MISC_SAVE:
			World.mgr.setScene(World.scene_save, 120);
			break;
		}

		if (msg != null) {
			SystemService.getInstance().showMessage(msg);
		}
		update(false, false, true, false);
	}

	private void longpress_tool(int id) {
		final int op = TOOLS * mode + id;
		final EffectChain ef = World.mixer.getEffectChain();
		String msg = null;

		tile_tools[id].animPress();

		switch (op) {
		case TOOL_FX_LOFI:
			get_choice(ef.getEffect(FX_LOFI), ICON_LOFI, 0, -1);
			break;

		case TOOL_FX_DELAY:
			get_choice(ef.getEffect(FX_DELAY), ICON_DELAY, 0, 1);
			break;

		case TOOL_FX_FILTER:
			get_choice(ef.getEffect(FX_FILTER), ICON_FILTER, 0, 1);
			break;

		case TOOL_FX_COMP:
			get_choice(ef.getEffect(FX_COMP), ICON_COMPRESS, 0, 1);
			break;

		case TOOL_TEMPO_DETECT:
			World.prog.setTempo(120);
			msg_show("120", 0, -1);
			break;

		case TOOL_SEQ_SHUFFLE:
			for (int i = 0; i < VOICES; i++)
				shuffle_pads(i);
			msg = "Shuffled song";
			break;

		case TOOL_SEQ_CLEAR:
			for (int i = 0; i < VOICES; i++)
				clear_pads(i, true);
			World.prog.reset();
			World.mixer.getEffectChain().reset();
			select_sound(0, true);

			msg = "Cleared song";
			break;
		}

		if (msg != null) {
			SystemService.getInstance().showMessage(msg);
		}

		update(true, true, true, false);
	}

	// ------------------------------------------------
	// MODE

	private void update_mode() {
		for (int i = 0; i < SELECTORS; i++)
			tile_selectors[i].setActive(i == this.mode);
	}

	private void select_mode(int mode, boolean force) {
		if (force || this.mode != mode) {
			this.mode = mode;
			update(false, false, false, true);
		}
	}

	// ------------------------------------------------
	// ALL
	private void update(boolean pads, boolean sounds, boolean tools, boolean mode) {
		if (pads || sounds || tools)
			update_pads();
		if (sounds || tools)
			update_sounds();
		if (tools || sounds || mode)
			update_tools(mode);
		if (mode)
			update_mode();
	}

	private void onEnter(BaseButton hit, boolean pressed) {
		if (hit.class_ == CLASS_PAD) {
			select_pad(World.prog.getVoice(), hit.id);
		}
	}

	private void onLongPress(BaseButton hit) {
		switch (hit.class_) {
		case CLASS_TOOL:
			longpress_tool(hit.id);
			break;
		case CLASS_VOICE:
			longpress_sound(hit.id);
		}
	}

	private void onShortPress(BaseButton hit) {
		switch (hit.class_) {
		case CLASS_VOICE:
			select_sound(hit.id, false);
			break;
		case CLASS_TOOL:
			select_tool(hit.id);
			break;
		case CLASS_SELECT:
			select_mode(hit.id, false);
			break;
		}
	}

	// ------------------------------------------------
	// Choices

	private void get_choice(Parameters params, int icon, int idx1, int idx2) {
		if (idx1 == -1)
			return;
		if (idx2 == -1) {
			World.scene_choice.set(params, idx1, icon);
			World.mgr.setScene(World.scene_choice, 120);
		} else {
			World.scene_choice2.set(params, idx1, idx2, icon);
			World.mgr.setScene(World.scene_choice2, 120);
		}
	}

	// ------------------------------------------------
	// SequencerListener interface:
	//
	// to avoid multi-thread madness we will just copy it variables in audio
	// thread and handle them in our own thread

	public void onBeatStart(int beat) {
		mb_beat = beat;
	}

	public void onSampleStart(int beat, int sample) {
		mb_sample |= 1 << sample;
	}

	public void onUpdate(float dt) {
		// detect long press
		long when = System.currentTimeMillis() - LONGPRESS_DELAY;
		for (int i = 0; i < MAX_TOUCH; i++) {
			if (hits0[i] != null && hits0[i].hasLongpress) {
				if (hits0[i].timeDown < when) {
					onLongPress(hits0[i]);
					hits0[i] = hits1[i] = null;
				}
			}
		}

		// beat update
		if (mb_beat != -1) {
			// copy it and reset the source
			final int beat = mb_beat;
			final int samples = mb_sample;
			mb_beat = -1;
			mb_sample = 0;

			// udpate beat marker
			marker.setBeat(beat);
			marker.flags |= BaseItem.FLAG_VISIBLE;

			// mark played pad
			if (samples != 0) {
				final int mask = 1 << World.prog.getVoice();
				if ((samples & mask) != 0) {
					tile_pads[beat].animAction(0);
				}
			}

			// mark played sound
			for (int i = 0; i < VOICES; i++) {
				if ((samples & (1 << i)) != 0) {
					final float amp = World.sounds[i].getAmp();
					tile_voices[i].animAction(amp);
				}
			}
		}
	}

	// ----------------------------------------------------------

	public void resize(int w, int h) {
		super.resize(w, h);
		reposition(false);
	}

	public boolean type(int key, boolean down) {
		if (down) {
			if (key == Keys.BACK || key == Keys.ESCAPE) {
				Gdx.app.exit();
				return true;
			}
		}

		return false;
	}

	public boolean touch(int p, int x, int y, boolean down, boolean drag) {
		/* multi-touch limit */
		if (p < 0 || p >= MAX_TOUCH)
			return false;

		final BaseButton hit = (BaseButton) layer_tiles.hit(x, y);
		if (down && !drag) {
			hits0[p] = hit;
		}

		if (hit == null)
			return false;

		/* record press time */
		if (down && !drag) {
			hit.timeDown = System.currentTimeMillis();
		}

		if (hits1[p] != hit) {
			onEnter(hit, down && !drag);
			hits1[p] = hit;
		}

		if (!down) {
			if (hits0[p] == hit)
				onShortPress(hit);
			hits0[p] = hits1[p] = null;
		}

		return true;
	}

}
