package oppen.skiss.lib

import android.graphics.Canvas

abstract class Skiss(private val skissView: SkissView): SkissSyntaxBase() {

    init {

        skissView.setup{ width, height ->
            this.width = width
            this.height = height
            setup(width, height)
        }

        skissView.update { canvas ->
            this.canvas = canvas
            if(canvas != null) update(canvas)
        }

        skissView.onTouch { x, y ->
            onTouch(x, y)
        }
    }

    fun start() = skissView.start()
    fun stop() = skissView.stop()
    fun pause() = skissView.pause()
    fun unpause() = skissView.unpause()

    abstract fun setup(width: Int, height: Int)
    abstract fun update(canvas: Canvas)
    open fun onTouch(x: Int, y: Int) {}

}