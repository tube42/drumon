
package se.tube42.drum.audio;

import se.tube42.drum.data.*;
import static se.tube42.drum.data.Constants.*;

/*
 * simple and dirty compressor with no memory
 */

public final class Compressor extends Effect
{
    public static final int
          PARAM_SRC = 0,
          PARAM_DST = 1;

    private float src, dst;
    private float mul1, mul2, add2;

    public Compressor()
    {
        super(2);
        configure(PARAM_SRC, 0, 1);
        configure(PARAM_DST, 0, 1);
        reset();
    }

    public void reset()
    {
        set(PARAM_SRC, 0.2f);
        set(PARAM_DST, 0.8f);
    }

    // --------------------------------------------------------

    protected void onUpdate(int index, float val)
    {
        src = get(PARAM_SRC);
        dst = get(PARAM_DST);

        // avoid div by zero
        src = Math.max(0.001f, src);
        dst = Math.max(0.001f, dst);
        src = Math.min(0.999f, src);
        dst = Math.min(0.999f, dst);

        this.mul1 = dst / src;
        this.mul2 = (1 - dst) / (1 - src);
        this.add2 = dst - src * mul2;
    }


    private final float comp(float in)
    {
        if(in < src && in > -src)
            return in * mul1;
        else if(in > 0)
            return in * mul2 + add2;
        else
            return in * mul2 - add2;
    }

    public void process(final float [] data,
              int offset, final int size)
    {
        for(int i = size / 2; i != 0; i--) {
            final float a0 = data[offset + 0];
            final float a1 = data[offset + 1];

            final float b0 = comp(a0);
            final float b1 = comp(a1);

            data[offset++] = b0;
            data[offset++] = b1;
        }
    }
}
