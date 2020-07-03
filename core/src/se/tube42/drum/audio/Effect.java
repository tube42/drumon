
package se.tube42.drum.audio;

import se.tube42.drum.data.*;

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
