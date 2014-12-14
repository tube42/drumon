package se.tube42.lib.util;

import com.badlogic.gdx.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.*;

import se.tube42.lib.tweeny.*;
// import se.tube42.lib.ks.*;
import se.tube42.lib.scene.*;


/*
 * Base class for a 3D game with simplified initialization
 */
public abstract class BaseApp
implements ApplicationListener, InputProcessor
{
    public static float speed = 1f;

    public abstract void onCreate(SceneManager mgr, Item bgc);
    public abstract void onResize(int sw, int sh);
    public abstract void onUpdate(float dt, long dtl);


    public BaseApp()
    {
        UIC.init(-1, -1);
    }

    public BaseApp(int w, int h)
    {
        UIC.init(w, h);
    }


    public void onPreDraw(SpriteBatch sb)
    {

    }

    public void onPostDraw(SpriteBatch sb)
    {

    }


    // ------------------------------------------------------

    private SpriteBatch batch;
    protected OrthographicCamera camera;
    private Vector3 touch_tmp = new Vector3();
    protected SceneManager mgr;
    protected Item bgc;

    @Override
    public void create()
    {
        // make sure screen sizes are valid before we continue
        UIC.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // create initial objects
        this.batch = new SpriteBatch();
        this.camera = new OrthographicCamera();
        this.mgr = new SceneManager();
        this.bgc = new Item(3);
        // init major objects

        // make sure UIC is valid before app starts

        TweenManager.allowEmptyTweens(true);
        onCreate(mgr, bgc);

        // input handler
        Gdx.input.setInputProcessor(this);
        Gdx.input.setCatchBackKey(true);
    }

    @Override public void resize(final int w, final int h)
    {
        UIC.resize(w, h);
        resize_scene();
    }

    private final void resize_scene()
    {

        camera.setToOrtho(false, UIC.sw, UIC.sh);
        camera.update();

        onResize(UIC.sw, UIC.sh);

        // resize manager:
        mgr.resize(UIC.sw, UIC.sh);
    }


    @Override
    public void render()
    {

        // camera
        batch.setProjectionMatrix(camera.combined);


        // update
        final float dt = Math.min(0.2f, Gdx.graphics.getDeltaTime()) * speed;
        final long dtl = (long)(dt * 1000 * 1f);

        onUpdate(dt, dtl);
        mgr.update(dt);         // update scene

        // clean bg
        Gdx.gl.glClearColor( bgc.get(0), bgc.get(1), bgc.get(2), 1f );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );

        // draw scene
        batch.begin();
        onPreDraw(batch);
        mgr.render(batch);
        onPostDraw(batch);
        batch.end();
    }


    // --------------------------------------------------

    @Override
    public void pause() { }
    @Override
    public void resume() { }
    @Override
          public void dispose() { }

    // --------------------------------------------------
    public boolean mouseMoved(int screenX, int screenY) { return false; }
    public boolean scrolled(int amount) { return false; }
    public boolean keyTyped(char character) { return false; }


    public boolean keyDown(int keycode) {
        return mgr.type(keycode, true);
    }
    public boolean keyUp(int keycode) {
        return mgr.type(keycode, false);
    }

    public boolean touchUp(int x, int y, int pointer, int button)
    {
        return touch(x, y, false, false);
    }

    public boolean touchDown(int x, int y, int pointer, int button)
    {
        return touch(x, y, true, false);
    }

    public boolean touchDragged(int x, int y, int pointer)
    {
        return touch(x, y, true, true);
    }

    private boolean touch(int x, int y, boolean down, boolean drag)
    {
        // correct the Y axis direction
        touch_tmp.set(x, y, 0);
        camera.unproject(touch_tmp);
        y = (int)( 0.5f + touch_tmp.y);
        x = (int)( 0.5f + touch_tmp.x);

        return mgr.touch(x, y, down, drag);
    }

}
