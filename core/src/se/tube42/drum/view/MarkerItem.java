package se.tube42.drum.view;

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


public class MarkerItem extends SpriteItem
{
    private int last;
    private PadItem [] pads;

    public MarkerItem(PadItem [] pads)
    {
        super(World.tex_tiles);
        super.setIndex(TILE_MARKER);
        this.last = -1;
        this.pads = pads;
    }

    public void setBeat(int b)
    {
        setBeat(b, 0f);
    }

    public void setBeat(int b, float time)
    {
        if(b == last) return;
        last = b;
        final PadItem t = pads[b];
        setPosition(time, t.getX(), t.getY());
    }
}
