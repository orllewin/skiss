package oppen.skiss.example.demos

import android.graphics.*
import oppen.skiss.lib.*
import oppen.skiss.lib.objects.Vektor
import kotlin.math.floor
import kotlin.math.sqrt

class GravitySkiss(view: SkissView, private val onTouch: () -> Unit): Skiss(view) {

    lateinit var blackHole: Vektor
    private val numberOfBands = 128
    private val greyCoefficient = 255/numberOfBands

    private val motes = mutableListOf<Mote>()

    private val bWidth = 80
    private val bHeight = 160

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

        blackHole = Vektor(bWidth/2, bHeight/2)

        motes.clear()
        repeat(10){
            motes.add(Mote())
        }
    }

    override fun update(canvas: Canvas) {
        motes.forEach { it.update() }

        for(x in 0 until bWidth){
            for(y in 0 until bHeight){
                var sum = 0f
                repeat(motes.size){index ->
                    val xx = motes[index].location.x - x
                    val yy = motes[index].location.y - y
                    sum += motes[index].radius / sqrt(xx * xx + yy * yy)
                }
                bufferPaint.color = greyColor(floor(sum * numberOfBands) * greyCoefficient)
                bufferCanvas.drawPoint(x.toFloat(), y.toFloat(), bufferPaint)
            }
        }

        canvas.drawBitmap(bufferBitmap, matrix, bufferPaint)
    }

    inner class Mote{
        var location = Vektor(random(bWidth), random(bHeight))
        private var velocity = Vektor(0f, 0f)
        private var acceleration: Vektor? = null
        private var maxSpeed = 5f
        var radius = 5

        fun update(): Mote {
            var agg = Vektor(0, 0)
            motes.forEach {mote ->
                var directionToMote = mote.location - location

                directionToMote.normalize()
                directionToMote *= -0.1f
                agg += directionToMote
            }

            var directionToBlackHole = blackHole - location
            directionToBlackHole.normalize()
            directionToBlackHole *= 2.4f
            agg += directionToBlackHole

            acceleration = agg/motes.size

            velocity += acceleration!!
            velocity.limit(maxSpeed)
            location += velocity

            if (location.x > bWidth || location.x < 0) {
                velocity.x *= -1
                velocity.x *= 0.5f
            }
            if (location.y > bHeight || location.y < 0){
                velocity.y *= -1
                velocity.y *= 0.5f
            }

            return this
        }
    }

    override fun onTouch(x: Int, y: Int) = onTouch.invoke()
}