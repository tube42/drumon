
package se.tube42.drum.audio;

import java.io.*;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.utils.*;

import se.tube42.drum.data.*;
import static se.tube42.drum.data.Constants.*;

/*
 * mixer is the audio loop.
 * It request new samples from the sequencer and
 * feeds them in the effect chain and out to the
 * speakers
 */

public final class Mixer
implements Runnable, Disposable
{
    private AudioDevice ad;
    private Thread thread;

    private int buffer_size;
    private float [] buffer;
    private boolean stopped;

    private Profiler prof;

    private Output output;
    private EffectChain chain;

    public Mixer(Output output)
    {
        this.buffer_size = (World.samples + SIMD_WIDTH - 1) & ~(SIMD_WIDTH - 1);
        this.buffer = new float[buffer_size];
        System.out.println("buffer-size=" + buffer_size +
                  " SIMD-width=" + SIMD_WIDTH);

        this.output = output;
        this.chain = new EffectChain();
        this.prof = new Profiler(20);
        this.thread = new Thread(this);
    }

    public void start()
    {
        stopped = false;
        output.open();
        thread.start();
    }

    public void stop()
    {
        output.close();
        stopped = true;
    }

    public void dispose()
    {
        output.close();
        stop();
    }

    // ---------------------------------------------------------

    public EffectChain getEffectChain()
    {
        return chain;
    }

    public Output getOutput()
    {
        return output;
    }

    public void setOutput(Output o) {
        output = o;
    }


    // ---------------------------------------------------------

    public void run()
    {

        while(!stopped)  {

            for(int i = 0; i < buffer_size; i++) {
                buffer[i] = 0;
            }

            while(World.seq.isPaused() && !stopped) {
                stopped |= !output.write(buffer, 0, buffer_size);
            }

            int cnt = World.seq.nextBeat();

            /* ensure it aligns to SIMD with! */
            if(cnt < buffer_size) {
                cnt = (cnt + SIMD_WIDTH / 2 - 1) & ~(SIMD_WIDTH - 1);
            }

            /* dont go outside buffer count */
            if(cnt >= buffer_size) {
                cnt = buffer_size;
            }

            if(DEBUG) {
                prof.start(cnt); /* profiler started */
            }


            /* process samples */
            World.seq.update(cnt);
            for(int i = 0; i < World.sounds.length ; i++) {
                World.sounds[i].write(buffer, 0, cnt);
            }

            final int rem = buffer_size - cnt;
            if(rem > 0) {
                World.seq.update(rem);
                for(int i = 0; i < World.sounds.length ; i++) {
                    World.sounds[i].write(buffer, cnt, rem);
                }
            }

            /* process effects */
            chain.process(buffer, 0, buffer_size);


            if(DEBUG) {
                prof.finish(); /* profiler finished */
            }

            /* write results */
            stopped |= !output.write(buffer, 0, buffer_size);
        }
    }

}
