package com.husseinelfeky.smartbank.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.core.content.withStyledAttributes
import com.google.android.material.button.MaterialButton
import com.husseinelfeky.smartbank.R
import com.husseinelfeky.smartbank.databinding.ViewLoadingButtonBinding

class LoadingButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding = ViewLoadingButtonBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    private val button: MaterialButton = binding.button as MaterialButton
    private val progressCircular: ProgressBar = binding.progressCircular

    init {
        context.withStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            defStyleAttr,
            0
        ) {
            button.text = getString(R.styleable.LoadingButton_android_text)
            button.isEnabled = getBoolean(R.styleable.LoadingButton_android_enabled, true)

            if (hasValue(R.styleable.LoadingButton_cornerRadius)) {
                button.cornerRadius = getDimensionPixelSize(
                    R.styleable.LoadingButton_cornerRadius,
                    button.cornerRadius
                )
            }
        }
    }

    override fun setOnClickListener(clickListener: OnClickListener?) {
        button.setOnClickListener(clickListener)
    }

    override fun setEnabled(isEnabled: Boolean) {
        button.isEnabled = isEnabled
    }

    fun showLoading() {
        button.visibility = View.INVISIBLE
        progressCircular.visibility = View.VISIBLE
    }

    fun hideLoading() {
        progressCircular.visibility = View.GONE
        button.visibility = View.VISIBLE
    }

    fun setText(text: String) {
        button.text = text
    }
}
