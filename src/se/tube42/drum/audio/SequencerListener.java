
package se.tube42.drum.audio;


public interface SequencerListener
{
	public void onBeatStart(int beat);
	public void onSampleStart(int beat, int sample);
}
