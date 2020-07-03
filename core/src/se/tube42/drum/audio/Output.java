
package se.tube42.drum.audio;

public interface Output
{
    public boolean write(float []buffer, int offset, int size);

    public void open();
    public void close();
}
