
package se.tube42.drum.data;

public final class Constants
{
    public static final boolean
          DEBUG = false
          ;

    public static final String
          CHARSET1 = "0123456789%",
          CHARSET2 = "abcdefghijklmnopqrstuvwxyz!?"
          ;

    public static final int
          MAX_TEMPO = 260,
          MIN_TEMPO = 40,
          MAX_VARIATION = 75,
          MIN_VARIATION = 0,
          DEFAULT_VARIATION = 20,
          LONGPRESS_DELAY = 1500,
          NUM_SAVES = 16,

          MAX_VOLUME = 400, // %
          MIN_VOLUME = 0, // %
          SIMD_WIDTH = 4
          ;

    public static final int
          SELECTORS = 4,
          TOOLS = 4,
          VOICES = 8,
          PADS = 4 * 4 * 2
          ;
    
    public static final int
          FLAG_48 = 1
          ;
          
    // effect chain
    public static final int
          FX_CRUSH = 0,
          FX_FILTER = 1,
          FX_DELAY = 2,
          FX_COMP = 3
          ;

    public static final int
          TILE_PAD0 = 0,
          TILE_PAD1 = 1,
          TILE_MARKER = 2,
          TILE_BUTTON0 = 4,
          TILE_BUTTON1 = 5
          ;

    public static final int
          DECALS_NUM = 0,
          DECALS_ALPHA = 4
          ;

    public static final int
          ICON_KICK = 0,
          ICON_SNARE = 1,
          ICON_TOM = 2,
          ICON_CLAP = 3,
          ICON_HC = 4,
          ICON_HO = 5,
          ICON_SYMBAL = 6,
          ICON_TICK = 7,
          ICON_A = 8,
          ICON_B = 9,
          ICON_DEL = 10,
          ICON_SETTINGS = 11,
          ICON_PAUSE = 14,
          ICON_PLAY = 15,
          ICON_METRONOME = 16,
          ICON_WAVEFORM = 17,
          ICON_MIX = 18,
          ICON_SEQ = 19,

          ICON_NOTE4 = 20,
          ICON_NOTE8 = 21,
          ICON_NOTE16 = 22,
          ICON_DOT = 23,

          ICON_CRUSH = 24,
          ICON_LPFILTER = 25,
          ICON_ECHO = 26,
          ICON_COMPRESS = 27,

          ICON_VOLUME = 28,
          ICON_SAVE = 29,
          ICON_44 = 30,
          ICON_48 = 31
          ;

    public final static int
          CHOICE_TEMPO = 0
          ;

    public final static int
          CHOICE2_VOLUME = 0,
          CHOICE2_COMPRESS = 1
          ;

    // ---------------------------------------
    // samples
    // ---------------------------------------

    public static final String [][] SAMPLES = {
        { "kick2.au", "kick_hard.au", "BD1.au" },
        { "snare2.au", "SD2.au", "snare_supraphonic_hard.au", },
        { "floor_tom2.au", "HITOM4.au" },
        { "tamburin.au", "CLAPS.au" },

        { "side_stick1.au", "CHOICE_BITS_COWBELL.au","RIM.au" },
        { "closed_hat_hard.au", "HIGH_HAT_closed_soft.au", "CLHIHAT2.au" },
        { "semi_open_hat.au", "open_hat4.au", "OPHIHAT2.au" },
        { "ride_centre2.au", "RIDE5.au" },
    };

    public static final float [] DEF_AMPS = {
        1.5f, 1.0f, 0.8f, 0.7f,
        0.8f, 0.5f, 0.5f, 0.5f,
    };

    // ---------------------------------------
    // pads
    // ---------------------------------------

    // ---------------------------------------
    // voices
    // ---------------------------------------
    public static final int [] VOICE_ICONS = {
        ICON_KICK, ICON_SNARE, ICON_TOM, ICON_CLAP,
        ICON_TICK, ICON_HC, ICON_HO, ICON_SYMBAL,
    };


    // ---------------------------------------
    // tools
    // ---------------------------------------
    public static final int
          TOOL_TEMPO_MUL = 0,
          TOOL_TEMPO_DETECT = 1,
          TOOL_TEMPO_SET = 2,
          TOOL_SEQ_PAUSE = 4,
          TOOL_SEQ_AB = 5,
          TOOL_SEQ_SHUFFLE = 6,
          TOOL_SEQ_44_48 = 7,
          TOOL_FX_LOFI = 8,
          TOOL_FX_LOWPASS = 9,
          TOOL_FX_ECHO = 10,
          TOOL_FX_COMP = 11,
          TOOL_MISC_VOL = 12,
          TOOL_MISC_COMP = 13,
          TOOL_MISC_CLEAR = 14,
          TOOL_MISC_SAVE = 15
          ;

    public static final int [] TOOL_ICONS = {
        // timing
        ICON_NOTE4, ICON_DOT, ICON_METRONOME, -1,

        // sequence
        ICON_PAUSE, ICON_A, ICON_MIX, ICON_44,

        // waveform
        ICON_CRUSH, ICON_LPFILTER, ICON_ECHO, ICON_COMPRESS,

        // settings
       ICON_VOLUME, ICON_COMPRESS, ICON_DEL, ICON_SAVE
    };

    // ---------------------------------------
    // selectors
    // ---------------------------------------

    public static final int [] SELECTOR_ICONS = {
        ICON_METRONOME, ICON_SEQ, ICON_WAVEFORM, ICON_SETTINGS,
    };


    // colors & alphas
    public static final int
          COLOR_BG = 0x505050,
          COLOR_MARKER = 0xA02032,
          COLOR_VOICES = 0xC29968,


          COLOR_SAVE = 0xFFFFFF,
          COLOR_BUTTON = 0xFFFFFF,
          COLOR_BUTTON_WARN = 0xFF3030

          ;

    public static final int [] COLOR_SELECTORS = {
        0x802222,
        0x228022,
        0x222280,
        0x808022,
    };


    /* see
     * http://gamedev.stackexchange.com/questions/46463/is-there-an-optimum-set-of-colors-for-10-players/
     */
    public static final int [] COLOR_PADS  = {
        0x95bf7f,
        0xffdd7f,
        0xf33f7f,
        0xffaebf,
        0xc47f7f,
        0x807f99,
        0x807fc7,
        0xff7fff,
    };

    public static final float
          // ALPHA_NORMAL = 1,
          ALPHA_DISABLED = 0.4f,
          ALPHA_INACTIVE = 0.4f
          ;

}
