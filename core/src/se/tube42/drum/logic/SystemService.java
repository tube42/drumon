
package se.tube42.drum.logic;

import java.io.*;

import se.tube42.drum.util.Work;

/**
 * SystemService provides platform dependent services.
 *
 * the base implementation is empty.
 */

public class SystemService {
    private static SystemService instance_ = new SystemService();

    public static SystemService getInstance() {
        return instance_;
    }

    public static void setInstance(SystemService i) {
        instance_ = i;
    }

    // --

    protected SystemService() {
        // protected
    }

    public void showMessage(String msg) {
        System.out.println(msg);
    }

    public void showURL(String url) {
        System.out.println("BROWSE " + url);
    }

    // write to public file using whatever system resource is available
    public void writeFile(String suggestedName, String mimeType, Work<OutputStream> w) {
    }

    // read form public file using whatever system resource is available
    public void readFile(String suggestedName, String mimeType, Work<InputStream> r) {
    }
}
