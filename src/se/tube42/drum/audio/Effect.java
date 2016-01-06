
package se.tube42.drum.audio;

import se.tube42.drum.data.*;
import static se.tube42.drum.data.Constants.*;

/**
 * Effect base class
 */

public abstract class Effect
{

    public Effect()
    {
    }


    // processor logic goes here
    public abstract void process(final float [] data, int offset, int size);

    public abstract void reset();

    // configuration empty implementation
    public int getConfigSize()
    {
        return 0; // nothing
    }

    public void setConfig(int index, float f)
    {
        // EMPTY
    }

    public float getConfig(int index)
    {
        return 0; // EMPTY
    }

}
