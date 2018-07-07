
package se.tube42.drum.audio;

import se.tube42.drum.data.*;
import static se.tube42.drum.data.Constants.*;

/*
 * simple delay effect
 */

public final class Delay extends Effect
{
    public static final int
          PARAM_TIME = 0,
          PARAM_AMP = 1
          ;

    private float amp, time;
    private float []buffer;
    private int freq, curr, len;

    public Delay(int freq)
    {
        super(2);
        configure(PARAM_AMP, 0.1f, 0.8f);
        configure(PARAM_TIME, 0.05f, 1.0f);
        this.freq = freq;
        this.buffer = new float[(int)(freq * getMax(PARAM_TIME) + 2 * SIMD_WIDTH)];
        reset();
    }

    // --------------------------------------------------------

    public void reset()
    {
        curr = 0;
        set(PARAM_AMP, 0.2f);
        set(PARAM_TIME, 0.32f);
    }

    public String getLabel(int i)
    {
        return (i == PARAM_AMP) ? "feedback" : "delay";
    }

    protected void onUpdate(int index, float f)
    {
        switch(index) {
        case PARAM_AMP:
            amp = f;
            break;
        case PARAM_TIME:
            time = f;
            len = ~3 & (int)(3 + freq * time);
            // make sure we are not past our new buffer
            if(curr > len + 5);
                curr = len - 5;
            break;
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
