package com.lsy.viewlib.weight

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import com.lsy.viewlib.R

/**
 * Title :
 * Author: Lsy
 * Date: 1/26/21 3:16 PM
 * Version:
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class IconTextView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    //固定文字颜色、大小
    private val fixedColor: Int
    private val fixedSize: Int

    //可省略文字颜色、大小
    private val omitColor: Int
    private val omitSize: Int

    //省略字符
    private val omitStr = "..."

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.IconTextView)
        fixedColor = typedArray.getColor(R.styleable.IconTextView_fixed_txt_color, Color.BLACK)
        fixedSize = typedArray.getDimensionPixelSize(R.styleable.IconTextView_fixed_txt_size, 14)
        omitColor = typedArray.getColor(R.styleable.IconTextView_omit_txt_color, Color.BLACK)
        omitSize = typedArray.getDimensionPixelSize(R.styleable.IconTextView_omit_txt_size, 12)
        typedArray.recycle()
    }

    //固定文字画笔
    private val fixedPaint: TextPaint by lazy {
        TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = fixedSize.toFloat()
            color = fixedColor
            style = Paint.Style.FILL
        }
    }

    //可省略文字画笔
    private val omitPaint: TextPaint by lazy {
        TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = omitSize.toFloat()
            color = omitColor
            style = Paint.Style.FILL
        }
    }

    //icon画笔
    private val iconPaint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            //固定文字
            fixedTxt?.let {
                //固定文字长度
                val fixedTxtW = fixedPaint.measureText(fixedTxt)
                //可省略文字长度
                val omitTxtW = omitTxt?.let { omitPaint.measureText(omitTxt) } ?: 0f
                //图标
                var icon: Bitmap? = null
                //图标长度
                var iconW = 0
                iconRes?.let { res ->
                    icon = BitmapFactory.decodeResource(resources, res)
                    iconW = icon?.width ?: 0
                }

                val type = when {
                    fixedTxtW > measuredWidth -> 1
                    (fixedTxtW + iconW) > measuredWidth -> 2
                    (fixedTxtW + omitTxtW + iconW) > measuredWidth -> 3
                    else -> 0
                }
                //计算固定文字左边
                val fixedX = if (0 == type) (measuredWidth - (fixedTxtW + omitTxtW + iconW)) / 2 else 0f
                canvas.drawText(fixedTxt, fixedX, getBaseLine(fixedPaint, measuredHeight / 2f), fixedPaint)

                //可省略文字
                var physicalOmit = ""
                if (!omitTxt.isNullOrEmpty() && (0 == type || 3 == type)) {
                    if (0 == type) {
                        canvas.drawText(omitTxt, fixedX + fixedTxtW, getBaseLine(omitPaint, measuredHeight / 2f), omitPaint)
                    } else {
                        val freeSpace = measuredWidth - (fixedTxtW + iconW)
                        val omitW = omitPaint.measureText(omitStr)
                        val widths = FloatArray(omitTxt!!.length)
                        omitPaint.getTextWidths(omitTxt, widths)
                        if (freeSpace > omitW) {
                            //计算去掉省略符后的空间
                            val txtFree = freeSpace - omitW

                            //计算这些空间可以显示多少字
                            var tempW = 0f
                            var end = 0
                            widths.forEach { w ->
                                tempW += w
                                if (tempW < txtFree) {
                                    end++
                                } else {
                                    return@forEach
                                }
                            }
                            //取出可以显示的字符长度
                            physicalOmit = omitTxt!!.substring(0, end)
                            //加上省略
                            physicalOmit += omitStr
                            canvas.drawText(physicalOmit, fixedX + fixedTxtW, getBaseLine(omitPaint, measuredHeight / 2f), omitPaint)
                        }
                    }
                }

                //图标
                if (null != icon && (0 == type || 3 == type)) {
                    var l = if (0 == type) fixedX + fixedTxtW + omitTxtW
                    else fixedX + fixedTxtW + omitPaint.measureText(physicalOmit)
                    //不超过边界
                    if (l > (measuredWidth - icon!!.width)) {
                        l = (measuredWidth - icon!!.width).toFloat()
                    }
                    val t = (measuredHeight - icon!!.height) / 2f
                    val r = l + icon!!.width
                    val b = t + icon!!.height
                    canvas.drawBitmap(icon, null, RectF(l, t, r, b), iconPaint)
                }
            }
        }
    }

    /**
     * 得到基点
     * @param paint 画笔
     * @param centerY 中点
     */
    private fun getBaseLine(paint: Paint, centerY: Float): Float {
        val fontMetrics: Paint.FontMetrics = fixedPaint.fontMetrics
        val dx = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
        return centerY + dx
    }

    private var fixedTxt: String? = null
    private var omitTxt: String? = null
    private var iconRes: Int? = null

    /**
     * 设置内容
     */
    fun setText(fixedTxt: String, omitTxt: String? = null, icon: Int? = null) {
        this.fixedTxt = fixedTxt
        this.omitTxt = omitTxt
        this.iconRes = icon

        requestLayout()
    }
}