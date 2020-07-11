package se.tube42.drum.view;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import se.tube42.lib.scene.*;
import se.tube42.lib.service.AssetService;
import se.tube42.lib.item.*;

import se.tube42.drum.audio.*;
import se.tube42.drum.data.*;
import se.tube42.drum.logic.*;

import static se.tube42.drum.data.Constants.*;

public class InitScene extends Scene implements Runnable {

	private BaseText text;
	private SpriteItem img;

	public InitScene() {
		super("loading");

		ServiceProvider.setColorItem(0xFFFFFF, World.bgc, 0f, 1f, 0f); // light-gray background so we can see things
		load_first_assets();

		text = new BaseText(World.font2);
		text.setAlignment(-0.5f, 0.5f);
		text.setText("loading ...");
		text.setColor(0x000000);

		img = new SpriteItem(World.tex_icons, ICON_HELP);
		img.setScale(2);

		Layer l = getLayer(0);
		l.add(text, img);

		Thread t = new Thread(this);
		t.start();
	}

	public void resize(int w, int h) {
		final int size = World.size_button;
		final int gap = size / 2;

		img.setPosition((w - img.getW()) / 2, h / 2);
		text.setPosition(w / 2, img.getY() - 1.5f * img.getH());
	}

	// ----------------------------------------------------------

	private void load_first_assets() {
		String aname = "" + World.ui_ascale;
		System.out.println("USING " + aname);

		Texture tmp;
		tmp = AssetService.load(aname + "/tiles.png", true);
		World.tex_tiles = AssetService.divide(tmp, 4, 2);

		tmp = AssetService.load(aname + "/icons.png", true);
		World.tex_icons = AssetService.divide(tmp, 8, 8);

		tmp = AssetService.load(aname + "/rect.png", false);
		World.tex_rect = new TextureRegion[1];
		World.tex_rect[0] = new TextureRegion(tmp);

		World.font1 = AssetService.createFonts("fonts/Roboto-Regular.ttf", CHARSET1, World.ui_ascale * SIZE_FONT1)[0];
		World.font2 = AssetService.createFonts("fonts/RobotoCondensed-Light.ttf", CHARSET2,
				World.ui_ascale * SIZE_FONT2)[0];

		World.sounds = new Sample[VOICES]; // we will load the samples later...
	}

	// load the other assets fron this thread
	public void run() {

		try {
			for (int i = 0; i < VOICES; i++) {
				img.setIndex(ICON_KICK + i);

				final int vcount = SAMPLES[i].length;
				final float[][] data = new float[vcount][];
				for (int j = 0; j < vcount; j++)
					data[j] = ServiceProvider.loadSample("samples/" + SAMPLES[i][j], World.freq);
				World.sounds[i] = new Sample(data);
			}

			// create mixer etc
			World.prog = new Program(DEF_AMPS);
			World.seq = new Sequencer(World.prog);
			final DeviceOutput dev = new DeviceOutput();
			World.mixer = new Mixer(dev);

			// create the scenes
			World.scene_drum = new DrumScene();
			World.scene_choice = new ChoiceScene();
			World.scene_choice2 = new Choice2Scene();
			World.scene_save = new SaveScene();
			World.scene_about = new AboutScene();
			World.scene_settings = new SettingsScene();

			// and lets start things...
			World.mgr.setScene(World.scene_drum);
			World.mixer.start();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(20);
		}
	}
}
