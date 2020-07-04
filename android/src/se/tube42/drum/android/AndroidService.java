package se.tube42.drum.android;

import android.app.*;
import android.content.*;
import android.net.*;
import android.widget.*;

import se.tube42.drum.logic.*;

public class AndroidService extends SystemService
{
    private Activity activity;
    private ClipboardManager cman;

    public AndroidService(Activity activity)
    {
        this.activity = activity;
        this.cman = (ClipboardManager)
              activity.getSystemService(Context.CLIPBOARD_SERVICE);
    }


    // -----------------------------------------------------

    public void showURL(String url)
    {
        Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(browser);
    }

    public boolean setClipboard(String text)
    {
        try {
            cman.setPrimaryClip( ClipData.newPlainText("drumon data", text) );
            return true;
        } catch(Exception e) {
            System.err.println(e);
            return false;
        }
    }

    public String getClipboard()
    {
        try {
            final ClipData clip = cman.getPrimaryClip();
            if(clip != null) {
                final ClipData.Item item = clip.getItemAt(0);
                final CharSequence text = item.getText();
                return text == null ? null : text.toString();
            }
        } catch(Exception e) {
            System.err.println(e);
        }
        return null;
    }

    public void showMessage(final String msg)
    {
        try {
            final Runnable r = () -> {
                Toast t = Toast.makeText(activity, msg, Toast.LENGTH_SHORT);
                t.show();
            };

            activity.runOnUiThread(r);
        } catch(Exception e) {
            System.err.println(e);
        }
    }

}
