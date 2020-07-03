
package se.tube42.drum.audio;

/*
 * Lo-fi creates a rough sound
 * by removing information from the sounds
 */

public final class Lofi extends Effect
{
    public static final int
          PARAM_BITS = 0
          ;

    private int mask;

    public Lofi()
    {
        super(1);
        configure(PARAM_BITS, 2, 15);
        reset();
    }

    // -------------------------------------------------

    public void reset()
    {
        set(PARAM_BITS, 7);
    }

    public String getLabel(int i)
    {
        return "bits removed";
    }

    protected void onUpdate(int index, float f)
    {
        final int bits = (int)(0.5f + get(PARAM_BITS));
        this.mask = (1 << bits) -1;
    }

    // -------------------------------------------------

    public void process(final float [] data, int offset, int size)
    {
        for(int i = size / 4; i != 0; i--) {

            /* average of 4 samples as a short */
            final int avg = (int)( (1 << 13) *
                      (data[offset + 0] + data[offset + 1] +
                       data[offset + 2] + data[offset + 3])
                      );

            /* reduce bit size */
            final int reduced = avg & ~mask;

            /* back to float */
            final float out = reduced / (float)(1 << 15);
            data[offset ++] =
            data[offset ++] =
            data[offset ++] =
            data[offset ++] = out;
        }
    }
}
