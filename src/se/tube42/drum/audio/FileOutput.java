
package se.tube42.drum.audio;

import java.io.*;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.utils.*;

import se.tube42.drum.data.*;
import se.tube42.drum.logic.*;


public class FileOutput implements Output
{
    private String filename;
    private float [] data;
    private int curr, max;

    public FileOutput(String filename, float time)
    {

        this.filename = filename;
        this.max = (int)(time * World.freq);
        this.curr = 0;
        this.data = new float[max + 64];
    }

    public void open()
    {
        this.curr = 0;
    }

    public boolean write(float []buffer, int offset, int size)
    {
        for(int i = 0; i < size; i++) {
            if(curr >= max) {
                return false;
            }
            data[curr++] = buffer[offset++];
        }
        return true;
    }

    public void close()
    {
        try {
            ServiceProvider.saveSample(filename, World.freq, data);
        } catch(Exception e) {
            System.err.println("ERROR " + e);
        }

    }
}
