

package se.tube42.drum.logic;

/**
 * SystemService provides platform dependent services.
 * 
 * the base implementation is empty.
 */

public class SystemService
{
    private static SystemService instance_ = new SystemService();
    
    public static SystemService getInstance()
    {
        return instance_;
    }
    
    public static void setInstance(SystemService i)
    {
        instance_ = i;
    }
    
    // --
    
    protected SystemService()
    {
        // protected
    }
    
    public boolean setClipboard(String text)
    {
        return false; // do nothing
    }
    
    public String getClipboard()
    {
        return null; // do nothing
    }
    
    public void showMessage(String msg)
    {
        System.out.println(msg);

    }
}
