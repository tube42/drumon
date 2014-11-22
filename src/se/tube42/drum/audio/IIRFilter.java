
package se.tube42.drum.audio;

import se.tube42.drum.data.*;

/*
 * simple averaging filter
 */

public final class IIRFilter
{
    private int size;
    private float []v;
    private Ring ring;

    public IIRFilter(int size)
    {
        this.size = size;
        this.v = new float[size];
        this.ring = new Ring(size);
        reset();
    }

    public void reset()
    {
        ring.reset();
        for(int i = 0; i < v.length; i++)
            v[i] = 0;
    }

    public int getSize()
    {
        return size;
    }

    public void set(int index, float val)
    {
        this.v[index] = val;
    }

    public float process(float in)
    {
        ring.write(in);
        return ring.mac(v);
    }

    public void process(final float [] data, int offset, final int size)
    {
        for(int i = size; i != 0; i--) {
            final float a0 = data[offset];
            final float b0 = process(a0);
            data[offset++] = b0;
        }
    }
}
