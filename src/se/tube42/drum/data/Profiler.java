
package se.tube42.drum.data;

/**
 * Class for time measurements, used for profiling
 */

public class Profiler
{
    private long report_time, report_delta;
    private long start_time, total_time;
    private int total_data, count_samples;

    public Profiler(int time)
    {
        this.report_delta = 1000L * (long) time;
        reset();
    }

    private long now()
    {
        return System.currentTimeMillis();
    }

    public void reset()
    {
        this.total_time = 0;
        this.total_data = 0;
        this.report_time = now() + report_delta;
        this.count_samples = 0;
    }

    public void start(int count)
    {
        start_time = now();
        total_data += count;
        count_samples++;
    }

    public void finish()
    {
        long now = now();
        long time = now - start_time;
        total_time += time;

        if(now > report_time)
            report("") ;

    }

    public void report(String message)
    {
        if(total_data <= 1) {
            System.out.println("PROFILER " +
                      message + ": not enough data");
        } else {
            float f = total_time / (float) total_data;
            System.out.println("PROFILER " +
                      message + ": "  +
                      (int)(f * 1000000 + 0.5f) +
                      " us/item");
            reset();
        }
    }
}
