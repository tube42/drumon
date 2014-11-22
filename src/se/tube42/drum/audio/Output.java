
package se.tube42.drum.audio;

import java.io.*;



public interface Output
{
    public boolean write(float []buffer, int offset, int size);

    public void open();
    public void close();
}
