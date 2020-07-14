package se.tube42.drum.view;

import java.io.*;

import com.badlogic.gdx.Input.*;

import se.tube42.lib.tweeny.*;
import se.tube42.lib.scene.*;
import se.tube42.lib.service.StorageService;
import se.tube42.lib.item.*;

import se.tube42.drum.data.*;
import se.tube42.drum.logic.*;
import se.tube42.drum.util.Work;

import static se.tube42.drum.data.Constants.*;

public class SaveScene extends Scene {
    private static final int
        BT_LOAD = 0,
        BT_SAVE = 1,
        BT_IMPORT = 2,
        BT_EXPORT = 3,
        BT_DEL = 4,
        BT_RENDER = 5,
        BT_CLOSE = 6,
        BT_COUNT = 7
        ;

    private Layer layer;
    private SaveItem[] saves;
    private ButtonItem[] buttons;
    private String restore_data, temp_data;
    private int curr_save;

    public SaveScene() {
        super("save");

        saves = new SaveItem[NUM_SAVES];
        for (int i = 0; i < saves.length; i++)
            saves[i] = new SaveItem();

        buttons = new ButtonItem[BT_COUNT];
        buttons[BT_LOAD] = new ButtonItem("Load");
        buttons[BT_SAVE] = new ButtonItem("Save");
        buttons[BT_IMPORT] = new ButtonItem("Import");
        buttons[BT_EXPORT] = new ButtonItem("Export");
        buttons[BT_DEL] = new ButtonItem("Delete");
        buttons[BT_RENDER] = new ButtonItem("Render");
        buttons[BT_CLOSE] = new ButtonItem("Close");
        buttons[BT_CLOSE].setColor(COLOR_BUTTON_CLOSE);

        layer = getLayer(0);
        layer.add(saves);
        layer.add(buttons);
    }

    // ------------------------------------------------

    public void onShow() {
        super.onShow();

        // save current data & cleat temp
        restore_data = SaveService.currentToString();
        temp_data = null;

        // no save selected for now
        curr_save = -1;

        // set current saves
        for (int i = 0; i < saves.length; i++) {
            saves[i].setData(SaveService.getSave(i));
        }

        animate(true);

        // initial update
        update_saves();
        update_buttons();
    }

    public void onHide() {
        super.onHide();
        animate(false);
    }

    private void animate(boolean in_) {
        // in animation:
        for (int i = 0; i < saves.length; i++) {
            final SaveItem si = saves[i];
            final float t = ServiceProvider.getRandom(0.3f, 0.4f);
            si.set(BaseItem.ITEM_Y, in_, World.sh, si.y2, t, TweenEquation.BACK_OUT);
            si.setImmediate(BaseItem.ITEM_X, si.x2);
        }

        for (int i = 0; i < buttons.length; i++) {
            final ButtonItem bi = buttons[i];
            final float t = ServiceProvider.getRandom(0.4f, 0.5f);
            bi.set(BaseItem.ITEM_A, in_, 0.0f, 1f, t / 2, null);
            bi.set(BaseItem.ITEM_Y, in_, -bi.getH(), bi.y2, t, TweenEquation.BACK_OUT);
            bi.setImmediate(BaseItem.ITEM_X, bi.x2);
        }
    }
    // ------------------------------------------------

    public void resize(int w, int h) {
        // 4x4
        for (int i = 0; i < NUM_SAVES; i++) {
            final int x = i & 3;
            final int y = i >> 2;
            final SaveItem si = saves[i];
            final float t = ServiceProvider.getRandom(0.2f, 0.4f);
            si.setSize(World.size_pad1, World.size_pad1);
            si.setPosition(t, si.x2 = World.x0_pad1 + World.stripe_pad1 * x,
                    si.y2 = World.y0_pad1 + World.stripe_pad1 * y);
        }

        // 2x4
        final int y0 = World.y0_tile /* + World.stripe_tile / 2 + gap / 2 */;
        final int gap = Math.max(2, World.size_tile / 8);
        for (int i = 0; i < buttons.length; i++) {
            final int x = i & 1;
            final int y = 3 - (i >> 1);
            final ButtonItem bi = buttons[i];
            final float t = ServiceProvider.getRandom(0.2f, 0.4f);
            bi.setSize(World.size_tile * 2 - gap, World.size_tile - gap);
            bi.setPosition(t, bi.x2 = World.x0_tile + gap / 2 + World.stripe_tile * x * 2,
                    bi.y2 = y0 + World.stripe_tile * y);
        }
    }



    private void render_song() {
        SystemService.getInstance().writeFile("track.wav", "audio/wav", new Work<OutputStream>() {
            @Override
            public void success(OutputStream t) {
                try {
                    t.write("TODO!".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                    SystemService.getInstance().showMessage("FAILED to render: " + e.getMessage());
                }
            }

            @Override
            public void failure(String msg) {
                SystemService.getInstance().showMessage("FAILED to render: " + msg);
            }
        });
    }

    private void export_song() {
        SystemService.getInstance().writeFile("track.drumon", "application/octet-stream", new Work<OutputStream>() {
            @Override
            public void success(OutputStream w) {
                try {
                    w.write(restore_data.getBytes());
                    go_back();
                } catch (IOException e) {
                    e.printStackTrace();
                    SystemService.getInstance().showMessage("FAILED to export: " + e.getMessage());
                }
            }

            @Override
            public void failure(String msg) {
                SystemService.getInstance().showMessage("FAILED to export: " + msg);
            }
        });
    }
    private void import_song() {
        SystemService.getInstance().readFile("track.drumon", "application/octet-stream", new Work<InputStream>() {
            @Override
            public void success(InputStream r) {
                try {
                    // get entire file
                    StringBuffer b = new StringBuffer();
                    for(;;) {
                        int c = r.read();
                        if(c == -1) break;
                        b.append( (char) c);
                    }
                    String contents = b.toString();
                    System.out.println("LOADED: " + contents);
                    if(SaveService.isValidSave(contents)) {
                        restore_data = contents;
                        go_back();
                    } else {
                        SystemService.getInstance().showMessage("Not a valid file!");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    SystemService.getInstance().showMessage("FAILED to import: " + e.getMessage());
                }
            }

            @Override
            public void failure(String msg) {
                SystemService.getInstance().showMessage("FAILED to import: " + msg);
            }
        });
    }

    // ----------------------------------------------------------
    // save items

    private void update_saves() {
        for (int i = 0; i < saves.length; i++) {
            saves[i].setActive(i == curr_save);
        }
    }

    private void press_save(int index) {
        curr_save = index;
        saves[index].animPress();

        // temp load song if it has any
        temp_data = SaveService.getSave(index);
        if (temp_data != null)
            if (!SaveService.stringToCurrent(temp_data))
                temp_data = null; // cant load this?

        update_saves();
        update_buttons();
    }

    // ----------------------------------------------------------
    // button items

    private void update_buttons() {
        buttons[BT_LOAD].setActive(temp_data != null);
        buttons[BT_SAVE].setActive(curr_save != -1);
        buttons[BT_SAVE].setColor(temp_data != null ? COLOR_BUTTON_WARN : COLOR_BUTTON); // overwriting!

        buttons[BT_IMPORT].setActive(true);
        buttons[BT_EXPORT].setActive(true);

        buttons[BT_RENDER].setActive(false); // TODO: under development
        buttons[BT_DEL].setActive(false); // TODO: implement this
    }

    private void press_button(int index) {
        if (!buttons[index].isActive())
            return;

        buttons[index].animPress();

        switch (index) {
            case BT_SAVE:
                if (curr_save != -1) {
                    SaveService.setSave(curr_save, restore_data);
                    go_back();
                }
                break;
            case BT_LOAD:
                if (temp_data != null) {
                    restore_data = temp_data;
                    go_back();
                }
                break;

            case BT_IMPORT:
                import_song();
                break;

            case BT_EXPORT:
                export_song();
                break;
            case BT_RENDER:
                render_song();
                break;
            case BT_CLOSE:
                go_back();
                break;
        }

        update_buttons();
    }

    private void press(BaseItem bi) {
        for (int i = 0; i < saves.length; i++) {
            if (bi == saves[i]) {
                press_save(i);
                return;
            }
        }

        for (int i = 0; i < buttons.length; i++) {
            if (bi == buttons[i]) {
                press_button(i);
                return;
            }
        }

    }
    // ----------------------------------------------------------

    public void go_back() {
        // restore data before going back
        SaveService.stringToCurrent(restore_data);
        World.mgr.setScene(World.scene_drum, 200);
    }

    public boolean type(int key, boolean down) {
        if (down) {
            if (key == Keys.BACK || key == Keys.ESCAPE) {
                go_back();
                return true;
            }
        }
        return false;
    }

    public boolean touch(int p, int x, int y, boolean down, boolean drag) {
        if (down && !drag) {
            BaseItem hit = layer.hit(x, y);
            if (hit != null)
                press(hit);
        }
        return true;
    }

}
