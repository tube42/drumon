package se.tube42.drum.view;

import static se.tube42.drum.data.Constants.ALPHA_INACTIVE;
import static se.tube42.drum.data.Constants.COLOR_SAVE;
import static se.tube42.drum.data.Constants.TILE_BUTTON0;
import static se.tube42.drum.data.Constants.TILE_BUTTON1;

public class SaveItem extends PadItem {
    private String data;
    private boolean active;

    public SaveItem() {
        super(-1);
        setActive(false);
        setData(null);
        setColor(COLOR_SAVE);
    }

    // --------------------------------------

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        setTile(active ? TILE_BUTTON1 : TILE_BUTTON0);
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;

        setAlpha(data != null ? 1 : ALPHA_INACTIVE);
    }
}
