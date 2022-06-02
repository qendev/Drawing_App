package com.example.drawingapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

//this class will be defined as a View
class DrawingView(context: Context, attrs:AttributeSet): View(context,attrs) {
    //we need to be able to draw on this View
    //we need to define variables that we will need inorder to draw

    private var mDrawPath:CustomPath? = null
    private var mCanvasBitmap:Bitmap? = null
    //will use paint that holds information on how tostyle,color,style,geometry,texts and bitmaps
    private var mDrawPaint: Paint? = null
    private var mCanvasPaint:Paint? = null
    //inorder to know how thick the paintBrush should be
    private var mBrushSize:Float = 0.toFloat()
    //for th Color with which we will need to draw
    private var color = Color.BLACK
    //for the backGround on which the user will draw on
    private var canvas: Canvas? = null
    //inorder to make the lines persist on the canvas will need the following
    private val mPaths = ArrayList<CustomPath>()
    //so as to implement undo functionality
    private val mUndoPaths = ArrayList<CustomPath>()
    //so as to implement redo functionality
    private val mRedoPaths = ArrayList<CustomPath>()






    //to set the variables that are null
    init {
        setUpDrawing()
    }
    //for undo functionality
    fun onClickUndo(){
        //first check if there are any paths
        if (mPaths.size > 0){
            mUndoPaths.add(mPaths.removeAt(mPaths.size-1))
            invalidate()
        }
    }


    private fun setUpDrawing() {
        mDrawPaint = Paint()
        mDrawPath = CustomPath(color,mBrushSize)
        //set the Color of the Paint
        mDrawPaint!!.color = color
        //define the style for the paint
        mDrawPaint!!.style = Paint.Style.STROKE
        mDrawPaint!!.strokeJoin = Paint.Join.ROUND
        mDrawPaint!!.strokeCap = Paint.Cap.ROUND
        mCanvasPaint = Paint(Paint.DITHER_FLAG)
//        mBrushSize = 20.toFloat()
    }


    //we will use this to display the canvas.
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
        canvas = Canvas(mCanvasBitmap!!)
    }
    //what should happen when we draw
    //change Canvas to Canvas? if fails
    override fun onDraw(canvas: Canvas ) {
        super.onDraw(canvas)
        canvas.drawBitmap(mCanvasBitmap!!,0f,0f,mCanvasPaint)

        for (path in mPaths){
            mDrawPaint!!.strokeWidth =path.bushThickness
            mDrawPaint!!.color = path.color
            canvas.drawPath(path, mDrawPaint!!)


        }

        //for the path
        //check if Path is empty first,if so draw something.
        if (!mDrawPath!!.isEmpty){
            //to set how thick the paint should be
            mDrawPaint!!.strokeWidth =mDrawPath!!.bushThickness
            //set color of the customPaint
            mDrawPaint!!.color = mDrawPath!!.color
            canvas.drawPath(mDrawPath!!, mDrawPaint!!)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x
        val touchY = event?.y

        when(event?.action){
            MotionEvent.ACTION_DOWN->{
                Log.e("CHECKING TOUCH EFFECT","WORKING")
                //set the pathColor first
                mDrawPath!!.color = color
                //set the pathThickness
                mDrawPath!!.bushThickness = mBrushSize
                //to clear any lines to make it empty
                mDrawPath!!.reset()
                if (touchX != null) {
                    if (touchY != null) {
                        mDrawPath!!.moveTo(touchX,touchY)
                    }
                }
            }

            MotionEvent.ACTION_MOVE->{
                Log.e("CHECKING MOVING EFFECT","WORKING")
                if (touchX != null) {
                    if (touchY != null) {
                        mDrawPath!!.lineTo(touchX,touchY)
                    }
                }
            }

            MotionEvent.ACTION_UP->{
                Log.e("CHECKING RELEASE EFFECT","WORKING")
                mPaths.add(mDrawPath!!)

                mDrawPath = CustomPath(color,mBrushSize)
            }
            else-> return false
        }
        invalidate()


        return true
    }

    fun setSizeForBrush(newSize:Float){
        mBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            newSize,resources.displayMetrics
        )
        mDrawPaint!!.strokeWidth = mBrushSize

    }

    fun setColor(newColor:String){
        color = Color.parseColor(newColor)
        mDrawPaint!!.color = color

    }

    //should only be usable within this drawingView and we should be able to have access to its variables
    internal inner class CustomPath(var color:Int, var bushThickness:Float): Path() {

    }
}