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
        BT_SEQ_GET = 0,
        BT_SEQ_SET = 1,
        BT_SET_LOAD = 2,
        BT_SET_SAVE = 3,
        BT_SET_CLEAR = 4,
        BT_SEQ_RENDER = 5,
        BT_CLOSE = 6,
        BT_COUNT = 7
        ;

    private Layer layer;
    private SaveItem[] seqs;
    private ButtonItem[] buttons;
    private String restore_data, temp_data;
    private int curr_save;


    private static final String
        CLEAR_NORMAL = "Clear",
        CLEAR_CONFIRM = "Confirm"
        ;

    public SaveScene() {
        super("save");

        seqs = new SaveItem[NUM_SAVES];
        for (int i = 0; i < seqs.length; i++)
            seqs[i] = new SaveItem();

        buttons = new ButtonItem[BT_COUNT];
        buttons[BT_SEQ_GET] = new ButtonItem("Get");
        buttons[BT_SEQ_SET] = new ButtonItem("Set");
        buttons[BT_SET_LOAD] = new ButtonItem("Load");
        buttons[BT_SET_SAVE] = new ButtonItem("Save");
        buttons[BT_SET_CLEAR] = new ButtonItem("Clear");
        buttons[BT_SEQ_RENDER] = new ButtonItem("Render");
        buttons[BT_CLOSE] = new ButtonItem("Close");
        buttons[BT_CLOSE].setColor(COLOR_BUTTON_CLOSE);

        layer = getLayer(0);
        layer.add(seqs);
        layer.add(buttons);
    }

    // ------------------------------------------------

    public void onShow() {
        super.onShow();

        // save current data & cleat temp
        restore_data = SaveService.drumMachineToString(World.dm);
        temp_data = null;

        // no save selected for now
        curr_save = -1;

        // set current saves
        for (int i = 0; i < seqs.length; i++) {
            seqs[i].setData(SaveService.getSave(i));
        }

        buttons[BT_SET_CLEAR].setText(CLEAR_NORMAL);
        buttons[BT_SET_CLEAR].setColor(COLOR_BUTTON);


        animate(true);

        // initial update
        update_seqs();
        update_buttons();
    }

    public void onHide() {
        super.onHide();
        animate(false);
    }

    private void animate(boolean in_) {
        // in animation: seq
        for (int i = 0; i < seqs.length; i++) {
            final SaveItem si = seqs[i];
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
            final SaveItem si = seqs[i];
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


    // ----------------------------------------------------------
    // set operations

    private void set_clear() {
        ButtonItem b = buttons[BT_SET_CLEAR];

        if(CLEAR_NORMAL.equalsIgnoreCase( b.getText())) {
            b.setText(CLEAR_CONFIRM);
            b.setColor(COLOR_BUTTON_WARN);
        } else {
            b.setText(CLEAR_NORMAL);
            b.setColor(COLOR_BUTTON);

            for (int i = 0; i < seqs.length; i++) {
                SaveService.deleteSave(i);
                seqs[i].setData(null);
            }
        }
    }

    private void set_save() {
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
    private void set_load() {
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
    // seq operations

    private void seq_get() {
        restore_data = temp_data;
    }


    private void seq_set() {
        SaveService.setSave(curr_save, restore_data);   // save to storage
        seqs[curr_save].setData(restore_data);         // force update the UI
    }

    private void seq_render() {
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

    // ----------------------------------------------------------
    // save items

    private void update_seqs() {
        for (int i = 0; i < seqs.length; i++) {
            seqs[i].setActive(i == curr_save);
        }
    }

    private void press_seq_button(int index) {
        curr_save = index;
        seqs[index].animPress();

        // temp load song if it has any
        temp_data = SaveService.getSave(index);
        if (temp_data != null)
            if (!SaveService.drumMachineFromString(World.dm, temp_data))
                temp_data = null; // cant load this?

        update_seqs();
        update_buttons();
    }

    // ----------------------------------------------------------
    // button items

    private void update_buttons() {
        buttons[BT_SEQ_GET].setActive(temp_data != null);
        buttons[BT_SEQ_SET].setActive(curr_save != -1);
        buttons[BT_SEQ_SET].setColor(temp_data != null ? COLOR_BUTTON_WARN : COLOR_BUTTON); // overwriting!

        buttons[BT_SET_LOAD].setActive(true);
        buttons[BT_SET_SAVE].setActive(true);

        buttons[BT_SEQ_RENDER].setActive(false); // TODO: under development
        buttons[BT_SET_CLEAR].setActive(false); // TODO: used library needs update
    }

    private void press_button(int index) {
        if (!buttons[index].isActive())
            return;

        buttons[index].animPress();

        switch (index) {
            case BT_SEQ_SET:
                if (curr_save != -1) {
                    seq_set();
                }
                break;
            case BT_SEQ_GET:
                if (temp_data != null) {
                    seq_get();
                }
                break;

            case BT_SET_LOAD:
                set_load();
                break;

            case BT_SET_SAVE:
                set_save();
                break;
            case BT_SEQ_RENDER:
                seq_render();
                break;
            case BT_SET_CLEAR:
                set_clear();
                break;
            case BT_CLOSE:
                go_back();
                break;
        }

        update_buttons();
    }

    private void press(BaseItem bi) {
        for (int i = 0; i < seqs.length; i++) {
            if (bi == seqs[i]) {
                press_seq_button(i);
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
        SaveService.drumMachineFromString(World.dm, restore_data);
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
