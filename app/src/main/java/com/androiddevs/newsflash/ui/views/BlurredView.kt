package com.androiddevs.newsflash.ui.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.cardview.widget.CardView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.renderscript.Allocation
import androidx.renderscript.Element
import androidx.renderscript.RenderScript
import androidx.renderscript.ScriptIntrinsicBlur


class BlurredView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : CardView(context, attrs, defStyle) {

    private var renderScript: RenderScript? = null

    private var script: ScriptIntrinsicBlur? = null


    init {
        visibility = View.INVISIBLE
        alpha = 0F
    }


    var viewContentBitmap: Bitmap? = null

    override fun onAttachedToWindow() {
        if (!isInEditMode) {
            super.onAttachedToWindow()
            renderScript = RenderScript.create(context.applicationContext)
            script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
        }
    }

    fun setView(view: View) {
        viewContentBitmap = captureViewContent(view)
        blurBitmapWithRenderscript()
        setBlurredBackground()
    }

    private fun captureViewContent(view: View): Bitmap? {
        return if (view.measuredHeight != 0 && view.measuredWidth != 0) {
            val image = Bitmap.createBitmap(
                view.measuredWidth,
                view.measuredHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(image)
            view.draw(canvas)
            //Make it frosty
            val paint = Paint()
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DARKEN)
            canvas.drawBitmap(image, 0F, 0F, paint)
            cropBitmapToViewLocation(image)
        } else {
            null
        }
    }

    private fun cropBitmapToViewLocation(image: Bitmap): Bitmap {
        if (y + measuredHeight <= image.height && x + measuredWidth <= image.width) {
            val matrix = Matrix()
            matrix.setScale(0.3f, 0.3f)
            val bitmap = Bitmap.createBitmap(
                image,
                x.toInt(),
                y.toInt(),
                measuredWidth,
                measuredHeight,
                matrix,
                true
            )
            image.recycle()
            return bitmap
        } else {
            return image
        }
    }


    private fun blurBitmapWithRenderscript() {
        viewContentBitmap?.let {
            //this will blur the bitmapOriginal with a radius of 25 and save it in bitmapOriginal
            val input: Allocation = Allocation.createFromBitmap(
                renderScript,
                viewContentBitmap
            ) //use this constructor for best performance, because it uses USAGE_SHARED mode which reuses memory
            val output: Allocation = Allocation.createTyped(renderScript, input.type)
            script?.setRadius(25f)
            script?.setInput(input)
            script?.forEach(output)
            output.copyTo(viewContentBitmap)
        }
    }

    private fun setBlurredBackground() {
        if (viewContentBitmap != null) {
            val drawable = RoundedBitmapDrawableFactory.create(resources, viewContentBitmap).apply {
                cornerRadius = radius
            }
            background = drawable
            startAlphaAnimation()
        }
    }

    private fun startAlphaAnimation() {
        visibility = View.VISIBLE
        animate().alpha(1F).setDuration(400).start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        viewContentBitmap?.recycle()
        renderScript?.destroy()
        script?.destroy()
    }
}