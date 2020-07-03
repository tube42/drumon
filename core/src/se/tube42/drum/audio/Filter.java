
package se.tube42.drum.audio;

/*
 * simple two pole filter
 */

public final class Filter extends Effect
{
    public static final int
          PARAM_FREQ = 0,
          PARAM_RAD = 1
          ;

    private float x0, y0, y1, y2;
    private float b0, a0, a1, a2;

    public Filter()
    {
        super(2);
        configure(PARAM_FREQ, 0, 0.3f);
        configure(PARAM_RAD, 0, 0.95f);
        reset();
    }

    // -----------------------------------------------------------------

    public void reset()
    {
        x0 = y0 = y1 = y2 = 0;
        set(PARAM_FREQ, 0.2f);
        set(PARAM_RAD, 0.5f);
    }

    public String getLabel(int i)
    {
        return (i == PARAM_FREQ) ? "frequency" : "radius";
    }

    public int getConfigSize()
    {
        return 2;
    }


    protected void onUpdate(int index, float f)
    {
        final float freq = get(PARAM_FREQ);
        final float rad = get(PARAM_RAD);

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
