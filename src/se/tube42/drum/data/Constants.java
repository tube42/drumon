
package se.tube42.drum.data;

public final class Constants
{
    public static final boolean
          DEBUG = true
          ;

    public static final int
          MAX_TEMPO = 280,
          MIN_TEMPO = 40,
          SIMD_WIDTH = 4
          ;

    public static final int
          BANKS = 2,
          SELECTORS = 4,
          TOOLS = 4,
          VOICES = 8,
          PADS = 4 * 4
          ;

    public static final int
          TILE_PAD1 = 0,
          TILE_PAD0 = 1,
          TILE_MARKER = 2,

          TILE_BUTTON0 = 4,
          TILE_BUTTON0_ALT = 5,
          TILE_BUTTON1 = 6
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
          ICON_UP = 12,
          ICON_DOWN = 13,
          ICON_PAUSE = 14,
          ICON_PLAY = 15,
          ICON_METRONOME = 16,
          ICON_WAVEFORM = 17,
          ICON_FX = 18,
          ICON_SEQ = 19,

          ICON_NOTE4 = 20,
          ICON_NOTE8 = 21,
          ICON_NOTE16 = 22,
          ICON_DOT = 23,
          ICON_MIX = 24,
          ICON_QUESTION = 31
          ;

    // ---------------------------------------
    // samples
    // ---------------------------------------

    public static final String [] SAMPLES = {
        "kick2.au", "snare2.au", "floor_tom2.au", "tamburin.au",
        "side_stick1.au", "closed_hat_hard.au", "semi_open_hat.au", "ride_centre2.au",

        "BD1.au",  "SD2.au", "HITOM4.au", "CLAPS.au",
        "RIM.au", "CLHIHAT2.au",  "OPHIHAT2.au", "RIDE5.au",
    };

    public static final float [] DEF_AMPS = {
        1.5f, 1.0f, 0.8f, 0.7f,
        0.8f, 0.5f, 0.5f, 0.5f,

        1.0f, 1.0f, 0.8f, 0.7f,
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
    public static final int [] TOOL_ICONS = {
        // timing
        ICON_DOWN, ICON_DOT, ICON_UP, ICON_NOTE4,

        // sequence
        ICON_PAUSE, ICON_A, ICON_MIX, ICON_DEL,

        // waveform
        ICON_FX, ICON_FX, ICON_FX, ICON_FX,

        // settings
        ICON_QUESTION, ICON_QUESTION, ICON_QUESTION, ICON_QUESTION
    };

    // ---------------------------------------
    // selectors
    // ---------------------------------------

    public static final int [] SELECTOR_ICONS = {
        ICON_METRONOME, ICON_SEQ, ICON_WAVEFORM, ICON_SETTINGS,
    };


    //

    public static final int
          COLOR_BG = 0x505050,
          COLOR_PADS = 0x000000,
          COLOR_MARKER = 0xA02032,
          COLOR_VOICES = 0xC29968
          ;

    public static final int [] COLOR_SELECTORS = {
        0x802222,
        0x228022,
        0x222280,
        0x808022,
    };

}
