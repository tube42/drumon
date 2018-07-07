
package se.tube42.drum.audio;

/*
 * ring buffer for fast mac operations:
 *
 * remove the bound check from the inner-loop by making two
 * adjacent copies of the buffer. Works even better with SIMD...
 *
 * (already patented, sorry)
 */

public final class Ring
{
    private int curr, size;
    private float []data;

    public Ring(int size)
    {
        this.size = Math.max(4, size);
        this.data = new float[size * 2];
        reset();
    }

    public void reset()
    {
        curr = 0;
        for(int i = 0; i < data.length; i++)
            data[i] = 0;
    }

    public void write(final float val)
    {
        data[curr] = data[curr + size] = val;
        if(curr == 0) curr = size;
        curr--;
    }

    public float mac(final float [] v)
    {

        float sum0, sum1, sum2, sum3;
        float ret = 0;
        final int len = v.length;
        final int len4 = len & ~3;


        int c = curr;
        sum0 = sum1 = sum2 = sum3 = 0;

        for(int i = 0; i < len4; ) {
            sum0 += data[c++] * v[i++];
            sum1 += data[c++] * v[i++];
            sum2 += data[c++] * v[i++];
            sum3 += data[c++] * v[i++];
        }

        for(int i = len4; i < len; ) {
            sum0  += data[ c++] * v[i++];
        }

        return sum0 + sum1 + sum2 + sum3;
    }

}
