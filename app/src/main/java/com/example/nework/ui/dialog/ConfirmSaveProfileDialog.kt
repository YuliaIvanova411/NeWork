package com.example.nework.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.nework.R


class ConfirmSaveProfileDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setView(R.layout.dialog_confirm_profile)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.cancel()
                return@setPositiveButton
            }.create()
    }
}
