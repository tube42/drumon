
package se.tube42.drum.audio;

import se.tube42.drum.data.*;
import static se.tube42.drum.data.Constants.*;

/*
 * simple delay effect
 */

public final class Delay
{
    private float amp;
    private float []buffer;
    private int curr, len;


    public Delay(int freq, float time, float amp)
    {
        this.amp = amp;
        this.curr = 0;
        this.len = ~3 & (int)(3 + Math.max(3, freq * time));
        this.buffer = new float[len];
    }


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
