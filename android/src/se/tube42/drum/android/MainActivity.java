package se.tube42.drum.android;

import android.app.Activity;
import android.os.Bundle;
import android.net.Uri;
import android.content.Intent;
import android.widget.Toast;

import android.content.*;
import android.media.*;

import java.io.*;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import se.tube42.lib.service.*;

import se.tube42.drum.*;
import se.tube42.drum.logic.*;
import se.tube42.drum.data.*;
import se.tube42.drum.util.*;

public class MainActivity extends AndroidApplication {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            String s1 = am.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
            String s2 = am.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);

            if (s1 != null && s2 != null) {
                World.freq = Integer.parseInt(s1);
                World.samples = Integer.parseInt(s2);
                System.out.println("AudioManager suggested fs=" + World.freq + ", samples=" + World.samples);
            }

        } catch (Throwable t) {
            System.err.println("Could not get device defaults: " + t.toString());
        }

        SystemService.setInstance(new AndroidService(this));

        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useAccelerometer = false;
        cfg.useCompass = false;
        cfg.useWakelock = true;
        initialize(new DrumApp(), cfg);
    }

    public void onPause() {
        super.onPause();
        StorageService.flush();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    // ------------------------------------------------------------------------
    // all this complexity is so we can read/write public files without
    // asking for android storage permission

    private static final int REQ_WORK_READ = 100, REQ_WORK_WRITE = 101;
    private Work<OutputStream> write_file_work;
    private Work<InputStream> read_file_work;

    void writeFile(String suggestedName, String mimeType, Work<OutputStream> w) {
        // we use ACTION_CREATE_DOCUMENT to get a file, but since on some versions
        // Bundle is
        // dropped, we need an internal refernece to keep the user data (in this case
        // the work callback)

        this.write_file_work = w;
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT).addCategory(Intent.CATEGORY_OPENABLE)
                .putExtra(Intent.EXTRA_TITLE, suggestedName);
        if (mimeType != null)
            intent.setType(mimeType);
        startActivityForResult(intent, REQ_WORK_WRITE);

    }

    void readFile(String suggestedName, String mimeType, Work<InputStream> r) {
        this.read_file_work = r;
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT).addCategory(Intent.CATEGORY_OPENABLE)
                .putExtra(Intent.EXTRA_TITLE, suggestedName);
        if (mimeType != null)
            intent.setType(mimeType);

        startActivityForResult(intent, REQ_WORK_READ);
    }

    @Override
    protected void onActivityResult(int req, int result, Intent data) {

        if (req == REQ_WORK_WRITE && write_file_work != null) {
            try {
                if(result == RESULT_OK) {
                    Uri uri = data.getData();
                    OutputStream stream = getContentResolver().openOutputStream(uri);
                    write_file_work.success(stream);
                    stream.flush();
                    stream.close();
                } else {
                    write_file_work.failure("Could not get file");
                }
            } catch (IOException e) {
                e.printStackTrace();
                write_file_work.failure(e.getMessage());
            }
            write_file_work = null;
        } else if (req == REQ_WORK_READ && read_file_work != null) {
            try {
                if(result == RESULT_OK) {
                    Uri uri = data.getData();
                    InputStream stream = getContentResolver().openInputStream(uri);
                    read_file_work.success(stream);
                    stream.close();
                } else {
                    read_file_work.failure("Could not get file");
                }
            } catch (IOException e) {
                e.printStackTrace();
                read_file_work.failure(e.getMessage());
            }
            read_file_work = null;
        } else {
            super.onActivityResult(req, result, data);
        }
    }
}
