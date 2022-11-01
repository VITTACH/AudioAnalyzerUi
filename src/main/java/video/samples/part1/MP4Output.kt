package video.samples.part1

import audio.io.AudioDevice
import audio.utils.toFloatArray
import audio.visualization.mp4.VideoPlayer
import org.jcodec.api.FrameGrab
import org.jcodec.codecs.aac.AACDecoder
import org.jcodec.common.DemuxerTrack
import org.jcodec.common.io.FileChannelWrapper
import org.jcodec.common.io.NIOUtils
import org.jcodec.common.model.Picture
import org.jcodec.containers.mp4.demuxer.MP4Demuxer
import org.jcodec.scale.AWTUtil
import java.io.File


object MP4Output {

    private val videoPlayer = VideoPlayer("Video Player", 512, 512)

    @JvmStatic
    fun main(argv: Array<String>) {
        val file = File("samples/bunny1.mp4")

        val readableChannel: FileChannelWrapper = NIOUtils.readableChannel(file)
        val grab = FrameGrab.createFrameGrab(readableChannel)

        val createMP4Demuxer = MP4Demuxer.createMP4Demuxer(readableChannel)
        val audioTrack: DemuxerTrack = createMP4Demuxer.audioTracks[0]
        val videoTrack = createMP4Demuxer.videoTrack
        val videoFrameRate: Double = videoTrack.meta.totalFrames / videoTrack.meta.totalDuration
        val audioFrameRate: Double = audioTrack.meta.totalFrames / audioTrack.meta.totalDuration
        val sampleRate = audioTrack.meta.audioCodecMeta.sampleRate
        val channelCount = audioTrack.meta.audioCodecMeta.channelCount

        println(audioFrameRate)
        println(videoFrameRate)

        renderWithFps(videoFrameRate) {
            return@renderWithFps grab.nativeFrame?.also { showFrame(it) }
        }

        val audioDevice = AudioDevice(sampleRate)
        var audioDecoder: AACDecoder? = null
        renderWithFps(audioFrameRate) {
            return@renderWithFps audioTrack.nextFrame()?.also {
                if (audioDecoder == null) {
                    audioDecoder = AACDecoder(it.data)
                }
                val frame = audioDecoder!!.decodeFrame(it.data, null)
                audioDevice.writeSamples(toFloatArray(channelCount, frame.data.array()))
            }
        }
    }

    private fun renderWithFps(fps: Double, payload: () -> Any?) {
        val frameCap = fps
        var fps = 0

        var delta = 0.0
        var last = System.nanoTime()
        var timer = 0L

        Thread {
            do {
                val now = System.nanoTime()
                val since = now - last
                delta += since / (1000000000.0 / frameCap)
                timer += since
                last = now
                if (delta >= 1.0) {
                    if (payload() == null) break
                    fps++
                    delta = 0.0
                }
                if (timer >= 1000000000L) {
                    fps = 0
                    timer = 0L
                }
            } while (true)
        }.start()
    }

    private fun showFrame(picture: Picture) {
        val bufferedImage = AWTUtil.toBufferedImage(picture)
        videoPlayer.showFrame(bufferedImage)
    }
}