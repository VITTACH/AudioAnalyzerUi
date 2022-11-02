package audio.visualization

import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.SwingUtilities

/**
 * A simple class that allows to plot float[] arrays
 * to a swing window. The first function to plot that
 * is given to this class will set the minimum and
 * maximum height values. I'm not that good with Swing
 * so i might have done a couple of stupid things in here :)
 *
 * @author VITTACH
 */
class Plot(title: String?, width: Int, height: Int) {
    private lateinit var frame: JFrame

    private var scrollPane: JScrollPane? = null

    private var panel: JPanel? = null

    private var image: BufferedImage

    /**
     * the last scaling factor to normalize samples
     */
    private var scalingFactor = 1f

    /**
     * wheter the plot was cleared, if true we have to recalculate the scaling factor
     */
    private var cleared = true

    /**
     * current marker position and color
     */
    private var markerPosition = 0
    private var markerColor = Color.white
    fun clear() {
        SwingUtilities.invokeLater {
            val g = image.createGraphics()
            g.color = Color.black
            g.fillRect(0, 0, image.width, image.height)
            g.dispose()
            cleared = true
        }
    }

    fun plot(samples: FloatArray, samplesPerPixel: Float, color: Color?) {
        synchronized(image) {
            if (image.width < samples.size / samplesPerPixel) {
                image = BufferedImage((samples.size / samplesPerPixel).toInt(), frame.height, BufferedImage.TYPE_4BYTE_ABGR)
                val g = image.createGraphics()
                g.color = Color.black
                g.fillRect(0, 0, image.width, image.height)
                g.dispose()
                panel!!.setSize(image.width, image.height)
            }
            if (cleared) {
                var min = 0f
                var max = 0f
                for (i in samples.indices) {
                    min = Math.min(samples[i], min)
                    max = Math.max(samples[i], max)
                }
                scalingFactor = max - min
                cleared = false
            }
            val g = image.createGraphics()
            g.color = color
            var lastValue = samples[0] / scalingFactor * image.height / 3 + image.height / 2
            for (i in 1 until samples.size) {
                val value = samples[i] / scalingFactor * image.height / 3 + image.height / 2
                g.drawLine(
                    ((i - 1) / samplesPerPixel).toInt(), image.height - lastValue.toInt(),
                    (i / samplesPerPixel).toInt(), image.height - value.toInt()
                )
                lastValue = value
            }
            g.dispose()
        }
    }

    fun plot(samples: List<Float>, samplesPerPixel: Float, color: Color?) {
        synchronized(image) {
            if (image.width < samples.size / samplesPerPixel) {
                image = BufferedImage((samples.size / samplesPerPixel).toInt(), frame.height, BufferedImage.TYPE_4BYTE_ABGR)
                val g = image.createGraphics()
                g.color = Color.black
                g.fillRect(0, 0, image.width, image.height)
                g.dispose()
                panel!!.setSize(image.width, image.height)
            }
            if (cleared) {
                var min = 0f
                var max = 0f
                for (i in samples.indices) {
                    min = Math.min(samples[i], min)
                    max = Math.max(samples[i], max)
                }
                scalingFactor = max - min
                cleared = false
            }
            val g = image.createGraphics()
            g.color = color
            var lastValue = samples[0] / scalingFactor * image.height / 3 + image.height / 2
            for (i in 1 until samples.size) {
                val value = samples[i] / scalingFactor * image.height / 3 + image.height / 2
                g.drawLine(
                    ((i - 1) / samplesPerPixel).toInt(), image.height - lastValue.toInt(),
                    (i / samplesPerPixel).toInt(), image.height - value.toInt()
                )
                lastValue = value
            }
            g.dispose()
        }
    }

    fun plot(samples: FloatArray, samplesPerPixel: Float, offset: Float, useLastScale: Boolean, color: Color?) {
        synchronized(image) {
            if (image.width < samples.size / samplesPerPixel) {
                image = BufferedImage((samples.size / samplesPerPixel).toInt(), frame.height, BufferedImage.TYPE_4BYTE_ABGR)
                val g = image.createGraphics()
                g.color = Color.black
                g.fillRect(0, 0, image.width, image.height)
                g.dispose()
                panel!!.setSize(image.width, image.height)
            }
            if (!useLastScale) {
                var min = 0f
                var max = 0f
                for (i in samples.indices) {
                    min = Math.min(samples[i], min)
                    max = Math.max(samples[i], max)
                }
                scalingFactor = max - min
            }
            val g = image.createGraphics()
            g.color = color
            var lastValue = samples[0] / scalingFactor * image.height / 3 + image.height / 2 - offset * image.height / 3
            for (i in 1 until samples.size) {
                val value = samples[i] / scalingFactor * image.height / 3 + image.height / 2 - offset * image.height / 3
                g.drawLine(
                    ((i - 1) / samplesPerPixel).toInt(), image.height - lastValue.toInt(),
                    (i / samplesPerPixel).toInt(), image.height - value.toInt()
                )
                lastValue = value
            }
            g.dispose()
        }
    }

    fun plot(samples: List<Float>, samplesPerPixel: Float, offset: Float, useLastScale: Boolean, color: Color?) {
        synchronized(image) {
            if (image.width < samples.size / samplesPerPixel) {
                image = BufferedImage((samples.size / samplesPerPixel).toInt(), frame.height, BufferedImage.TYPE_4BYTE_ABGR)
                val g = image.createGraphics()
                g.color = Color.black
                g.fillRect(0, 0, image.width, image.height)
                g.dispose()
                panel!!.setSize(image.width, image.height)
            }
            if (!useLastScale) {
                var min = 0f
                var max = 0f
                for (i in samples.indices) {
                    min = Math.min(samples[i], min)
                    max = Math.max(samples[i], max)
                }
                scalingFactor = max - min
            }
            val g = image.createGraphics()
            g.color = color
            var lastValue = samples[0] / scalingFactor * image.height / 3 + image.height / 2 - offset * image.height / 3
            for (i in 1 until samples.size) {
                val value = samples[i] / scalingFactor * image.height / 3 + image.height / 2 - offset * image.height / 3
                g.drawLine(
                    ((i - 1) / samplesPerPixel).toInt(), image.height - lastValue.toInt(),
                    (i / samplesPerPixel).toInt(), image.height - value.toInt()
                )
                lastValue = value
            }
            g.dispose()
        }
    }

    fun setMarker(x: Int, color: Color) {
        markerPosition = x
        markerColor = color
    }

    /**
     * Creates a new Plot with the given title and dimensions.
     *
     * @param title  The title.
     * @param width  The width of the plot in pixels.
     * @param height The height of the plot in pixels.
     */
    init {
        image = BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR)
        try {
            SwingUtilities.invokeAndWait {
                frame = JFrame(title)
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
                frame.setPreferredSize(Dimension(
                    width + frame.getInsets().left + frame.getInsets().right,
                    frame.getInsets().top + frame.getInsets().bottom + height)
                )
                val img = BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR)
                val g = img.graphics as Graphics2D
                g.color = Color.black
                g.fillRect(0, 0, width, height)
                g.dispose()
                image = img
                panel = object : JPanel() {
                    public override fun paintComponent(g: Graphics) {
                        super.paintComponent(g)
                        synchronized(image) {
                            g.drawImage(image, 0, 0, null)
                            g.color = markerColor
                            g.drawLine(markerPosition, 0, markerPosition, image.height)
                        }
                        Thread.yield()
                        frame.repaint()
                    }

                    override fun update(g: Graphics) {
                        paint(g)
                    }

                    override fun getPreferredSize(): Dimension {
                        return Dimension(image.width, image.height)
                    }
                }
                scrollPane = JScrollPane(panel)
                frame.getContentPane().add(scrollPane)
                frame.pack()
                frame.setVisible(true)
            }
        } catch (ex: Exception) {
            // doh...
        }
    }
}