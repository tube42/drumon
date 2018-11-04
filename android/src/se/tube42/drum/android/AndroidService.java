package se.tube42.drum.android;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

import se.tube42.drum.logic.SystemService;

public class AndroidService extends SystemService {
    private Activity activity;
    private ClipboardManager cman;

    public AndroidService(Activity activity) {
        this.activity = activity;
        this.cman = (ClipboardManager)
                activity.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    // -----------------------------------------------------

    public boolean setClipboard(String text) {
        try {
            cman.setPrimaryClip(ClipData.newPlainText("drumon data", text));
            return true;
        } catch (Exception e) {
            System.err.println(e);
            return false;
        }
    }

    public String getClipboard() {
        try {
            final ClipData clip = cman.getPrimaryClip();
            if (clip != null) {
                final ClipData.Item item = clip.getItemAt(0);
                final CharSequence text = item.getText();
                return text == null ? null : text.toString();
            }
        } catch (Exception e) {
            System.err.println(e);
        }
        return null;
    }

    public void showMessage(final String msg) {
        try {
            final Runnable r = () -> {
                Toast t = Toast.makeText(activity, msg, Toast.LENGTH_SHORT);
                t.show();
            };

            activity.runOnUiThread(r);
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
