
package se.tube42.drum.logic;

/**
 * SystemService provides platform dependent services.
 * <p>
 * the base implementation is empty.
 */

public class SystemService {
    private static SystemService instance_ = new SystemService();

    protected SystemService() {
        // protected
    }

    public static SystemService getInstance() {
        return instance_;
    }

    // --

    public static void setInstance(SystemService i) {
        instance_ = i;
    }

    public boolean setClipboard(String text) {
        return false; // do nothing
    }

    public String getClipboard() {
        return null; // do nothing
    }

    public void showMessage(String msg) {
        System.out.println(msg);
    }
}
