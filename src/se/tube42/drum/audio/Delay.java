
package se.tube42.drum.audio;

import se.tube42.drum.data.*;
import static se.tube42.drum.data.Constants.*;


public final class Delay
{        
    private float amp;
    private float []data;
    private int curr;
    
    public Delay(int freq, float time, float amp)
    {        
        this.amp = amp;
        this.curr = 0;
        this.data = new float[ (int)Math.max(2, freq * time)];        
    }
    
    
    public void process(final float [] data, int offset, int size)
    {
        for(int i = 0; i < size; i++) {
            final float v0 = data[offset + i];
            final float v1 = this.data[curr];
            final float out = v0 + v1 * amp;
            
            this.data[curr] = data[offset + i] = out;
            curr++;
            if(curr == this.data.length)
                curr = 0;
        }
    }
}
