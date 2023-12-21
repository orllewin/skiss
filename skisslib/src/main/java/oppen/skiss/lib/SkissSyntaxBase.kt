package oppen.skiss.lib

import android.graphics.Canvas
import android.graphics.Paint
import oppen.skiss.lib.objects.Coord

const val TWO_PI = 6.2831855f

abstract class SkissSyntaxBase {

    class SkissPaint(var active: Boolean = false): Paint(){
        fun draw(operation: () -> Unit){
            if(active) operation.invoke()
        }
    }

    private val fillPaint = SkissPaint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    private val strokePaint = SkissPaint().apply {
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    var width = -1
    var height = -1

    var xT = 0//x translate
    var yT = 0//y translate

    var canvas: Canvas? = null

    fun translate(x: Int, y: Int){
        xT = x
        yT = y
    }

    fun background(color: Int){
        canvas?.drawColor(color)
    }

    fun smooth(){
        strokePaint.isAntiAlias = true
        fillPaint.isAntiAlias = true
    }

    fun noSmooth(){
        strokePaint.isAntiAlias = false
        fillPaint.isAntiAlias = false
    }

    fun stroke(color: String){
        strokePaint.apply {
            this.color = color(color)
            active = true
        }
    }

    fun strokeWeight(weight: Number){
        strokePaint.apply{
            strokeWidth = weight.toFloat()
        }
    }

    fun stroke(color: Int){
        strokePaint.apply {
            this.color = color
            active = true
        }
    }

    fun fill(color: String){
        fillPaint.apply {
            this.color = color(color)
            active = true
        }
    }

    fun fill(color: Int){
        fillPaint.apply{
            this.color = color
            active = true
        }
    }

    fun noFill(){
        fillPaint.active = false
    }

    fun noStroke(){
        strokePaint.active = false
    }

    /**
     *
     * Drawing Operations
     *
     */

    fun line(x1: Number, y1: Number, x2: Number, y2: Number){
        strokePaint.draw {
            canvas?.drawLine(
                xT + x1.toFloat(),
                yT + y1.toFloat(),
                xT + x2.toFloat(),
                yT + y2.toFloat(), strokePaint)
        }
    }

    fun lines(lines: List<Pair<Coord, Coord>>){
        strokePaint.draw {
            canvas?.drawLines(lines.flatMap {
                arrayListOf(it.first.x + xT, it.first.y + yT, it.second.x + xT, it.second.y + yT)
            }.toFloatArray(), strokePaint)
        }
    }

    fun circle(x: Number, y: Number, radius: Number){
        strokePaint.draw {
            canvas?.drawCircle(xT + x.toFloat(), yT + y.toFloat(), radius.toFloat(), strokePaint)
        }
        fillPaint.draw{
            canvas?.drawCircle(xT + x.toFloat(), yT + y.toFloat(), radius.toFloat(), fillPaint)
        }
    }

    fun square(x: Number, y: Number, diameter: Number){
        strokePaint.draw {
            canvas?.drawRect(
                xT + x.toFloat(),
                yT + y.toFloat(),
                (x.toFloat() + diameter.toFloat() + xT),
                (y.toFloat() + diameter.toFloat() + yT),  strokePaint)
        }
        fillPaint.draw{
            canvas?.drawRect(
                xT + x.toFloat(),
                yT + y.toFloat(),
                (x.toFloat() + diameter.toFloat() + xT),
                (y.toFloat() + diameter.toFloat() + yT),  fillPaint)
        }
    }

    fun points(points: List<Coord>){
        strokePaint.draw {
            canvas?.drawPoints(points.flatMap {
                arrayListOf(it.x + xT, it.y + yT)
            }.toFloatArray(), strokePaint)
        }
    }

    fun point(x: Int, y: Int) = point(x.toFloat(), y.toFloat())

    fun point(x: Float, y: Float){
        fillPaint.draw {
            canvas?.drawPoint(xT + x.toFloat(), yT + y.toFloat(), strokePaint)
        }
    }

    fun point(point: Coord){
        strokePaint.draw {
            canvas?.drawPoint(xT + point.x, yT + point.y, strokePaint)
        }
    }
}