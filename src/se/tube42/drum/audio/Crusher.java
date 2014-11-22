
package se.tube42.drum.audio;

import se.tube42.drum.data.*;
import static se.tube42.drum.data.Constants.*;

/*
 * Crusher creates a rough sound
 * by removing information from the sounds
 */

public final class Crusher
{

    public Crusher()
    {

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
            final int reduced = avg & ~0x007F;

            /* back to float */
            final float out = reduced / (float)(1 << 15);


            data[offset ++] =
            data[offset ++] =
            data[offset ++] =
            data[offset ++] = out;
        }
    }
}
