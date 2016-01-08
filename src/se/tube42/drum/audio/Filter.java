
package se.tube42.drum.audio;

import se.tube42.drum.data.*;
import static se.tube42.drum.data.Constants.*;

/*
 * simple two pole filter
 */

public final class Filter extends Effect
{
    public static final int
          CONFIG_FREQ = 0,
          CONFIG_RAD = 1
          ;

    private float x0, y0, y1, y2;
    private float b0, a0, a1, a2;
    private float freq, rad;

    public Filter()
    {
        reset();
    }

    // -----------------------------------------------------------------

    public void reset()
    {
        x0 = y0 = y1 = y2 = 0;
        setConfig(CONFIG_FREQ, 0.2f);
        setConfig(CONFIG_RAD, 0.5f);
    }

    public int getConfigSize()
    {
        return 2;
    }

    public void setConfig(int index, float f)
    {
        switch(index) {
        case CONFIG_FREQ:
            freq = Math.max(Math.min(f, MAX_FILTER_FREQ), MIN_FILTER_FREQ);
            update();
            break;
        case CONFIG_RAD:
            rad = Math.max(Math.min(f, MAX_FILTER_RAD), MIN_FILTER_RAD);
            update();
            break;
        }
    }

    public float getConfig(int index)
    {
        switch(index) {
        case CONFIG_FREQ:
            return freq;
        case CONFIG_RAD:
            return rad;
        default:
            return 0;
        }
    }

    private void update()
    {
        final double w = 2 * Math.PI * freq;
        final double cw = Math.cos(w);
        final double sw = Math.sin(w);

        a2 = rad * rad;
        a1 = (float)(-2 * rad * cw);
        a0 = 1;
        // b0 = 1;

        // normalize?
        final double x = 1 - rad + (a2 - rad) * cw;
        final double y = (a2 - rad) * sw;
        b0 = (float)Math.sqrt(x * x + y * y);
    }

    // -----------------------------------------------------------------

    public final float process(float in)
    {
        x0 = in;
        y0 = b0 * x0 - a1 * y1 - a2 * y2;
        y2 = y1;
        y1 = y0;
        return y0;
    }

    public final void process(final float [] data, int offset, final int size)
    {
        for(int i = size; i != 0; i -= 4) {
            data[offset] = process(data[offset]);
            data[offset + 1] = process(data[offset + 1]);
            data[offset + 2] = process(data[offset + 2]);
            data[offset + 3] = process(data[offset + 3]);
            offset+= 4;
        }
    }
}
