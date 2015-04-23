
package se.tube42.lib.scene;

import java.util.*;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

import se.tube42.lib.tweeny.*;
import se.tube42.lib.ks.*;

import se.tube42.lib.item.*;

public class ParticleLayer extends Layer
{

    private Particle pool_head, active_head;

    public ParticleLayer()
    {
        flags = FLAG_UPDATE | FLAG_VISIBLE;
        pool_head = null;
        active_head = null;
    }



    public synchronized Particle create(float delay, float lifespan)
    {
        Particle ret = pool_alloc();
        ret.lifeSpan = lifespan;
        ret.delay = delay;
		active_add(ret);
        return ret;
    }


    // pool operations
    private Particle pool_alloc()
    {
 		Particle ret = pool_head;
        if(ret == null) {
			ret = new Particle();
        } else {
            pool_head = ret.next;
        }
        ret.reset();
        return ret;
    }

    private void pool_free(Particle p)
    {
        p.next = pool_head;
        pool_head = p;
    }

	// --------------------------------------------
    private void active_add(Particle p)
    {
        p.next = active_head;
        active_head = p;
    }
    private void active_remove(Particle p)
    {
    	// TODO
    }
	// --------------------------------------------

    protected void onRemove(Particle p)
    {
        /* subclass this from post-removal logic */
    }

    // --------------------------------------------

    // disabled stuff
    public final Layer add(BaseItem [] bi) { return this; }
    public final Layer add(BaseItem bi) { return this; }
    public final int getSize() { return 1; }
    public final BaseItem get(int index) { return null; }
    public final BaseItem hit(float x, float y) { return null; }
    public final void clear() { }
    // public final void remove(BaseItem item) { }

    // -----------------------------------------

    public void killAllParticles()
    {
    	for(Particle curr = active_head; curr != null; curr = curr.next) {
    		curr.lifeSpan = 0;
    	}
    	update(0);
    }

    public synchronized void update(float dt)
    {
        if( (flags & FLAG_UPDATE) == 0 || (flags & FLAG_VISIBLE) == 0)
            return;

        Particle curr = active_head;
        Particle last = null;
        while(curr != null) {
            Particle next = curr.next;
            if(!curr.isDead()) {
                curr.update(dt);
                last = curr;
            } else {
                // pool_return it!
                if(last == null) {
                    active_head = next;
                } else {
                    last.next = next;
                }
                onRemove(curr);
                pool_free(curr);
            }
            curr = next;
        }
    }

    public void draw(SpriteBatch sb)
    {
        if( (flags & FLAG_VISIBLE) == 0)
            return;

        Particle tmp = active_head;
        while(tmp != null) {
            tmp.draw(sb);
            tmp = tmp.next;
        }
    }
}
