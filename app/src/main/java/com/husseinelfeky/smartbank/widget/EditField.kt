package com.husseinelfeky.smartbank.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.getIntOrThrow
import androidx.core.content.withStyledAttributes
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textfield.TextInputLayout.END_ICON_NONE
import com.husseinelfeky.smartbank.R
import com.husseinelfeky.smartbank.databinding.ViewEditFieldBinding

class EditField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding = ViewEditFieldBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    private val textInputLayout: TextInputLayout = binding.textInputLayout
    val editText: TextInputEditText = binding.editText

    fun interface TextValidator {
        fun onValidate(text: String): Boolean
    }

    private var textValidator: TextValidator? = null

    init {
        textInputLayout.id = id
        editText.id = id

        context.withStyledAttributes(
            attrs,
            R.styleable.EditField,
            defStyleAttr,
            0
        ) {
            editText.setText(getString(R.styleable.EditField_android_text))
            textInputLayout.hint = getString(R.styleable.EditField_android_hint)
            textInputLayout.endIconMode = getInteger(
                R.styleable.EditField_endIconMode,
                END_ICON_NONE
            )
            if (hasValue(R.styleable.EditField_endIconDrawable)) {
                textInputLayout.endIconDrawable = getDrawable(
                    R.styleable.EditField_endIconDrawable
                )
            }

            editText.inputType = getInt(
                R.styleable.EditField_android_inputType,
                InputType.TYPE_CLASS_TEXT
            )

            if (hasValue(R.styleable.EditField_android_maxLength)) {
                editText.filters = arrayOf(
                    InputFilter.LengthFilter(getIntOrThrow(R.styleable.EditField_android_maxLength))
                )
            }

            editText.setLines(getInt(R.styleable.EditField_android_lines, 1))

            textInputLayout.isEnabled = getBoolean(R.styleable.EditField_android_enabled, true)
            if (hasValue(R.styleable.EditField_inputEnabled)) {
                setInputEnabled(getBoolean(R.styleable.EditField_inputEnabled, true))
            }
            textInputLayout.isErrorEnabled = getBoolean(R.styleable.EditField_errorEnabled, true)
        }
    }

    override fun setEnabled(isEnabled: Boolean) {
        textInputLayout.isEnabled = isEnabled
    }

    private fun setInputEnabled(isInputEnabled: Boolean) {
        val textColor = editText.currentTextColor
        editText.isEnabled = isInputEnabled
        editText.setTextColor(textColor)
    }

    private fun isPasswordField(): Boolean {
        val inputType = editText.inputType
        return inputType == InputType.TYPE_NUMBER_VARIATION_PASSWORD
                || inputType == InputType.TYPE_TEXT_VARIATION_PASSWORD
                || inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                || inputType == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
    }

    fun getText(trimmed: Boolean = true): CharSequence? {
        return if (trimmed && !isPasswordField()) {
            editText.text?.trim()
        } else {
            editText.text
        }
    }

    fun setText(@StringRes resourceId: Int, validateText: Boolean = false) {
        editText.setText(resourceId)
        if (validateText) {
            textValidator?.onValidate(getText().toString())
        }
    }

    fun setText(text: CharSequence, validateText: Boolean = false) {
        editText.setText(text)
        if (validateText) {
            textValidator?.onValidate(getText().toString())
        }
    }

    fun getHint(): CharSequence? {
        return textInputLayout.hint
    }

    fun setHint(@StringRes resourceId: Int) {
        setHint(context.getString(resourceId))
    }

    private fun setHint(text: CharSequence) {
        textInputLayout.hint = text
    }

    fun showError(@StringRes resourceId: Int) {
        showError(context.getString(resourceId))
    }

    fun showError(text: CharSequence) {
        textInputLayout.error = text
    }

    fun hideError() {
        textInputLayout.error = null
    }

    fun setEndIconMode(@TextInputLayout.EndIconMode endIconMode: Int) {
        textInputLayout.endIconMode = endIconMode
    }

    fun setEndIconDrawableResource(@DrawableRes resourceId: Int) {
        setEndIconDrawable(ContextCompat.getDrawable(context, resourceId))
    }

    private fun setEndIconDrawable(endIconDrawable: Drawable?) {
        textInputLayout.endIconDrawable = endIconDrawable
    }

    fun setEndIconOnClickListener(clickListener: OnClickListener) {
        textInputLayout.setEndIconOnClickListener(clickListener)
    }

    fun setEndIconOnClickListener(clickListener: (View) -> Unit) {
        textInputLayout.setEndIconOnClickListener(clickListener)
    }

    fun setOnClickListener(clickListener: (View) -> Unit) {
        editText.isFocusable = false
        editText.isClickable = true
        editText.setOnClickListener(clickListener)
    }

    inline fun addTextChangedListener(
        crossinline beforeTextChanged: (
            text: CharSequence?,
            start: Int,
            count: Int,
            after: Int
        ) -> Unit = { _, _, _, _ -> },
        crossinline onTextChanged: (
            text: CharSequence?,
            start: Int,
            before: Int,
            count: Int
        ) -> Unit = { _, _, _, _ -> },
        crossinline afterTextChanged: (text: Editable?) -> Unit = {}
    ): TextWatcher {
        return editText.addTextChangedListener(beforeTextChanged, onTextChanged, afterTextChanged)
    }

    private fun setOnInputTypedListener(onInputTyped: (String) -> Unit) {
        var isTextWritten = false

        editText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && isTextWritten) {
                onInputTyped.invoke(getText().toString())
            }
        }

        editText.addTextChangedListener {
            isTextWritten = true
        }
    }

    fun setTextValidator(textValidator: TextValidator) {
        this.textValidator = textValidator
        setOnInputTypedListener {
            textValidator.onValidate(it)
        }
    }

    /**
     * Validate the input if [textValidator] is initialized.
     *
     * When validating many [EditField]s, you can use it in conjunction with
     * the bitwise operator "and" rather than the logical operator "&&", to
     * validate all [EditField]s, showing any error if there is any, by
     * invoking the [textValidator] callback.
     *
     * @sample com.robustastudio.hyperone.feature.login.LoginFragment.areAllFieldsFilledCorrectly
     *
     * @return the input validity
     */
    fun validateInput(): Boolean {
        return textValidator?.onValidate(getText().toString()) ?: true
    }
}
