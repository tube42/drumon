
package se.tube42.drum.audio;

import se.tube42.drum.data.*;
import static se.tube42.drum.data.Constants.*;

/*
 * simple delay effect
 */

public final class Delay extends Effect
{
    public static final int
          CONFIG_AMP = 0,
          CONFIG_TIME = 1
          ;

    private float amp, time;
    private float []buffer;
    private int freq, curr, len;

    public Delay(int freq)
    {
        this.freq = freq;
        this.buffer = new float[(int)(freq * MAX_DELAY_TIME + 2 * SIMD_WIDTH)];
        reset();
    }

    // --------------------------------------------------------

    public void reset()
    {
        curr = 0;
        setConfig(CONFIG_AMP, 0.2f);
        setConfig(CONFIG_TIME, 0.32f);
    }

    public int getConfigSize()
    {
        return 2;
    }

    public void setConfig(int index, float f)
    {
        switch(index) {
        case CONFIG_AMP:
            f = Math.max(Math.min(f, MAX_DELAY_AMP), MIN_DELAY_AMP);
            amp = f;
            break;
        case CONFIG_TIME:
            f = Math.max(Math.min(f, MAX_DELAY_TIME), MIN_DELAY_TIME);
            time = f;
            len = ~3 & (int)(3 + freq * time);
            // make sure we are not past our new buffer
            if(curr > len + 5);
                curr = len - 5;
            break;
        }
    }

    public float getConfig(int index)
    {
        switch(index) {
        case CONFIG_AMP:
            return amp;
        case CONFIG_TIME:
            return time;
        default:
            return 0;
        }
    }

    // --------------------------------------------------------

    public void process(final float [] data, int offset,
              final int size)
    {
        final int len = this.len;

        for(int i = size / 4; i != 0 ; i --) {
            final float a0 = data[offset + 0];
            final float a1 = data[offset + 1];
            final float a2 = data[offset + 2];
            final float a3 = data[offset + 3];

            final float b0 = buffer[curr + 0];
            final float b1 = buffer[curr + 1];
            final float b2 = buffer[curr + 2];
            final float b3 = buffer[curr + 3];

            final float c0 = a0 + b0 * amp;
            final float c1 = a1 + b1 * amp;
            final float c2 = a2 + b2 * amp;
            final float c3 = a3 + b3 * amp;

            buffer[curr ++] = data[offset ++] = c0;
            buffer[curr ++] = data[offset ++] = c1;
            buffer[curr ++] = data[offset ++] = c2;
            buffer[curr ++] = data[offset ++] = c3;

            if(curr >= len)
                curr = 0;
        }
    }
}
