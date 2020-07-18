
package se.tube42.drum.audio;

import static se.tube42.drum.data.Constants.*;


public final class Hypermeasure {
    //  0   1   2   3   4   5   | 6   7
    //  8   9   10  11  12  13  | 14  15
    //  16  17  18  19  20  21  | 22  23
    //  24  25  26  27  28  29  | 30  31
    // the odd ones are only in 4/8  and 3/8
    // the ones after | are not in the tenary measures
    private int bcnt;

    public Hypermeasure() {
        reset();
    }

    public int getBeat() {
        return bcnt;
    }
    public void setBeat(int bcnt) {
        this.bcnt = bcnt;
    }

    public void reset() {
        this.bcnt = 31;
    }

    public boolean next(TimeSignature ts) {
        bcnt = (bcnt + 1) & 31;

        bcnt = ts.beatCorrection(bcnt);
        return ts.beatPlays(bcnt); // check if we skip this beat (e.g. skip odd ones in x/4)
    }
}
