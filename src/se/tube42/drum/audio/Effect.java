
package se.tube42.drum.audio;

import se.tube42.drum.data.*;
import static se.tube42.drum.data.Constants.*;

/**
 * Effect base class
 */

public abstract class Effect extends Parameters
{

    public Effect(int parameters)
    {
        super(parameters);
    }


    // processor logic goes here
    public abstract void process(final float [] data, int offset, int size);

    public abstract void reset();
}
