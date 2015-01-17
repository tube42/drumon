
package se.tube42.drum.audio;

import se.tube42.drum.data.*;
import static se.tube42.drum.data.Constants.*;

/*
 * simple and dirty compressor with no memory
 */

public final class Compressor
{
    private float src, dst;
    private float mul1, mul2, add2;

    public Compressor(float src, float dst)
    {
        configure(src, dst);
    }


    public float getSource() { return src; }
    public float getDest() { return dst; }

    public void configure(float src, float dst)
    {
        // avoid div by zero
        src = Math.max(0.001f, src);
        dst = Math.max(0.001f, dst);
        src = Math.min(0.999f, src);
        dst = Math.min(0.999f, dst);

        this.src = src;
        this.dst = dst;
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
