package com.lsy.viewlib.weight

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.lsy.viewlib.R

/**
 * Title :
 * Author: Lsy
 * Date: 2020/11/3 4:13 PM
 * Version:
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class FlowersImageView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val bitmap = ArrayList<Bitmap>()

    init {
        initAttrs(attrs)
    }

    private fun initAttrs(attrs: AttributeSet?) {
        attrs?.let {

        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            drawBitmap(canvas)
        }
    }

    private fun messageView() {

    }

    private fun drawBitmap(canvas: Canvas) {
        var bitmap = BitmapFactory.decodeResource(resources, R.mipmap.logo)
        val ratio = if (bitmap.width > bitmap.height) {
            measuredHeight / bitmap.height.toFloat()
        } else {
            measuredWidth / bitmap.width.toFloat()
        }
        val matrix = Matrix()
        matrix.preScale(ratio, ratio)
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, false)
        val xp = (bitmap.width - measuredWidth) / 2
        val yp = (bitmap.height - measuredHeight) / 2
        bitmap = Bitmap.createBitmap(bitmap, xp, yp, bitmap.width - xp, bitmap.height - yp)
//        val src = Rect(0, 0, bitmap.width, bitmap.height)
//        val dst = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
//        canvas.drawBitmap(bitmap, src, dst, Paint())

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.shader = shader
        canvas.drawCircle(measuredWidth / 2f, measuredHeight / 2f, measuredWidth / 2f, paint)
        bitmap.recycle()
    }
}