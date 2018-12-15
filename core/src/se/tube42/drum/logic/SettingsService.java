
package se.tube42.drum.logic;

import java.io.*;
import java.util.Set;

import se.tube42.lib.service.*;

import se.tube42.drum.audio.*;
import se.tube42.drum.data.*;

import static se.tube42.drum.data.Constants.*;


public final class SettingsService
{
    private final static String
          HEADER = "drumon-settings",
          SETTINGS_NAME = "DrumonSettings"
          ;

    private final static int
		  VERSION = 1
		  ;

    // -----------------------------------------------------------
    // load / save
    // -----------------------------------------------------------

    public static void save()
    {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			// format
			dos.writeByte(VERSION);

			// format flags
			int fileflags = 0;
			dos.writeInt(fileflags);

			dos.writeBoolean(Settings.bg_play);

			// reserved for future
			dos.writeBoolean(false);
			dos.writeBoolean(false);
			dos.writeInt(0);
			dos.writeInt(0);
			dos.writeUTF("");
			dos.writeUTF("");

			final String str = HEADER + EncodingService.encode64( baos.toByteArray());
			StorageService.save(SETTINGS_NAME, str);
			StorageService.flush();
		} catch(IOException e) {
			System.err.println("ERROR: could not save settings: " + e);
		}
    }

    // load from a save
    public static boolean load()
    {
		String str = StorageService.load(SETTINGS_NAME, null);
		if(str == null)
			return false;

		// check header
		if(!str.startsWith(HEADER))
            return false;
        str = str.substring(HEADER.length());

		byte [] bytes = EncodingService.decode64(str);
		if(bytes == null)  {
			System.err.println("ERROR: could not decode settings string: " + str);
			return false;
		}

		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			DataInputStream dis = new DataInputStream(bais);

            final byte version = dis.readByte();
			final int fileflags = dis.readInt();
			Settings.bg_play = dis.readBoolean();

		} catch(IOException e) {
			System.err.println("Could not load settings: " + e);
			return false;
		}
		return true;
    }
}
