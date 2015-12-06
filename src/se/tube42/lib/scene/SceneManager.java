package se.tube42.lib.scene;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.*;



import se.tube42.lib.ks.*;
import se.tube42.lib.service.*;

public class SceneManager
implements MessageListener
{
    public static final long
          TIME_DEFAULT_CHANGE = 500
          ;

    private static final int
          MSG_SET_SCENE = 0
          ;

    // ---------------------------------------------

    private boolean touch_ignore;
    private Scene bg, fg, scene;
    private int sw, sh;
    private boolean disable_input;

    public SceneManager()
    {
        this.touch_ignore = false;
        this.scene = null;
        this.bg = null;
        this.fg = null;
        this.disable_input = false;
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

    public void setScene(Scene s)
    {
        setScene(s, TIME_DEFAULT_CHANGE);
    }

    public synchronized void setScene(Scene s, long time)
    {
        System.out.println("[setScene] " + scene + " ==> " + s); // DEBUG

        if(s == scene) return;

        if(this.scene != null && time > 0) {
            /* hide old one */
            this.scene.onHide();

            /* show new one */
            disable_input = true;
            JobService.add(this, time,
                      MSG_SET_SCENE, 0, s, this);
        } else {
            set_scene_now(s);
        }

    }

    private void set_scene_now(Scene scene)
    {
        this.scene = scene;
        if(scene != null) {
            scene.resizeIfChanged(sw, sh);
            scene.onShow();
        }
        disable_input = false;
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


    public boolean touch(int ptr, int x, int y, boolean down, boolean drag)
    {
        if(disable_input) return false;

        if(down && !drag)
            touch_ignore = false;
        else if(touch_ignore)
            return false;

        boolean ret = (scene == null) ? false : scene.touch(ptr, x, y, down, drag);

        if(!ret && bg != null)
            ret = bg.touch(ptr, x, y, down, drag);
        return ret;
    }

    public void onPause()
    {
        if(scene != null)
            scene.onPause();
    }

    public void onResume()
    {
        if(scene != null)
            scene.onResume();
    }

    public boolean type(int key, boolean down)
    {
        if(disable_input) return false;

        boolean ret = (scene == null) ? false : scene.type(key, down);

        if(!ret && bg != null)
            ret = bg.type(key, down);

        return ret;

    }

    public void onMessage(int msg, int data0,
              Object data1, Object sender)
    {
        switch(msg) {
        case MSG_SET_SCENE:
            set_scene_now((Scene) data1);
            break;
        }
    }

}

