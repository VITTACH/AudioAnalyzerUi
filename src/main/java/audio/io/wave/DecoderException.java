package audio.io.wave;

public class DecoderException extends Exception {

    public DecoderException(String message) {
        super(message);
    }

    public DecoderException(String message, Throwable cause) {
        super(message, cause);
    }
}
