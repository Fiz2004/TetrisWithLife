package com.fiz.tetriswithlife

import android.R.attr
import android.content.Context
import android.graphics.*
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.WindowManager


private const val TIMES_BREATH_LOSE = 60
private const val DIRECTORY_IMG = "Resurs/v1/"


class MySurfaceView(context: Context?) :
    SurfaceView(context), SurfaceHolder.Callback {
    private var drawThread: DrawThread? = null

    var x = 0
    var y = 0


    init {
        holder.addCallback(this)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceCreated(holder: SurfaceHolder) {
        drawThread = DrawThread(getHolder(), getResources())
        drawThread!!.setRunning(true)
        drawThread!!.start()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        var retry = true
        drawThread!!.setRunning(false)
        while (retry) {
            try {
                drawThread!!.join()
                retry = false
            } catch (e: InterruptedException) {
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        return super.onTouchEvent(event)

        x = event!!.x.toInt()
        y = event!!.y.toInt()


        val displaymetrics = resources.displayMetrics

        Log.d("Touch event!!.action=",event!!.action.toString())

        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                if (x<displaymetrics.widthPixels/3){
                    drawThread!!.controller.Down=false
                    drawThread!!.controller.Up = false
                    drawThread!!.controller.Left = true
                    drawThread!!.controller.Right= false
                }
                if (x>2*displaymetrics.widthPixels/3){
                    drawThread!!.controller.Down=false
                    drawThread!!.controller.Up = false
                    drawThread!!.controller.Left = false
                    drawThread!!.controller.Right= true
                }
                if (y<displaymetrics.heightPixels/6){
                    drawThread!!.controller.Down=false
                    drawThread!!.controller.Up = true
                    drawThread!!.controller.Left = false
                    drawThread!!.controller.Right= false
                }
                if (y>5*displaymetrics.heightPixels/6){
                    drawThread!!.controller.Down=true
                    drawThread!!.controller.Up = false
                    drawThread!!.controller.Left = false
                    drawThread!!.controller.Right= false
                }
            }
            MotionEvent.ACTION_MOVE -> {
//                Log.d("Touch Move x=",x.toString())
//                Log.d("Touch Move y=",y.toString())
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                drawThread!!.controller.Down=false
                drawThread!!.controller.Up = false
                drawThread!!.controller.Left = false
                drawThread!!.controller.Right= false
            }
        }
//        tv.setText(sDown.toString() + "\n" + sMove + "\n" + sUp)
        return true
    }

}








