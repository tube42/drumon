
package se.tube42.lib.service;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.graphics.g2d.freetype.*;
import com.badlogic.gdx.utils.*;

import java.util.*;


public class AssetService
{
    private static HashMap<String, TextureRegion []>
          db = new HashMap<String, TextureRegion [] >();
    
    // -------------------------------------------------------------------
    // atlas

    public static TextureAtlas loadAtlas(String name)
    {
        String filename = name + ".atlas";
        return new TextureAtlas(Gdx.files.internal(filename));
    }

    public static void setFilter(TextureAtlas atlas, boolean linear)
    {
        Texture.TextureFilter tf = linear ?
              Texture.TextureFilter.Linear : Texture.TextureFilter.Nearest;
        for(Texture t : atlas.getTextures())
            t.setFilter(tf, tf);
    }

    public static TextureRegion [] extractRegions(TextureAtlas atlas, String name)
    {
        TextureRegion [] ret = db.get(name);
        if(ret != null) return ret;

        Array tmp = atlas.findRegions(name);
        if(tmp == null) {
            db.put(name, null);
            return null;
        }

        ret = new TextureRegion[tmp.size];
        for(int i = 0; i < ret.length; i++) {
            TextureAtlas.AtlasRegion ar = (TextureAtlas.AtlasRegion) tmp.items[i];

            ret[i] = (TextureRegion) tmp.items[i];

            
            // XXX: libgdx bugfix??
            ar.setRegionWidth(ar.originalWidth);
            ar.setRegionHeight(ar.originalHeight);
            

            // fix region borders
            correctRegionBorders(ar);

        }

        db.put(name, ret);
        return ret;
    }

    public static void correctRegionBorders(TextureRegion tr)
    {
        final Texture t = tr.getTexture();

        if(t.getMinFilter() != Texture.TextureFilter.Linear)
            return;

        if(t.getWidth() < 2 || t.getHeight() < 2)
            return;

        final float hpw = 0.5f / t.getWidth();
        final float hph = 0.6f / t.getHeight();
        tr.setU ( tr.getU () + hpw);
        tr.setV ( tr.getV () + hph);
        tr.setU2( tr.getU2() - hpw);
        tr.setV2( tr.getV2() - hph);
    }

    public static TextureRegion [] divide(TextureRegion t,
              int cw, int ch, boolean correct_borders)
    {
        final int tw = t.getRegionWidth();
        final int th = t.getRegionHeight();
        final int w = tw / cw;
        final int h = th / ch;
        if(cw < 1 || ch < 1) return null;

        final TextureRegion [][] tmp = t.split(w, h);
        final TextureRegion [] ret = new TextureRegion[cw * ch];
        for(int y = 0; y < ch; y++) {
            for(int x = 0; x < cw; x++) {
                ret[x + y * cw] = tmp[y][x];
            }
        }

        if(correct_borders) {
            for(int i = 0; i < ret.length; i++)
                correctRegionBorders(ret[i]);
        }

        return ret;
    }


    // -------------------------------------------------------------------
    // fonts

    public static BitmapFont [] createFonts(String filename,
              String charset, int... sizes)
    {
        final FreeTypeFontGenerator g = new FreeTypeFontGenerator(
                  Gdx.files.internal(filename));
        if(g == null) return null;

        BitmapFont [] ret = new BitmapFont[sizes.length];
        for(int i = 0; i < sizes.length; i++) {
            ret[i] = g.generateFont(sizes[i], charset, false);
            ret[i].setUseIntegerPositions(true);
        }

        g.dispose(); // to avoid memory leaks!
        return ret;
    }
}
