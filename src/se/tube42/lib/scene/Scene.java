package se.tube42.lib.scene;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.*;

import se.tube42.lib.item.*;


public class Scene
{
	private LayerList layers;
    private String name;
    private int _w, _h;

    public Scene(String name)
    {
        this.name = name;
        this.layers = new LayerList();
        this._w = -1;
        this._h = -1;
    }

    // -----------------------------------------------

    public void render(SpriteBatch batch)
    {
        layers.draw(batch);
    }

    public BaseItem hit(int x, int y)
    {
    	return layers.hit(x, y);
    }

    // ---------------------------------------

    public Layer addLayer(Layer l)
    {
    	return layers.add(l);
    }

    public Layer getLayer(int index)
    {
    	return layers.get(index);
    }

    public LayerList getLayers()
    {
    	return layers;
    }

    // ------------------------------------------------
    /* package */ void resizeIfChanged(int w, int h)
    {
        if(_w != w || h != _h) {
            resize(w, h);
            _w = w;
            _h = h;
        }
    }

    public void resize(int w, int h)
    {

    }

    public final void update(float dt)
    {
        onUpdate(dt);
        layers.update(dt);
    }


    public void dispose() { }

    public void onUpdate(float dt)  { }
    public void onShow() { }
    public void onHide() { }

    public void onPause() { }
    public void onResume() { }

    public boolean touch(int ptr, int x, int y, boolean down, boolean drag)
    {
        return false;
    }

    public boolean type(int key, boolean down)
    {
        return false;
    }

    public String toString()
    {
        return name;
    }
}
