
package se.tube42.drum.audio;

import static se.tube42.drum.data.Constants.*;


public final class Measure
{
    // The beats are laid out like this:
    //  0   1   2   3   4   5   | 6   7
    //  8   9   10  11  12  13  | 14  15
    //  16  17  18  19  20  21  | 22  23
    //  24  25  26  27  28  29  | 30  31
    // the odd ones are only in 4/8  and 3/8
    // the ones after | are not in the tenary measures

    public static boolean plays(int m, int b)
    {
        if(b < 0 || b >= 32)
            return false;

        if((m == MEASURE_44 || m == MEASURE_34) && (b & 1) != 0)
            return false;

        if((m == MEASURE_34 || m == MEASURE_68) && (b & 7) >= 6)
            return false;

        return true;
    }

    public static int tenaryCorrection(int m, int b)
    {
        if((m == MEASURE_34 || m == MEASURE_68)) {
            while((b & 7) >= 6)
                b++;
            b &= 31;
        }
        return b;
    }

    public static int getNextMeasure(int m)
    {
        return (m == MEASURE_68) ? MEASURE_44 : m + 1;
    }

    // returns the time until next beat given current configuration
    public static float beatTime(int m, int tempo, int tempo_mul)
    {
        float t = 30.0f / (tempo << tempo_mul);  // temp is in minutes, we use half-beats to handle x/8

        if(m == MEASURE_88 || m == MEASURE_68)
            t /= 2;
        return t;
    }
}
