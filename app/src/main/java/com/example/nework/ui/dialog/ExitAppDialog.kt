package com.example.nework.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.nework.R
import com.example.nework.auth.AppAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ExitAppDialog : DialogFragment() {

    @Inject
    lateinit var appAuth: AppAuth

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setView(R.layout.dialog_exit_profile)
            .setNeutralButton(R.string.no) { dialog, _ ->
                dialog.cancel()
                return@setNeutralButton
            }
            .setPositiveButton(R.string.yes) { _, _ ->
                appAuth.removeAuth()
                return@setPositiveButton
            }.create()
    }
}
