package com.yg.pdf.weight

import android.content.Context
import android.content.Intent
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.yg.pdf.listener.OnAdjustChange
import com.lxj.xpopup.core.BottomPopupView
import com.yg.pdf.R
import com.yg.pdf.databinding.DialogAdjustBinding

class AdjustPopup(context: Context) : BottomPopupView(context) {
    companion object {
        const val ContrastRatio = "ContrastRatio"
        const val Brightness = "Brightness"
        const val Details = "Details"
    }

    lateinit var listener: OnAdjustChange
    private lateinit var binding: DialogAdjustBinding
    var selectType = ContrastRatio
    var contrastRatioSize = 0
    var brightnessSize = 0
    var detailsSize = 0
    override fun getImplLayoutId(): Int {
        return R.layout.dialog_adjust
    }

    override fun onCreate() {
        super.onCreate()
        binding = DialogAdjustBinding.bind(popupImplView)
        binding.ivClose.setOnClickListener {
            dismiss()
        }
        binding.ivFinish.setOnClickListener {
            dismiss()
        }
        binding.tvLeft.setOnClickListener {
            if (!binding.tvLeft.isSelected) {
                binding.tvLeft.isSelected = true
                selectType = ContrastRatio
                binding.tvCenter.isSelected = false
                binding.tvRight.isSelected = false
                binding.seekBar.progress = contrastRatioSize
            }
        }
        binding.tvCenter.setOnClickListener {
            if (!binding.tvCenter.isSelected) {
                binding.tvCenter.isSelected = true
                selectType = Brightness
                binding.tvLeft.isSelected = false
                binding.tvRight.isSelected = false
                binding.seekBar.progress = brightnessSize

            }

        }
        binding.tvRight.setOnClickListener {
            if (!binding.tvRight.isSelected) {
                binding.tvRight.isSelected = true
                selectType = Details
                binding.tvLeft.isSelected = false
                binding.tvCenter.isSelected = false
                binding.seekBar.progress = detailsSize
            }
        }
        binding.seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            //当滑块进度改变时，会执行该方法下的代码
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                listener.onAdjustChange(selectType, progress)
                when (selectType) {
                    ContrastRatio -> {
                        contrastRatioSize = progress
                    }
                    Brightness -> {
                        brightnessSize = progress
                    }
                    Details -> {
                        detailsSize = progress
                    }
                }
            }

            //当开始滑动滑块时，会执行该方法下的代码
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            //当结束滑动滑块时，会执行该方法下的代码
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        binding.tvLeft.isSelected = true
    }

    fun nextActivity(cls: Class<*>) {
        val intent = Intent(context, cls)
        context.startActivity(intent)
    }
}