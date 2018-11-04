
package se.tube42.drum.audio;

public interface Output {
    boolean write(float[] buffer, int offset, int size);

    void open();

    void close();
}
