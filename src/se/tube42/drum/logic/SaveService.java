
package se.tube42.drum.logic;

import java.io.*;

import se.tube42.lib.service.*;

import se.tube42.drum.audio.*;
import se.tube42.drum.data.*;

import static se.tube42.drum.data.Constants.*;


public final class SaveService
{
    private final static String
          HEADER = "drumon-",
          TAIL = "drum-done",
          SAVE_NAME = "DrumonSave-"
          ;

    private final static String
          HEADER_OLD = "drum-save-v0.2",
          SAVE_NAME_OLD = "PREF-" + HEADER_OLD
          ;

    private final static int
          VERSION = 3,
          CHECKSUM_SIZE = 4,
          SAVE_SIZE = 1
          ;

    // -----------------------------------------------------------
    // storageAccess
    // -----------------------------------------------------------

    public static String getSave(int num)
    {
        String s = StorageService.load(SAVE_NAME + "." + num, (String)null);;
        if(s == null)
            s = StorageService.load(SAVE_NAME_OLD + "." + num, (String)null);;
        return s;
    }

    public static void setSave(int num, String data)
    {
        if(data != null) {
            StorageService.save(SAVE_NAME + "." + num, data);
            StorageService.flush();
        }
    }

    // ------------------------------------------------
    //  helper functions

    private static String calcChecksum(String str)
    {
        int sum = 0;

        for(int i = 0; i < str.length(); i++)
            sum = sum + (str.charAt(i) << (i & 3));

        return EncodingService.shortToHex(sum & 0xFFFF);
    }
    
    // valid save data? works only for new format
    public static boolean isValidSave(String str)
    {
        if(str == null) return false;
        str = str.trim();
        
        if(str.startsWith(HEADER)) {
            String s1 = str.substring(0, str.length() - CHECKSUM_SIZE);
            String s2 = str.substring(str.length() - CHECKSUM_SIZE);
            return s2.equals( calcChecksum(s1));
        }
        return false;        
    }
          
    // -----------------------------------------------------------
    // serial / deserial:
    //
    // valid formats are
    //   new: HEADER + <data> + CHECKSUM
    //   old: <HEADER_OLD + data  + TAIL>
    // -----------------------------------------------------------


    // serialize current program
    public static String currentToString()
    {
        try {
            final Program p = World.prog;
            final EffectChain fx = World.mixer.getEffectChain();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);

            // format
            dos.writeByte(VERSION);

            // serialize p
            dos.writeInt(p.getRawBanks());
            dos.writeShort(p.getTempo());
            dos.writeByte(p.getTempoMultiplier());
            dos.writeByte(p.getVoice());
            dos.writeInt(p.getFlags());
            for(int i = 0; i < 2; i++) dos.writeInt(0); // pad


            final int [] data = p.getRawPads();
            for(int i = 0; i < data.length; i++) {
                dos.writeInt( data[i]);
                dos.writeByte( p.getSampleVariant(i));
                dos.writeFloat( p.getVolume(i));
                dos.writeShort( p.getVolumeVariation(i));
            }

            // serialize fx
            dos.writeShort(fx.getEnabledRaw());
            dos.writeShort(0); // reserved

            Effect comp = fx.getEffect(FX_COMP);
            dos.writeFloat( comp.getConfig(0));
            dos.writeFloat( comp.getConfig(1));
            
            comp = fx.getEffect(FX_LOFI);
            dos.writeFloat( comp.getConfig(0));
            
            for(int i = 0; i < 3; i++) dos.writeFloat( 0); // reserved


            // encode and add checksum
            dos.flush();
            final String str = HEADER + EncodingService.encode64( baos.toByteArray() );
            return str + calcChecksum(str);

        } catch(Exception e) {
            System.err.println("SAVE ERROR: " + e);
            return null;
        }
    }

    // load current program from a string
    public static boolean stringToCurrent(String str)
    {
        if(str == null || str.length() < 20)
            return false;

        // in case this was a copy-paste, we may have extra spaces
        str = str.trim();


        // check header, see if its the new format
        boolean oldformat = true;

        if(str.startsWith(HEADER)) {
            oldformat = false;
            String s1 = str.substring(0, str.length() - CHECKSUM_SIZE);
            String s2 = str.substring(str.length() - CHECKSUM_SIZE);
            if(!s2.equals( calcChecksum(s1)))
               return false;
            else
               str = s1.substring(HEADER.length());
        }
        try {

            final Program p = World.prog;
            final EffectChain fx = World.mixer.getEffectChain();

            byte [] bytes = EncodingService.decode64(str);
            if(bytes == null)  {
                System.err.println("ERROR: could not decode string: " + str);
                return false;
            }

            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            DataInputStream dis = new DataInputStream(bais);

            if(oldformat) {
                String header = dis.readUTF();
                if(!HEADER_OLD.equals(header)) {
                    System.err.println("Internal error: bad save header - " + header);
                    return false;
                } else {
                    System.out.println("NOTE: save file is in old format");
                }
            }

            // get version
            int version = 0;
            if(!oldformat)
                version = dis.readByte();

            // deserialze to p
            p.setRawBanks( dis.readInt());
            p.setTempo( dis.readShort() );
            p.setTempoMultiplier( dis.readByte() );
            p.setVoice( dis.readByte() );
            p.setFlags( dis.readInt() );
            for(int i = 0; i < 2; i++) dis.readInt(); // pad


            final int [] data = p.getRawPads();
            for(int i = 0; i < data.length; i++) {
                data[i] = dis.readInt();
                p.setSampleVariant(i, dis.readByte());
                p.setVolume(i, dis.readFloat());
                p.setVolumeVariation(i, dis.readShort());
            }


            // deserialize fx
            fx.setEnabledRaw( dis.readShort());
            dis.readShort(); // reserved

            Effect comp = fx.getEffect(FX_COMP);
            comp.setConfig(0, dis.readFloat() );
            comp.setConfig(1, dis.readFloat() );
            
            comp = fx.getEffect(FX_LOFI);
            comp.setConfig(0, dis.readFloat() );
            
            for(int i = 0; i < 3; i++) dis.readFloat(); // reserved

            if(oldformat) {
                if(!TAIL.equals(dis.readUTF())) {
                    System.err.println("Internal error: bad save tail");
                    return false;
                }
            }

            return true;
        } catch(Exception e) {
            System.err.println("LOAD ERROR: " + e);
            return false;
        }
    }

    // -----------------------------------------------------------
    // load / save
    // -----------------------------------------------------------

    // save this
    public static boolean save(int num)
    {
        final String data = currentToString();
        if(data != null) {
            setSave(num, data);
            return true;
        } else {
            return false;
        }
    }


    // load from a save
    public static boolean load(int num)
    {
        final String str = getSave(num);
        return str == null ? false : stringToCurrent(str);
    }
}
