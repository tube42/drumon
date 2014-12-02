
package se.tube42.drum.data;

/*
 * this class represents a sample as a series of float data.
 *
 * NOTE that we assume a sample is SIMD_WIDTH aligned!
 */

public class Sample
{

    private float [][] data;
    private float amp;
    private int variant, curr, safe_end;

    public Sample(float [][] data)
    {
        this.data = data;
        this.variant = 0;
        this.amp = 1;

        // get the end of the biggest sample
        this.safe_end = 0;
        for(int i = 0; i < data.length; i++) {
            safe_end = Math.max(safe_end, data[i].length);
        }

        stop();
    }

    //
    public void write(float [] buffer, int offset, int count)
    {
        final float [] ss = this.data[variant];
        final int size = ss.length;
        final int remain = Math.min(count, size - curr);
        final float amp = this.amp;
        if(remain <= 0) return;

        for(int i = remain / 4; i != 0; i--) {
            final float v0 = ss[curr++];
            final float v1 = ss[curr++];
            final float v2 = ss[curr++];
            final float v3 = ss[curr++];

            buffer[offset++] += v0 * amp;
            buffer[offset++] += v1 * amp;
            buffer[offset++] += v2 * amp;
            buffer[offset++] += v3 * amp;
        }
    }

    //
    public void start(int variant, float amp)
    {
        if(variant >= 0 && variant < data.length) {
            this.amp = amp;
            this.variant = variant;
            this.curr = 0;
        }
    }

    public void stop()
    {
        curr = safe_end;
    }

    //
    public float getAmp()
    {
        return amp;
    }

    //
    public int getCurrentVariant()
    {
        return variant;
    }
    public int getNumOfVariants()
    {
        return data.length;
    }

}
