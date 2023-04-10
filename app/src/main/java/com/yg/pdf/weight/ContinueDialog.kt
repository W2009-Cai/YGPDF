package com.yg.pdf.weight

import android.content.Context
import android.content.Intent
import com.lxj.xpopup.core.BottomPopupView
import com.yg.pdf.R
import com.yg.pdf.databinding.DialogContinueBinding
import com.yg.pdf.ui.CameraActivity
import com.yg.pdf.utils.SavePicUtil

class ContinueDialog(context: Context) : BottomPopupView(context) {
    lateinit var binding: DialogContinueBinding

    override fun onCreate() {
        super.onCreate()
        binding = DialogContinueBinding.bind(popupImplView)
        binding.btnCancel.setOnClickListener {
            dismissWith {
                SavePicUtil.cleanPic(context)
            }
        }
        binding.btnStart.setOnClickListener {
            dismissWith {
                context.startActivity(Intent(context, CameraActivity::class.java))
            }
        }
    }

    override fun getImplLayoutId(): Int {
        return R.layout.dialog_continue

    }
}