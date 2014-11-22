
package se.tube42.drum.data;

/*
 * this class represents a sample as a series of float data.
 *
 * NOTE that we assume a sample is SIMD_WIDTH aligned!
 */

public class Sample
{
    private float [] samples;
    private int curr;

    public Sample(float [] samples, float amp)
    {
        this.samples = samples;
        stop();

        // pre-calc amp!
        for(int i = 0; i < samples.length; i++)
            samples[i] *= amp;
    }

    //
    public void write(float [] buffer, int offset, int count)
    {
        int size = samples.length;
        int remain = Math.min(count, size - curr);


        for(int i = remain / 4; i != 0; i--) {
            final float v0 = samples[curr++];
            final float v1 = samples[curr++];
            final float v2 = samples[curr++];
            final float v3 = samples[curr++];

            buffer[offset++] += v0;
            buffer[offset++] += v1;
            buffer[offset++] += v2;
            buffer[offset++] += v3;
        }
    }

    //

    public void start()
    {
        curr = 0;
    }

    public void stop()
    {
        curr = samples.length;
    }
}
