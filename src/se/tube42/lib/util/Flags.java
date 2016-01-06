package se.tube42.lib.util;


/**
 * class representing 32 binary flags packed into an int
 */
public class Flags
{
    private int flags;
    
    public Flags()
    {
        this.flags = 0;
    }
    public Flags(int flags)
    {
        this.flags = flags;
    }
    
    public boolean getFlag(int f)
    {
        return (flags & (1 << f)) != 0;
    }
    
    public void setFlag(int f, boolean val)
    {
        if(val)
            flags |= 1 << f;
        else
            flags &= ~(1 << f);
    }
    
    public void toggleFlag(int f)
    {
        flags ^= 1 << f;
    }
    
    public int getRawFlags()
    {
        return flags;
    }
    
    public void setRawFlags(int flags)
    {
        this.flags = flags;
    }
}
