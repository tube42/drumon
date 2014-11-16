
package se.tube42.drum.data;


public class Sample
{    
    private float [] samples;
    private int curr;
    
    public Sample(float [] samples, float amp)
    {        
        this.samples = samples;
        stop();
        
        for(int i = 0; i < samples.length; i++)
            samples[i] *= amp;        
    }
    
    // 
        
    public void write(float [] buffer, int offset, int count)
    {
        int size = samples.length;
        int remain = Math.min(count, size - curr);
        
        if(remain > 0) {
            int c = curr;
            for(int i = 0; i < remain; i++) {
                buffer[offset++] += samples[curr++];
            }            
        }
    }    
    
    // 
    
    public void start()
    {
        curr = 0;
    }
    
    public void stop()
    {
        curr = samples.length;
    }
}
