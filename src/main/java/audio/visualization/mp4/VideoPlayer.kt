package audio.visualization.mp4

import java.awt.Dimension
import java.awt.Graphics
import java.awt.image.BufferedImage
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.SwingUtilities

class VideoPlayer(title: String?, width: Int, height: Int) {

    private var image = BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR)

    init {
        val panel = object : JPanel() {
            override fun paintComponent(g: Graphics) {
                super.paintComponent(g)
                synchronized(image) {
                    g.drawImage(image, 0, 0, null)
                }
                Thread.yield()
                repaint()
            }

            override fun getPreferredSize(): Dimension {
                return Dimension(image.width, image.height)
            }

            override fun update(g: Graphics) {
                paint(g)
            }
        }

        SwingUtilities.invokeAndWait {
            JFrame(title).apply {
                defaultCloseOperation = JFrame.EXIT_ON_CLOSE
                preferredSize = Dimension(width, height)

                contentPane.add(panel)
                pack()
                isVisible = true
            }
        }
    }

    fun showFrame(bufferedImage: BufferedImage) {
        synchronized(image) { image = bufferedImage }
    }
}