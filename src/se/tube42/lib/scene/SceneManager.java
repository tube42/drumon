package se.tube42.lib.scene;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.*;


public class SceneManager
{
    private boolean touch_ignore;
    private Scene bg, fg, scene;
    private int sw, sh;

    public SceneManager()
    {
        this.touch_ignore = false;
        this.scene = null;
        this.bg = null;
        this.fg = null;
        sw = sh = 1;
    }

    // ---------------------------------------------------

    public void setBackground(Scene bg)
    {
        this.bg = bg;

        if(bg != null) {
            bg.resize(sw, sh);
        }
    }

    public Scene getBackground()
    {
        return bg;
    }

    public void setForeground(Scene fg)
    {
        this.fg = fg;

        if(fg != null) {
            fg.resize(sw, sh);
        }
    }

    public Scene getForeround()
    {
        return fg;
    }

    // ---------------------------------------------------

    public int getWidth() {  return sw; }
    public int getHeight() {  return sh; }
    public Scene getScene() { return scene; }

    public synchronized void setScene(Scene s)
    {
        System.out.println("[setScene] " + scene + " ==> " + s); // DEBUG

        if(s == scene) return;

        final Scene tmp = scene;

        this.scene = s;
        if(scene != null) {
            scene.onShow();
            scene.resize(sw, sh);
        }

        if(tmp != null) {
            tmp.onHide();
        }
    }
    // ---------------------------------------------------

    public void render(SpriteBatch batch)
    {
        if(bg != null)
            bg.render(batch);

        if(scene != null)
            scene.render(batch);

        if(fg != null)
            fg.render(batch);
    }


    public void resize(int w, int h)
    {
        if(this.sw == w && this.sh == h)
            return;

        this.sw = w;
        this.sh = h;

        if(scene != null)
            scene.resize(sw, sh);

        if(bg != null)
            bg.resize(sw, sh);

        if(fg != null)
            fg.resize(sw, sh);
    }

    public void update(float dt)
    {
        if(scene != null)
            scene.update(dt);

        if(bg != null)
            bg.update(dt);

        if(fg != null)
            fg.update(dt);
    }


    public boolean touch(int x, int y, boolean down, boolean drag)
    {
        if(down && !drag)
            touch_ignore = false;
        else if(touch_ignore)
            return false;

        boolean ret = (scene == null) ? false : scene.touch(x, y, down, drag);

        if(!ret && bg != null)
            ret = bg.touch(x, y, down, drag);
        return ret;
    }

    public boolean type(int key, boolean down)
    {
        boolean ret = (scene == null) ? false : scene.type(key, down);

        if(!ret && bg != null)
            ret = bg.type(key, down);

        return ret;

    }
}
