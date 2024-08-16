package com.plcoding.kidsdrawingapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import java.util.ArrayList

class DrawingView(context : Context, attrs : AttributeSet) : View(context , attrs) {

    private var mDrawPath : CustomPath? = null
    private var mCanvasBitmap : Bitmap?=null
    private var mDrawPaint : Paint?=null
    private var mCanvaspaint : Paint?=null
    private var mBrushSize : Float = 0.toFloat()
    private var color = Color.BLACK
    private var canvas : Canvas? = null
    private val mPaths = ArrayList<CustomPath>()
    private val mUndoPaths = ArrayList<CustomPath>()
    init {
        setUpDrawing()
    }
    fun onClickUndo(){
        if(mPaths.size>0){
            mUndoPaths.add(mPaths.removeAt(mPaths.size-1))
            invalidate()
        }
    }
    private fun setUpDrawing(){
        mDrawPaint = Paint()
        mDrawPath = CustomPath(color,mBrushSize)
        mDrawPaint!!.color = color
        mDrawPaint!!.style = Paint.Style.STROKE
        mDrawPaint!!.strokeJoin = Paint.Join.ROUND
        mDrawPaint!!.strokeCap = Paint.Cap.ROUND
        mCanvaspaint = Paint(Paint.DITHER_FLAG)
        //mBrushSize = 20.toFloat()


    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if(w>0 && h>0){
            mCanvasBitmap =Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
            canvas  = Canvas(mCanvasBitmap!!)

        }


    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mCanvasBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, mCanvaspaint)
        }

        for (path in mPaths) {
            mDrawPaint?.apply {
                strokeWidth = path.brushThickness
                color = path.color
                canvas.drawPath(path, this)
            }
        }

        mDrawPath?.let { path ->
            if (!path.isEmpty) {
                mDrawPaint?.apply {
                    strokeWidth = path.brushThickness
                    color = path.color
                    canvas.drawPath(path, this)
                }
            }
        }
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x
        val touchY = event?.y
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                mDrawPath?.apply {
                    color = this@DrawingView.color
                    brushThickness = mBrushSize
                    reset()
                    if (touchX != null && touchY != null) {
                        moveTo(touchX, touchY)
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                mDrawPath?.apply {
                    if (touchX != null && touchY != null) {
                        lineTo(touchX, touchY)
                    }
                }
                invalidate()  // Redraw the view to show the drawing
            }
            MotionEvent.ACTION_UP -> {
                mDrawPath?.let {
                    mPaths.add(it)  // Add the path to the list of paths
                }
                mDrawPath = CustomPath(color, mBrushSize)  // Prepare a new path for the next stroke
                invalidate()  // Redraw the view to show the final path
            }
            else -> return false
        }
        return true
    }

    fun setSizeForBrush(newSize : Float){
        mBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,newSize,resources.displayMetrics)
        mDrawPaint!!.strokeWidth = mBrushSize
    }

    fun setColor(newColor: String){
        color = Color.parseColor(newColor)
        mDrawPaint!!.color = color
    }


    internal inner class CustomPath(var color :Int, var brushThickness : Float) : Path() {

    }


}