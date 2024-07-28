package com.example.weatherapp.ui

import android.app.Activity
import androidx.appcompat.app.AlertDialog

data class AlertDialogContext(
    val title: String,
    val message: String,
    val primaryBtnText: String,
    val onClickPrimaryBtn: (() -> Unit) ?= null,
)

fun Activity.displayAlert(dialogContext: AlertDialogContext) {
    with(dialogContext) {
        AlertDialog.Builder(this@displayAlert).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(primaryBtnText) { dialog, _ ->
                onClickPrimaryBtn?.invoke()
                dialog.dismiss()
            }
        }.also {
            it.create()
            it.show()
        }
    }
}
