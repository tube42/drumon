
package se.tube42.drum.audio;

import se.tube42.drum.data.*;
import static se.tube42.drum.data.Constants.*;

/*
 * Lo-fi creates a rough sound
 * by removing information from the sounds
 */

public final class Lofi extends Effect
{
    private int bits, mask;
    
    public Lofi()
    {
        setConfig(0, 7);
    }
    
    public int getConfigSize()
    {
        return 1;
    }

    public void setConfig(int index, float f)
    {
        if(index == 0) {
            final int bits = (int) Math.max(MIN_LOFI_BITS,
                      Math.min(MAX_LOFI_BITS, f));
            this.bits = bits;
            this.mask = (1 << bits) -1;
        }
    }

    public float getConfig(int index)
    {
        return (float)bits;
    }

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
