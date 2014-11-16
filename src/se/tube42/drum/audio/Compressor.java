
package se.tube42.drum.audio;

import se.tube42.drum.data.*;
import static se.tube42.drum.data.Constants.*;


public final class Compressor
{        
    private float src, mul1, mul2, add2;
    
    public Compressor(float src, float dst)
    {
        // avoid div by zero 
        src = Math.max(0.001f, src);
        dst = Math.max(0.001f, dst);
        src = Math.min(0.999f, src);
        dst = Math.min(0.999f, dst);
        
        this.src = src;
        this.mul1 = dst / src;
        this.mul2 = (1 - dst) / (1 - src);
        this.add2 = dst - src * mul2;                
    }
    
    
    public void process(final float [] data, int offset, int size)
    {
        for(int i = 0; i < size; i++) {
            final float in = data[offset + i];
            float out;
            
            if(in < src && in > -src)
                out = in * mul1;
            else if(in > 0) {
                out = in * mul2 + add2;
            } else {
                out = in * mul2 - add2;
            }
            
            data[offset + i] = out;
        }
    }
}
