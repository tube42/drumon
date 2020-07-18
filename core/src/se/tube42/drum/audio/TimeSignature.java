
package se.tube42.drum.audio;

import static se.tube42.drum.data.Constants.*;


public final class TimeSignature {

    public static final int
        TS_44 = 0,
        TS_34 = 1,
        TS_88 = 2,
        TS_68 = 3,
        TS_344 = 4,
        TS_334 = 5,
        TS_388 = 6,
        TS_368 = 7,

        TS_COUNT = 8
        ;



    private int ts;

    public TimeSignature() {
        this.ts = TS_44;
    }

    public int getTimeSignature() {
        return ts;
    }

    public void setTimeSignature(int ts) {
        if(ts < 0)
            ts = 0;
        else if(ts >= TS_COUNT)
            ts = TS_COUNT - 1;

        this.ts = ts;
    }

    public int nextTimeSignature() {
        ts = ts + 1;
        if(ts >= TS_COUNT)
            ts = 0;

        // DEBUG
        System.out.println("TIME SIGNATURE IS: " + ts + " " + getColumns() + " " + getRows() + " " + isQuaver());
        /*
        for(int y = 0; y < 4; y++) {
            String line = "";
            for(int x = 0; x < 8; x++) {
                line = line + (beatPlays(y * 8 + x) ? "x " : ". ");
            }
            System.out.println(line);
        }*/


        return ts;
    }

    public int getColumns() {
        switch(ts) {
            case TS_334:
            case TS_34: return 3;
            case TS_344:
            case TS_44: return 4;
            case TS_368:
            case TS_68: return 6;
            case TS_388:
            case TS_88: return 8;
            default: return 4;
        }
    }

    public int getRows() {
        switch(ts) {
            case TS_344:
            case TS_334:
            case TS_388:
            case TS_368: return 3;
            default: return 4;
        }
    }

    // Quaver( -> eight-note resolution instead of quarter-notes (i.e. when bcnt is an odd number)
    public boolean isQuaver() {
        switch(ts) {
            case TS_368:
            case TS_388:
            case TS_88:
            case TS_68: return true;
            default: return false;
        }
    }

    public int beatCorrection(int b) {
        if((ts == TS_34 || ts == TS_334 || ts == TS_68 || ts == TS_368)) {
            while((b & 7) >= 6)
                b++;
            b &= 31;
        }

        if( b / 8 >= getRows()) {
            b = 0;
        }

        return b;
    }


    public boolean beatPlays(int b)
    {
        if(b < 0 || b > 31)
            return false;

        if( (b&1) != 0 && !isQuaver())
            return false;

        final int rows = b / 8;
        if( rows >= getRows())
            return false;

        final int cols = (b & 7) >> ( isQuaver() ? 0 : 1);
        if(cols >= getColumns())
            return false;

        return true;
    }


    // returns the time until next beat given current configuration
    public float beatTime(int tempo, int tempo_mul)
    {
        float t = 30.0f / (tempo << tempo_mul);  // temp is in minutes, we use half-beats to handle x/8

        if(isQuaver() )
            t /= 2;
        return t;
    }

}
