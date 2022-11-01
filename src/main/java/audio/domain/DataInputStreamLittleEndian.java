package audio.domain;

import java.io.DataInputStream;
import java.io.IOException;

public class DataInputStreamLittleEndian {
    private DataInputStream systemStream;

    public DataInputStreamLittleEndian(DataInputStream systemStream) {
        this.systemStream = systemStream;
    }

    public void close() throws IOException {
        this.systemStream.close();
    }

    public void read(byte[] byteBufferToReadInto) throws IOException {
        // no need to translate to little-endian here

        this.systemStream.read(byteBufferToReadInto);
    }

    public int readInt() throws IOException {
        byte[] bytesLittleEndian = new byte[4];
        this.systemStream.read(bytesLittleEndian);

        long returnValueAsLong = (
                (bytesLittleEndian[0] & 0xFF)
                        | ((bytesLittleEndian[1] & 0xFF) << 8)
                        | ((bytesLittleEndian[2] & 0xFF) << 16)
                        | ((bytesLittleEndian[3] & 0xFF) << 24)
        );

        return (int) returnValueAsLong;
    }

    public short readShort() throws IOException {
        byte[] bytesLittleEndian = new byte[2];
        this.systemStream.read(bytesLittleEndian);

        int returnValueAsInt = ((bytesLittleEndian[0] & 0xFF) | ((bytesLittleEndian[1] & 0xFF) << 8));

        return (short) returnValueAsInt;
    }
}
