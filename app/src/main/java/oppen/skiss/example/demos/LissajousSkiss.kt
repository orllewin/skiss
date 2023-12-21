package oppen.skiss.example.demos

import android.graphics.Canvas
import oppen.skiss.lib.*
import oppen.skiss.lib.objects.Coord
import kotlin.math.cos
import kotlin.math.sin

class LissajousSkiss(view: SkissView, private val onTouch: () -> Unit): Skiss(view) {

    private var waveLengthA = 0.0f
    private var waveLengthB = 0.0f
    private var frame = 0
    private var angle = 0f

    private val coords = arrayListOf<Coord>()
    private var coordA: Coord? = null
    private var coordB: Coord? = null

    private val background = color("#1d1d1d")

    override fun setup(width: Int, height: Int) {
        stroke(color("#efefef"))
        translate(width/2, height/2)
        reset()
    }

    private fun reset(){
        //reset
        coords.clear()
        frame = 0
        angle = 0f
        waveLengthA = random(25.0, 160.0)
        waveLengthB = random(25.0, 160.0)
    }

    override fun update(canvas: Canvas) {
        background(background)

        if(frame > 3000) {
            reset()
        }

        angle = frame/ waveLengthA * TWO_PI
        val y = sin(angle) * (height/2.5)

        angle = frame/ waveLengthB * TWO_PI
        val x = cos(angle) * (width/2.5)

        coords.add(Coord(x.toInt(), y.toInt()))


        for(index in 1 until coords.size){
            coordA = coords[index -1]
            coordB = coords[index]
            line(coordA!!.x, coordA!!.y, coordB!!.x, coordB!!.y)
        }

        frame++
        angle += 0.1f
    }

    override fun onTouch(x: Int, y: Int) {
        reset()
        onTouch.invoke()
    }
}