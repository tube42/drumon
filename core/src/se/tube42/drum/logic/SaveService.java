
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
          SAVE_NAME = "DrumonSave-A-"
          ;

    private final static int
          VERSION = 4,
          CHECKSUM_SIZE = 4,
          SAVE_SIZE = 1
          ;

    // -----------------------------------------------------------
    // storageAccess
    // -----------------------------------------------------------

    public static String getSave(int num)
    {
        return StorageService.load(SAVE_NAME + "." + num, null);
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
    //   format: HEADER + <data> + CHECKSUM
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

            // format flags
            int fileflags = 0;

            dos.writeInt(fileflags);

            // serialize p
            dos.writeInt(p.getRawBanks());
            dos.writeShort(p.getTempo());
            dos.writeByte(p.getTempoMultiplier());
            dos.writeByte(p.getVoice());
            dos.writeInt(p.getRawFlags());

            for(int i = 0; i < VOICES; i++) {
                final int banks = p.getUsedBanks(i);

                int voiceflags = 0;
                voiceflags |= (0x0F & p.getSampleVariant(i)) << 0;
                voiceflags |= (0xFF & p.getVolumeVariation(i)) << 4;
                voiceflags |= (0x0F & banks) << 12;

                dos.writeInt(voiceflags);
                dos.writeFloat( p.getVolume(i));
                for(int j = 0; j < banks; j++)
                    dos.writeInt( p.getRawData(j, i));
            }

            // serialize fx
            int fxcount = 0x2221;
            int fxflags = 0;
            fxflags |= 0x0F & fx.getEnabledRaw();
            fxflags |= fxcount << 4;
            dos.writeInt(fxflags);
            for(int i = 0; i < EffectChain.SIZE; i++) {
                Effect comp = fx.getEffect(i);
                for(int j = 0; j < (fxcount & 0xF); j++)
                    dos.writeFloat( comp.get(j));
                fxcount >>= 4;
            }

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

        if(!str.startsWith(HEADER))
            return false;

        // check and remove checksum
        String s1 = str.substring(0, str.length() - CHECKSUM_SIZE);
        String s2 = str.substring(str.length() - CHECKSUM_SIZE);
        if(!s2.equals( calcChecksum(s1)))
            return false;
        str = s1.substring(HEADER.length());

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

            final byte version = dis.readByte();
            final int fileflags = dis.readInt();

            // deserialize to p
            p.setRawBanks( dis.readInt());
            p.setTempo( dis.readShort() );
            p.setTempoMultiplier( dis.readByte() );
            p.setVoice( dis.readByte() );
            p.setRawFlags( dis.readInt() );

            for(int i = 0; i < VOICES; i++) {
                final int voiceflags = dis.readInt();
                p.setVolume(i, dis.readFloat());

                final int samplevar = voiceflags & 0xF;
                final int volvar = (voiceflags >> 4) & 0xFF;
                final int banks = (voiceflags >> 12) & 0x0F;

                p.setSampleVariant(i, samplevar);
                p.setVolumeVariation(i, volvar);
                for(int j = 0; j < VOICE_BANKS; j++) {
                    int data = 0;
                    if(j < banks)
                        data = dis.readInt();
                    p.setRawData(j, i, data);
                }
            }

            // deserialize fx
            final int fxflags = dis.readInt();
            final int fxenabled = 0x0F & fxflags;
            int fxcount = (fxflags >> 4) & 0xFFFF;
            fx.setEnabledRaw( fxenabled);

            for(int i = 0; i < EffectChain.SIZE; i++) {
                Effect comp = fx.getEffect(i);
                comp.reset();
                for(int j = 0; j < (fxcount & 0xF); j++)
                    comp.set(j, dis.readFloat() );
                fxcount >>= 4;
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
        return stringToCurrent(str);
    }
}
