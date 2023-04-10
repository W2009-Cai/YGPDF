package com.yg.pdf.weight

import android.content.Context
import android.content.Intent
import com.yg.pdf.listener.OnChangeNameListener
import com.blankj.utilcode.util.ToastUtils
import com.lxj.xpopup.core.BottomPopupView
import com.yg.pdf.R
import com.yg.pdf.databinding.DialogInputBinding

class RenamePopup(context: Context) : BottomPopupView(context) {

    lateinit var listener: OnChangeNameListener
    private lateinit var binding: DialogInputBinding
    override fun getImplLayoutId(): Int {
        return R.layout.dialog_input
    }

    override fun onCreate() {
        super.onCreate()
        binding = DialogInputBinding.bind(popupImplView)
        binding.tvCancel.setOnClickListener {
            dismiss()
        }
        binding.tvConfirm.setOnClickListener {
            if (binding.etInput.text.isEmpty()) {
                ToastUtils.showShort("Please input pdf name first")
            } else {
                dismissWith {
                    listener.onRename(binding.etInput.text.toString())
                }
            }
        }
    }

    fun nextActivity(cls: Class<*>) {
        val intent = Intent(context, cls)
        context.startActivity(intent)
    }
}