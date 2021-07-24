package com.husseinelfeky.smartbank.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.text.InputType
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.husseinelfeky.smartbank.R

class PinCodeBox @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatEditText(context, attrs, defStyleAttr) {

    init {
        val color = Color.BLACK
        setTextColor(ContextCompat.getColor(context, R.color.blue_gray_500))
        setHintTextColor(color)
        setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            context.resources.getDimension(R.dimen.pin_code_box_text_size)
        )
        setBackgroundResource(R.drawable.bg_pin_code_box)
        setPadding(0, 0, 0, 0)

        val spacing = context.resources.getDimensionPixelSize(R.dimen.pin_code_box_spacing)
        layoutParams = LinearLayout.LayoutParams(
            context.resources.getDimensionPixelSize(R.dimen.pin_code_box_width),
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            leftMargin = spacing
            rightMargin = spacing
        }

        backgroundTintList = ColorStateList.valueOf(color)
        typeface = Typeface.DEFAULT_BOLD
        gravity = Gravity.CENTER
        inputType = InputType.TYPE_CLASS_NUMBER
        alpha = 0.3f
        isFocusable = false
        isFocusableInTouchMode = false
        isClickable = false
        isEnabled = false

        initTextWatcher()
    }

    private fun initTextWatcher() {
        addTextChangedListener {
            alpha = if (it?.length == 0) {
                0.3f
            } else {
                1.0f
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return false
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}
