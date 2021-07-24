package com.husseinelfeky.smartbank.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.InputType
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View.OnKeyListener
import android.view.animation.AnimationUtils
import android.view.inputmethod.BaseInputConnection
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.core.content.withStyledAttributes
import androidx.core.view.children
import com.husseinelfeky.smartbank.R
import timber.log.Timber

class PinCodeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    enum class KeypadType(val code: Int) {

        DEVICE_KEYPAD(0),
        CUSTOM_KEYPAD(1);

        companion object {
            fun get(code: Int): KeypadType {
                return values().find {
                    it.code == code
                }!!
            }
        }
    }

    private val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    private lateinit var boxes: Array<PinCodeBox>
    private var lastTypedBox = -1

    private var onCodeTyped: ((String) -> Unit)? = null

    private val indexLastBox: Int
        get() = boxes.size - 1

    private var keypadType = DEFAULT_KEYPAD_TYPE

    init {
        context.withStyledAttributes(attrs, R.styleable.PinCodeView, defStyleAttr) {
            val boxesCount = getInt(R.styleable.PinCodeView_pinBoxesCount, DEFAULT_PIN_BOXES_COUNT)
            keypadType = KeypadType.get(
                getInt(R.styleable.PinCodeView_keypadType, DEFAULT_KEYPAD_TYPE.code)
            )

            for (i in 0 until boxesCount) {
                addView(PinCodeBox(context))
            }

            boxes = children.run {
                val iterator = iterator()
                Array(boxesCount) { iterator.next() as PinCodeBox }
            }
        }

        orientation = HORIZONTAL
        isFocusable = true
        isFocusableInTouchMode = true

        setOnKeyListener(OnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                if (KeyEvent.KEYCODE_0 <= keyCode && keyCode <= KeyEvent.KEYCODE_9) {
                    if (lastTypedBox < indexLastBox) {
                        boxes[++lastTypedBox].setText(event.displayLabel.toString())
                        if (lastTypedBox == indexLastBox) {
                            hideKeyboard()
                            onCodeTyped?.invoke(getCompleteCode())
                        }
                        return@OnKeyListener true
                    }
                } else if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (lastTypedBox > -1) {
                        boxes[lastTypedBox--].text = null
                        return@OnKeyListener true
                    }
                }
            }
            false
        })
    }

    override fun onCheckIsTextEditor(): Boolean {
        return true
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo?): InputConnection {
        val inputConnection = BaseInputConnection(this, false)
        outAttrs?.apply {
            actionLabel = null
            inputType = InputType.TYPE_CLASS_NUMBER
            imeOptions = EditorInfo.IME_ACTION_NONE
        }
        return inputConnection
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (isEnabled && keypadType == KeypadType.DEVICE_KEYPAD) {
            if (event?.action == MotionEvent.ACTION_DOWN) {
                return requestFocus()
            } else if (event?.action == MotionEvent.ACTION_UP) {
                return showKeyboard()
            }
            return super.onTouchEvent(event)
        } else {
            return false
        }
    }

    private fun showKeyboard(): Boolean {
        hideError()
        return imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard(): Boolean {
        return imm.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    private fun isPinCodeTyped(): Boolean {
        return lastTypedBox == indexLastBox
    }

    private fun getTypedCode(): String {
        val typedCode = StringBuilder()
        for (box in boxes) {
            val boxText = box.text.toString()
            if (boxText.isNotEmpty()) {
                typedCode.append(boxText)
            } else {
                break
            }
        }
        return typedCode.toString()
    }

    private fun getCompleteCode(): String {
        return if (isPinCodeTyped()) {
            getTypedCode()
        } else {
            throw IllegalStateException("Code is not completely typed yet.")
        }
    }

    fun clearCode() {
        lastTypedBox = -1
        boxes.forEach {
            it.text = null
        }
    }

    fun showError() {
        val color = Color.DKGRAY
        boxes.forEach {
            it.backgroundTintList = ColorStateList.valueOf(color)
        }

        startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_incorrect_otp))
    }

    private fun hideError() {
        val color = Color.BLACK
        boxes.forEach {
            it.backgroundTintList = ColorStateList.valueOf(color)
        }
    }

    fun setOnCodeTypedListener(onCodeTyped: ((String) -> Unit)?) {
        this.onCodeTyped = onCodeTyped
    }

    fun setKeypadType(keypadType: KeypadType) {
        this.keypadType = keypadType
    }

    fun sendKeyEvent(keyCode: Int) {
        if (keypadType == KeypadType.CUSTOM_KEYPAD) {
            requestFocus()
            dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
        } else {
            Timber.d("Cannot invoke key events as the keypad type is set to ${KeypadType.DEVICE_KEYPAD}.")
        }
    }

    companion object {
        private const val DEFAULT_PIN_BOXES_COUNT = 4
        private val DEFAULT_KEYPAD_TYPE = KeypadType.DEVICE_KEYPAD
    }
}
