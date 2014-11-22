package se.tube42.lib.service;

import se.tube42.lib.ks.*;

public class JobService
{
    private static JobManager manager = new JobManager();

    public static Job add(Job j)
    {
        return add(j, 0);
    }

    public static Job add(Job j, long delay)
    {
        synchronized(manager) {
            return manager.add(j, delay);
        }
    }

    public static Job add(MessageListener ml, long time, int msg)
    {
        synchronized(manager) {
            return manager.add(ml, time, msg);
        }
    }

    public static Job add(MessageListener ml, long time, int msg, int data0, Object data1, Object sender)
    {
        synchronized(manager) {
            return manager.add(ml, time, msg, data0, data1, sender);
        }
    }

    public static void service(long d)
    {
        synchronized(manager) {
            manager.service(d);
        }
    }
}
