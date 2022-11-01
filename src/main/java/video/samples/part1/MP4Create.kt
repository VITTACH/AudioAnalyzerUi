package video.samples.part1

import audio.io.wave.WaveDecoder
import audio.utils.toFloatArray
import org.jcodec.common.AudioFormat.MONO_44K_S16_LE
import org.jcodec.common.Codec
import org.jcodec.common.Format
import org.jcodec.common.io.NIOUtils
import org.jcodec.common.model.AudioBuffer
import org.jcodec.common.model.Rational
import org.jcodec.scale.AWTUtil
import java.awt.Color
import java.awt.GradientPaint
import java.awt.image.BufferedImage
import java.io.FileInputStream
import java.nio.ByteBuffer

object MP4Create {
    private const val PITCH = 1.0

    @JvmStatic
    fun main(args: Array<String>) {
        val outputChannel = NIOUtils.writableFileChannel("samples/sample.mp4")
        val fps = 60
        val encoder = CustomSequenceEncoder(outputChannel, Rational.R(fps, 1), Format.MOV, Codec.H264, Codec.PCM)

        val waveDecoder = WaveDecoder()
        waveDecoder.setStream(FileInputStream("samples/sample.wav"))
        val riffWaveHeader = waveDecoder.riffWaveHeader
        val channelsCount = riffWaveHeader.channels

        val duration = 5 // sec

        var samples: ByteArray? = null

        for (i in 0..duration * fps) {
            val img = BufferedImage(480, 272, BufferedImage.TYPE_INT_RGB)
            img.createGraphics().apply {
                paint = GradientPaint(300f, 0f, Color.GREEN, 400f, 272f, Color.MAGENTA)
                fillRect(0, 0, 480, 272)
                color = Color.RED
                fillOval(i, i, 30, 30)
            }

            samples = waveDecoder.readNextNSamples(2048)
            val floatArray = samples?.let { toFloatArray(channelsCount, it) }
            val audioBuffer = floatArray?.let {
                AudioBuffer(fillBuffer(it), MONO_44K_S16_LE, 0)
            }

            encoder.encodeNativeFrame(AWTUtil.fromBufferedImageRGB(img), audioBuffer)
        }

        encoder.finish()
    }

    private fun fillBuffer(soundArray: FloatArray): ByteBuffer {
        val ret = ByteArray(soundArray.size * 2)
        var j = 1
        for (i in soundArray.indices) {
            val value = (soundArray[i] * Short.MAX_VALUE).toShort()
            ret[j] = (value.toInt() shr 8).toByte()
            j += 2
        }
        return ByteBuffer.wrap(ret)
    }

}