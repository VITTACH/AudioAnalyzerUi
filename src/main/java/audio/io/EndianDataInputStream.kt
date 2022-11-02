package audio.io

import java.io.DataInputStream
import java.io.InputStream
import java.nio.charset.Charset

class EndianDataInputStream(`in`: InputStream?) : DataInputStream(`in`) {
    @Throws(Exception::class)
    fun read4ByteString(): String {
        val bytes = ByteArray(4)
        readFully(bytes)
        return String(bytes, Charset.forName("US-ASCII"))
    }

    @Throws(Exception::class)
    fun readShortLittleEndian(): Short {
        var result = readUnsignedByte()
        result = result or (readUnsignedByte() shl 8)
        return result.toShort()
    }

    @Throws(Exception::class)
    fun readIntLittleEndian(): Int {
        var result = readUnsignedByte()
        result = result or (readUnsignedByte() shl 8)
        result = result or (readUnsignedByte() shl 16)
        result = result or (readUnsignedByte() shl 24)
        return result
    }

    @Throws(Exception::class)
    fun readInt24BitLittleEndian(): Int {
        var result = readUnsignedByte()
        result = result or (readUnsignedByte() shl 8)
        result = result or (readUnsignedByte() shl 16)
        if (result and (1 shl 23) == 8388608) result = result or -0x1000000
        return result
    }

    @Throws(Exception::class)
    fun readInt24Bit(): Int {
        var result = readUnsignedByte() shl 16
        result = result or (readUnsignedByte() shl 8)
        result = result or readUnsignedByte()
        return result
    }
}