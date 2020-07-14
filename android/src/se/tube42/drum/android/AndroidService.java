package se.tube42.drum.android;

import android.app.*;
import android.widget.*;
import android.text.*;

import android.content.*;
import android.net.*;

import java.io.*;

import se.tube42.drum.logic.*;
import se.tube42.drum.util.*;

public class AndroidService extends SystemService {
    private MainActivity activity;

    public AndroidService(MainActivity activity) {
        this.activity = activity;
    }

    public void showURL(String url) {
        Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(browser);
    }

    public void showMessage(final String msg) {
        try {
            final Runnable r = new Runnable() {
                public void run() {
                    Toast t = Toast.makeText(activity, msg, Toast.LENGTH_SHORT);
                    t.show();
                }
            };

            activity.runOnUiThread(r);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    @Override
    public void writeFile(String suggestedName, String mimeType, Work<OutputStream> w) {
        activity.writeFile(suggestedName, mimeType, w);
    }

    @Override
    public void readFile(String suggestedName, String mimeType, Work<InputStream> r) {
        activity.readFile(suggestedName, mimeType, r);
    }
}
