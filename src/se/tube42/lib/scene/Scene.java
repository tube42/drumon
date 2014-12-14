package se.tube42.lib.scene;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.*;

import  se.tube42.lib.item.*;

public class Scene
{
	private LayerList layers;
    private String name;
    protected int sw, sh;

    public Scene(String name)
    {
        this.name = name;
        this.layers = new LayerList();
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

    public void resize(int w, int h)
    {
        this.sw = w;
        this.sh = h;
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

    public boolean touch(int x, int y, boolean down, boolean drag)
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
