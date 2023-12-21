package oppen.skiss.example.demos

import android.graphics.*
import oppen.skiss.lib.*
import oppen.skiss.lib.objects.Vektor
import kotlin.math.floor
import kotlin.math.sqrt


class MetaballSkiss(view: SkissView, private val onTouch: () -> Unit): Skiss(view) {

    private val ballCount = 8
    private val numberOfBands = 128
    private val greyCoefficient = 255/numberOfBands

    private var balls = mutableListOf<Metaball>()

    private val bWidth = 100
    private val bHeight = 200

    private val bufferBitmap = Bitmap.createBitmap(bWidth, bHeight, Bitmap.Config.ARGB_8888)
    private val bufferCanvas = Canvas(bufferBitmap)
    private val bufferPaint = Paint()
    private lateinit var destinationRect: Rect
    private lateinit var matrix: Matrix

    override fun setup(width: Int, height: Int) {

        destinationRect = Rect(0, 0, width, height)

        matrix = Matrix().apply {
            preScale(width / bWidth.toFloat(), height/bHeight.toFloat())
        }

        noSmooth()

        balls.clear()

        repeat(ballCount){
            balls.add(Metaball())
        }
    }

    override fun update(canvas: Canvas) {

        balls.forEach { it.update() }

        for(x in 0 until bWidth){
            for(y in 0 until bHeight){
                var sum = 0f
                repeat(balls.size){index ->
                    val xx = balls[index].position.x - x
                    val yy = balls[index].position.y - y
                    sum += balls[index].radius / sqrt(xx * xx + yy * yy)
                }
                bufferPaint.color = greyColor(floor(sum * numberOfBands) * greyCoefficient)
                bufferCanvas.drawPoint(x.toFloat(), y.toFloat(), bufferPaint)
            }
        }

        canvas.drawBitmap(bufferBitmap, matrix, bufferPaint)
    }

    override fun onTouch(x: Int, y: Int) {
        onTouch.invoke()
    }

    inner class Metaball{
        var direction = Vektor.randomDirection()
        var velocity = random(1, 2)
        var position = Vektor(random(0f, (bWidth-1).toFloat()), random(0f, (bHeight-1).toFloat()))
        val radius = random(3, 10)

        fun update(){
            position.x += (direction.x * velocity)
            position.y += (direction.y * velocity)

            if (position.x > bWidth || position.x < 0) direction.x *= -1
            if (position.y > bHeight || position.y < 0) direction.y *= -1
        }
    }
}