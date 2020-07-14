
package se.tube42.drum.desktop;

import se.tube42.drum.logic.SystemService;
import se.tube42.drum.util.Work;

import java.io.*;

// services specific to the desktop, mostly dummy implementation so we can test the app on the desktop

public class DesktopService extends SystemService {

    // TODO: use system open/create dialogs in AWT or wahtever to get the filename

    @Override
    public void writeFile(String suggestedName, String mimeType, Work<OutputStream> w) {
        try {
            FileOutputStream f = new FileOutputStream("/tmp/" + suggestedName);
            w.success(f);
            f.flush();
            f.close();
        } catch (Exception e) {
            e.printStackTrace();
            w.failure(e.getMessage());
        }
    }

    @Override
    public void readFile(String suggestedName, String mimeType, Work<InputStream> r) {
        try {
            FileInputStream f = new FileInputStream("/tmp/" + suggestedName);
            r.success(f);
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
            r.failure(e.getMessage());
        }
    }
}
