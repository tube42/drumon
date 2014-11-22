
package se.tube42.drum.logic;

import java.io.*;

import se.tube42.lib.service.*;

public final class SampleService
{

    public static void write(String filename, int freq, float [] data)
          throws IOException
    {
        OutputStream os = new FileOutputStream(filename);
        DataOutputStream dos = new DataOutputStream(os);

        dos.writeInt(0x2e736e64);
        dos.writeInt(24);
        dos.writeInt(data.length * 2);
        dos.writeInt(3);
        dos.writeInt(freq);
        dos.writeInt(1);
        dos.writeInt(0);


        for(int i = 0; i < data.length; i++)
            dos.writeShort((short)(data[i] * (1 << 15)));

        dos.close();
        os.close();
    }

    public static float [] load(String filename, int freq, int add)
          throws IOException
    {
        final InputStream is = ServiceProvider.readFile(filename);
        final DataInputStream dis = new DataInputStream(is);

        int [] hdr = new int[6];
        for(int i = 0; i < hdr.length; i++)
            hdr[i] = dis.readInt();

        if(hdr[0] != 0x2e736e64 || hdr[3] != 3 || hdr[5] != 1)
            throw new IOException("BAD FORMAT!");

        final int freq_in = hdr[4];
        final int size = hdr[2] / 2;
        final int size_a = (size + add + 63) & ~ 63;
        final float [] samples = new float[size_a];

        for(int i = 0; i < hdr[1] - 24; i++)
            dis.readByte();

        for(int i = 0; i < size; i++)
            samples[i] = dis.readShort() / (float) (1 << 15);

        for(int i = size; i < size_a; i++)
            samples[i] = 0;

        // cleanup
        dis.close();
        is.close();

        // resample
        final float [] resampled = resample(samples, freq_in, freq);
        return resampled;
    }


    public static float [] resample(float [] src, int fin, int fout)
    {
        if(fin == fout)
            return src;

        while(fin > fout * 2) {
            src = sample_half(src);
            fin /= 2;
        }

        return sample_fract(src, fin, fout);
    }

    // -------------------------------------------

    private static float [] sample_half(float [] src)
    {

        final int slen = src.length;
        final int dlen = slen / 2;
        float [] ret = new float[dlen];

        for(int i = 0; i < dlen; i++)
            ret[i] = (src[i * 2 + 0] +  src[i * 2 + 1]) / 2;

        return ret;
    }

    private static float [] sample_fract(float [] src, int fin, int fout)
    {
        if(fin == fout)
            return src;

        // dumb GCD, to avoid int overflow later
        for(int i = 2; i < 14; i++) {
            while( (fin % i) == 0 && (fout % i) == 0) {
                fin /= i;
                fout /= i;
            }
        }

        final int slen = src.length;
        final int dlen = slen * fout / fin;
        float [] ret = new float[dlen];

        ret[0] = src[0];
        ret[dlen-1] = src[slen-1];
        for(int i = 1; i < dlen-1; i++) {
            final float j = (i * fin) / (float)fout;
            final int idx1 = (int)j;
            final float amt  = idx1 + 1 - j;

            ret[i] = src[idx1] * amt + src[idx1+1] * (1 - amt);
        }
        return ret;
    }
}
