
package se.tube42.lib.service;

import com.badlogic.gdx.*;

import java.util.*;

public final class StorageService
{


    private static Preferences prefs;

    public static void init(String name)
    {
        prefs = Gdx.app.getPreferences(name);
    }
    // -------------------------------------------------------------------

    public static void flush()
    {
//        System.out.println(" ** STORAGE-FLUSH");
        prefs.flush();
    }

    // ----------------------------------------------------------------

    public static void save(String key, String data)
    {
//        System.out.println(" ** SAVE-S + " + key + " " + data); // DEBUG
        prefs.putString(key, data);
    }

    public static void saveLong(String key, long data)
    {
//        System.out.println(" ** SAVE-L + " + key + " " + data); // DEBUG
        prefs.putLong(key, data);
    }

    public static void save(String key, int data)
    {
//        System.out.println(" ** SAVE-I + " + key + " " + data); // DEBUG
        prefs.putInteger(key, data);
    }

    public static void save(String key, boolean data)
    {
        prefs.putBoolean(key, data);
    }

    // ----------------------------------------------------------------

    public static String load(String key, String default_)
    {
        String ret = prefs.getString(key, default_);
//        System.out.println(" ** LOAD-S + " + key + " " + ret + "/" + default_); // DEBUG
        return ret;
    }

    public static boolean load(String key, boolean default_)
    {
        return prefs.getBoolean(key, default_);
    }

    public static int load(String key, int default_)
    {
        int ret = prefs.getInteger(key, default_);
//        System.out.println(" ** LOAD-I + " + key + " " + ret + "/" + default_); // DEBUG
        return ret;
    }

    public static long loadLong(String key, long default_)
    {
        long ret = prefs.getLong(key, default_);
//        System.out.println(" ** LOAD-L + " + key + " " + ret + "/" + default_); // DEBUG
        return ret;
    }


}
