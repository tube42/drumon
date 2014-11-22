package se.tube42.lib.service;

import java.util.*;

public final class RandomService
{

    private final static Random r = new Random();

    public static boolean flipCoin()
    {
        return r.nextBoolean();
    }
    public static float get()
    {
        return r.nextFloat();
    }

    public static float get(float min, float max)
    {
        float d = max - min;
        return min + d * get();
    }

    public static int getInt(int max)
    {
        return r.nextInt(max);
    }

    public static int getFromDistribution(int [] dist)
    {
        int max = 0;
        for(int i = 0; i < dist.length; i++)
            max += dist[i];

        if(max <= 0) return 0; // EROR

        int r = getInt(max);

        for(int i = 0; i < dist.length; i++) {
            r -= dist[i];

            if(r < 0)
                return i;
        }

        System.out.println("ERROR!\n");
        return 0; // ERROR
    }
}
