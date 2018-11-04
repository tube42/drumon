
package se.tube42.drum.audio;

public interface SequencerListener {
    void onBeatStart(int beat);

    void onSampleStart(int beat, int sample);
}
