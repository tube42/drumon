

OUT=../../android/assets/samples
WAV = $(wildcard *.wav)
AU = $(patsubst %.wav,$(OUT)/%.au,$(WAV))

all: $(OUT) $(AU)


$(OUT)/%.au: %.wav Makefile
	@sox $< -e signed-integer -b 16 -r 44100 $@ channels 1 gain -n gain -6
	@file $@
	@-play $@ 2> /dev/null



$(OUT):
	mkdir -p $(OUT)
clean:
	rm -f $(AU)
