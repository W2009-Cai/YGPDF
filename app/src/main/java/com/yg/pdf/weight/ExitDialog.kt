package com.yg.pdf.weight

import android.content.Context
import com.blankj.utilcode.util.AppUtils
import com.lxj.xpopup.core.BottomPopupView
import com.yg.pdf.R
import com.yg.pdf.databinding.DialogExitBinding

class ExitDialog(context: Context) : BottomPopupView(context) {
    lateinit var binding: DialogExitBinding

    override fun onCreate() {
        super.onCreate()
        binding = DialogExitBinding.bind(popupImplView)
        binding.btnCancel.setOnClickListener {
            dismissWith {
                AppUtils.exitApp()
            }
        }
        binding.btnStart.setOnClickListener {
            dismiss()
        }
    }

    override fun getImplLayoutId(): Int {
        return R.layout.dialog_exit

    }
}