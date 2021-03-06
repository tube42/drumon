
package se.tube42.drum.data;

public final class Constants
{
	public static final boolean
		DEBUG = false
		;

	public static final String
		ALPHA = "abcdefghijklmnopqrstuvwxyz",
		NUMBERS = "0123456789",
		CHARSET1 = NUMBERS + "%",
		CHARSET2 = ALPHA + ALPHA.toUpperCase() + NUMBERS + "@'.,/:!?()"
		;

	public static final int
		SIZE_FONT1 = 16,
		SIZE_FONT2 = 12
		;

	public static final float
		MAX_VOLUME = 4.0f,
		MIN_VOLUME = 0.0f
		;

	public static final int
		MAX_TEMPO = 260,
		MIN_TEMPO = 40,
		MIN_TEMPO_MUL = 0,
		MAX_TEMPO_MUL = 2,

		MIN_VOLUME_VAR = 0,
		MAX_VOLUME_VAR = 75,

		LONGPRESS_DELAY = 500,
		NUM_SAVES = 16,

		SIMD_WIDTH = 4
		;

	public static final int
		SELECTORS = 4,
		TOOLS = 4,
		VOICES = 8,
		VOICE_BANKS = 2,
		PADS = 4 * 4 * 2
		;

	// effect chain
	public static final int
		FX_LOFI = 0,
		FX_FILTER = 1,
		FX_DELAY = 2,
		FX_COMP = 3
		;

	public static final int
		TILE_PAD0 = 0,
		TILE_PAD1 = 1,
		TILE_MARKER = 2,
		TILE_CIRCLE = 3,
		TILE_BUTTON0 = 4,
		TILE_BUTTON1 = 5
		;

	public static final int
		ICON_KICK = 0,
		ICON_SNARE = 1,
		ICON_TOM = 2,
		ICON_CLAP = 3,
		ICON_TICK = 4,
		ICON_HC = 5,
		ICON_HO = 6,
		ICON_SYMBAL = 7,
		ICON_A = 8,
		ICON_B = 9,
		ICON_DEL = 10,
		ICON_SETTINGS = 11,
		ICON_HELP = 12,
		ICON_SAVE = 13,
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

		ICON_LOFI = 24,
		ICON_FILTER = 25,
		ICON_DELAY = 26,
		ICON_COMPRESS = 27,
		ICON_MISC = 28,
		ICON_TIME = 29,


		ICON_44 = 32,

		ICON_SOUND_SELECT = 40,
		ICON_SOUND_SELECT_TEMPLATE = 48,
		ICON_PATTERN_SELECT = 56
		;

	// ---------------------------------------
	// samples
	// ---------------------------------------

	public static final String [][] SAMPLES = {
		{ "kick2.au", "kick_hard.au", "BD1.au", "domingus-djembe-lo-1.au", "domingus-djembe-lo-king.au" },
		{ "snare2.au", "SD2.au", "snare_supraphonic_hard.au", "domingus-djembe-hi-1.au", "domingus-djembe-hi-2.au" },
		{ "floor_tom2.au", "HITOM4.au", "domingus-djembe-lo-1.au", "domingus-djembe-lo-king.au"},
		{ "tamburin.au", "CLAPS.au", "Hi_Shk1.au",  "domingus-djembe-hi-1.au", "domingus-djembe-hi-2.au" },

		{ "side_stick1.au", "CHOICE_BITS_COWBELL.au", "RIM.au", "domingus-djembe-mid-3.au" },
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
	// tools
	// ---------------------------------------
	public static final int
		TOOL_TEMPO_PAUSE = 0,
		TOOL_TEMPO_MUL = 1,
		TOOL_TEMPO_DETECT = 2,
		TOOL_TEMPO_SET = 3,
		TOOL_SEQ_AB = 4,
		TOOL_SEQ_CLEAR = 5,
		TOOL_SEQ_SHUFFLE = 6,
		TOOL_SEQ_MEASURE = 7,
		TOOL_FX_LOFI = 8,
		TOOL_FX_FILTER = 9,
		TOOL_FX_DELAY = 10,
		TOOL_FX_COMP = 11,
		TOOL_MISC_SAVE = 12,
		TOOL_MISC_SETTINGS = 14,
		TOOL_MISC_ABOUT = 15
		;

	public static final int [] TOOL_ICONS = {
		// timing
		ICON_PAUSE, ICON_NOTE4, ICON_DOT, ICON_METRONOME,

		// sequence
		ICON_A, ICON_DEL, ICON_MIX, ICON_44,

		// waveform
		ICON_LOFI, ICON_FILTER, ICON_DELAY, ICON_COMPRESS,

		// settings
		ICON_SAVE, -1, ICON_SETTINGS, ICON_HELP,
	};

	// ---------------------------------------
	// selectors
	// ---------------------------------------

	public static final int [] SELECTOR_ICONS = {
		ICON_TIME, ICON_SEQ, ICON_WAVEFORM, ICON_MISC,
	};


	// colors & alphas
	public static final int
		COLOR_BG = 0x909090,
		COLOR_MARKER = 0xA02032,
		COLOR_VOICES = 0xC29968,
		COLOR_PADS = 0xffdd7f,


		COLOR_SAVE = 0xFFFFFF,
		COLOR_BUTTON = 0xFFFFFF,
		COLOR_BUTTON_WARN = 0xFF3030,
		COLOR_BUTTON_CLOSE = 0xA0D0A0
		;

	public static final int [] COLOR_SELECTORS = {
		0x802222,
		0x228022,
		0x222280,
		0x808022,
	};


	public static final float
		// ALPHA_NORMAL = 1,
		ALPHA_DISABLED = 0.4f,
		ALPHA_INACTIVE = 0.4f
		;

	public static final String ABOUT_TEXT =
		"Some years ago someone showed me this iOS app that was supposed to be 'intuitive' and 'magical'. I bet myself I could create something better for Android in just a weekend. And I did exactly that, it just took a couple of years...\n" +
		"\n" +
		"The app may look simple, but you can get some amazing sound from it. " +
		"Pro tip 1: try long-press. Pro tip 2: the save page can be used for live playing!"
		;
	public static final String GITHUB_PAGE = "https://github.com/tube42/drumon";
}
