package audio.io.wave;

import java.util.Arrays;

public enum AudioCodingFormat {

    LINEAR_PCM(1, "linear pulse-code modulation");

    private final int code;
    private final String name;

    AudioCodingFormat(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static AudioCodingFormat from(int value) {
        return Arrays.stream(values())
                .filter(audioCodingFormat -> audioCodingFormat.code == value)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Unknown audio coding format code provided: " + value));
    }
}