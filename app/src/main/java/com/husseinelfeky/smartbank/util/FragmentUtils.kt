package com.husseinelfeky.smartbank.util

import android.graphics.Color
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.husseinelfeky.smartbank.R
import com.kaopiz.kprogresshud.KProgressHUD

fun Fragment.showError(
    throwable: Throwable?,
    @StringRes actionButtonRes: Int? = null,
    onActionButtonClicked: () -> Unit = {}
) {
    showError(
        if (throwable != null) {
            throwable.printStackTrace()
            throwable.localizedMessage
        } else {
            getString(R.string.error_unknown)
        },
        actionButtonRes,
        onActionButtonClicked
    )
}

fun Fragment.showError(
    @StringRes errorMessageRes: Int,
    @StringRes actionButtonRes: Int? = null,
    onActionButtonClicked: () -> Unit = {}
) {
    showError(getString(errorMessageRes), actionButtonRes, onActionButtonClicked)
}

fun Fragment.showError(
    errorMessage: String,
    @StringRes actionButtonRes: Int? = null,
    onActionButtonClicked: () -> Unit = {}
) {
    Snackbar.make(
        requireActivity().findViewById(android.R.id.content),
        errorMessage,
        Snackbar.LENGTH_LONG
    ).apply {
        view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red_600))

        if (actionButtonRes != null) {
            setActionTextColor(Color.WHITE)
            setAction(actionButtonRes) {
                onActionButtonClicked()
            }
        }

        show()
    }
}

fun Fragment.showSuccess(
    @StringRes successMessageRes: Int,
    @StringRes actionButtonRes: Int? = null,
    onActionButtonClicked: () -> Unit = {}
) {
    showSuccess(getString(successMessageRes), actionButtonRes, onActionButtonClicked)
}

fun Fragment.showSuccess(
    successMessage: String,
    @StringRes actionButtonRes: Int? = null,
    onActionButtonClicked: () -> Unit = {}
) {
    Snackbar.make(
        requireActivity().findViewById(android.R.id.content),
        successMessage,
        Snackbar.LENGTH_LONG
    ).apply {
        view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green_500))

        if (actionButtonRes != null) {
            setActionTextColor(Color.WHITE)
            setAction(actionButtonRes) {
                onActionButtonClicked()
            }
        }

        show()
    }
}

fun Fragment.buildProgressDialog(): KProgressHUD {
    return KProgressHUD.create(requireContext())
        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
        .setAnimationSpeed(2)
        .setDimAmount(0.5f)
}
