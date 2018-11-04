package se.tube42.drum;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import se.tube42.drum.audio.DeviceOutput;
import se.tube42.drum.audio.Mixer;
import se.tube42.drum.audio.Sequencer;
import se.tube42.drum.data.Program;
import se.tube42.drum.data.Sample;
import se.tube42.drum.data.World;
import se.tube42.drum.logic.LayoutService;
import se.tube42.drum.logic.ServiceProvider;
import se.tube42.drum.view.Choice2Scene;
import se.tube42.drum.view.ChoiceScene;
import se.tube42.drum.view.DrumScene;
import se.tube42.drum.view.SaveScene;
import se.tube42.lib.scene.SceneManager;
import se.tube42.lib.service.AssetService;
import se.tube42.lib.tweeny.Item;
import se.tube42.lib.util.BaseApp;

import static se.tube42.drum.data.Constants.CHARSET1;
import static se.tube42.drum.data.Constants.CHARSET2;
import static se.tube42.drum.data.Constants.DEF_AMPS;
import static se.tube42.drum.data.Constants.SAMPLES;
import static se.tube42.drum.data.Constants.VOICES;

public class DrumApp extends BaseApp {

    public DrumApp() {
    }

    public void onCreate(SceneManager mgr, Item bgc) {
        ServiceProvider.init();

        // set size before loading assets
        onResize(World.sw, World.sh);

        World.bgc = bgc;
        load_assets();

        // update size once more
        onResize(World.sw, World.sh);

        // create mixer
        World.prog = new Program(DEF_AMPS);
        World.seq = new Sequencer(World.prog);
        final DeviceOutput dev = new DeviceOutput();
        World.mixer = new Mixer(dev);

        // set the default amps


        World.scene_drum = new DrumScene();
        World.scene_choice = new ChoiceScene();
        World.scene_choice2 = new Choice2Scene();
        World.scene_save = new SaveScene();
        World.mgr = mgr;

        World.mgr.setScene(World.scene_drum);

        // TEMP until we fix the code handling back:
        Gdx.input.setCatchBackKey(false);

        // start the mixer!
        World.mixer.start();
    }

    public void onUpdate(float dt, long dtl) {
        ServiceProvider.service(dtl); // this will update job manager, tween manager and so on
    }

    public void onResize(int w, int h) {
        LayoutService.resize(w, h);
    }

    // ----------------------------------------------

    private void load_assets() {
        int ascale = World.ui_scale;
        if (ascale == 3) ascale = 2;
        if (ascale > 4) ascale = 4;

        String aname = "" + ascale;
        System.out.println("USING " + aname);


        Texture tmp;
        tmp = AssetService.load(aname + "/tiles.png", true);
        World.tex_tiles = AssetService.divide(tmp, 4, 2);

        tmp = AssetService.load(aname + "/icons.png", true);
        World.tex_icons = AssetService.divide(tmp, 4, 8);

        tmp = AssetService.load(aname + "/decals.png", true);
        World.tex_decals = AssetService.divide(tmp, 4, 2);

        tmp = AssetService.load(aname + "/rect.png", false);
        World.tex_rect = new TextureRegion[1];
        World.tex_rect[0] = new TextureRegion(tmp);


        World.font1 = AssetService.createFonts(
                "fonts/Roboto-Regular.ttf",
                CHARSET1, ascale * 16)[0];

        World.font2 = AssetService.createFonts(
                "fonts/RobotoCondensed-Light.ttf",
                CHARSET2, ascale * 12)[0];

        try {
            World.sounds = new Sample[VOICES];
            for (int i = 0; i < VOICES; i++) {
                final int vcount = SAMPLES[i].length;
                final float[][] data = new float[vcount][];
                for (int j = 0; j < vcount; j++) {
                    data[j] = ServiceProvider.loadSample(
                            "samples/" + SAMPLES[i][j], World.freq);
                }

                World.sounds[i] = new Sample(data);
            }

        } catch (Exception e) {
            System.err.println("ERROR " + e);
            System.err.flush();
            System.exit(20);
        }
    }


    @Override
    public void pause() {
        ServiceProvider.autoSave();
        World.mgr.onPause();
        World.mixer.stop();
        super.pause();
    }


    @Override
    public void resume() {
        super.resume();
        World.mixer.start();
        World.mgr.onResume();

    }

    public void dispose() {
        System.out.println("Disposing...\n");
        World.mixer.dispose();
        super.dispose();
    }
}

