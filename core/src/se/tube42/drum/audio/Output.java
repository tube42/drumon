
package se.tube42.drum.audio;

import java.io.*;



public interface Output
{
    boolean write(float []buffer, int offset, int size);

    void open();
    void close();
}
