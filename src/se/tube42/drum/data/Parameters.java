
package se.tube42.drum.data;

/**
 * base class for objects that have a number of parameters
 */
public class Parameters
{
    private float[] min, max, curr;
    private int size;

    public Parameters(int size)
    {
        this.size = size;
        this.min = new float[size];
        this.max = new float[size];
        this.curr = new float[size];

        // set somesort ofdefault values
        for(int i = 0; i < size; i++) {
            min[i] = 0;
            max[i] = 1;
            curr[i] = 0;
        }
    }

    public void configure(int index, float min, float max, float val)
    {
        configure(index, min, max);
        set(index, val);
    }

    public void configure(int index, float min, float max)
    {
        if(index < 0 || index >= size)
            return;

        if(min > max) {
            float tmp = min;
            min = max;
            max= tmp;
        }

        this.min[index] = min;
        this.max[index] = max;

        // this will ensure we are within limits
        set(index, curr[index]);
    }

    protected void onUpdate(int index, float val)
    {
        // EMPTY
    }

    public int getSize()
    {
        return size;
    }

    public void set(int index, float val)
    {
        if(index < 0 || index >= size)
            return;
        val = Math.min(val, max[index]);
        val = Math.max(val, min[index]);
        if(curr[index] != val) {
            curr[index] = val;
            onUpdate(index, val);
        }
    }

    public float get(int index)
    {
        return index < 0 || index >= size ? 0 : curr[index];
    }

    public int getInt(int index)
    {
        return (int)(0.5f + get(index));
    }

    public float getMin(int index)
    {
        return index < 0 || index >= size ? 0 : min[index];
    }

    public float getMax(int index)
    {
        return index < 0 || index >= size ? 0 : max[index];
    }
}
