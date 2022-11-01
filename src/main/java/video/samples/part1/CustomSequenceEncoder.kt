package video.samples.part1

import org.jcodec.api.transcode.*
import org.jcodec.api.transcode.PixelStore.LoanerPicture
import org.jcodec.common.Codec
import org.jcodec.common.Format
import org.jcodec.common.io.SeekableByteChannel
import org.jcodec.common.model.*
import org.jcodec.common.model.Packet.FrameType
import org.jcodec.scale.ColorUtil
import org.jcodec.scale.Transform
import java.io.IOException

class CustomSequenceEncoder(
    out: SeekableByteChannel?,
    private val fps: Rational,
    outputFormat: Format?,
    outputVideoCodec: Codec?,
    outputAudioCodec: Codec?
) {
    private var transform: Transform? = null
    private var frameNo = 0
    private var timestamp = 0
    private val sink: Sink
    private val pixelStore: PixelStore

    init {
        sink = SinkImpl.createWithStream(out, outputFormat, outputVideoCodec, outputAudioCodec)
        sink.init()
        if (sink.inputColor != null) {
            transform = ColorUtil.getTransform(ColorSpace.RGB, sink.inputColor)
        }
        pixelStore = PixelStoreImpl()
    }

    /**
     * Encodes a frame into a movie.
     *
     * @param pic
     * @throws IOException
     */
    @Throws(IOException::class)
    fun encodeNativeFrame(pic: Picture, audioBuffer: AudioBuffer?) {
        require(pic.color == ColorSpace.RGB) {
            "The input images is expected in RGB color."
        }
        val sinkColor = sink.inputColor
        val toEncode = if (sinkColor != null) {
            pixelStore.getPicture(pic.width, pic.height, sinkColor).also {
                transform!!.transform(pic, it.picture)
            }
        } else {
            LoanerPicture(pic, 0)
        }

        val pkt = Packet.createPacket(
            null,
            timestamp.toLong(),
            fps.getNum(),
            fps.getDen().toLong(),
            frameNo.toLong(),
            FrameType.KEY,
            null
        )
        sink.outputVideoFrame(VideoFrameWithPacket(pkt, toEncode))
        audioBuffer?.let {
            sink.outputAudioFrame(AudioFrameWithPacket(it, pkt))
        }

        if (sinkColor != null) {
            pixelStore.putBack(toEncode)
        }
        timestamp += fps.getDen()
        frameNo++
    }

    @Throws(IOException::class)
    fun finish() {
        sink.finish()
    }
}