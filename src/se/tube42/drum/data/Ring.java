
package se.tube42.drum.data;


public final class Ring
{    
    private int curr, size;
    private float []data;
    
    public Ring(int size)
    {
        this.size = size;
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
        float ret = 0;
        
        for(int i = 0; i < v.length; i++)
            ret += data[ curr + i] * v[i];
        
        return ret;
    }

}
