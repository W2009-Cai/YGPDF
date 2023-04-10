package com.yg.pdf.weight

import ando.file.core.FileUtils
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.yg.pdf.listener.OnChangePDFListener
import com.lxj.xpopup.core.BottomPopupView
import com.yg.pdf.R
import com.yg.pdf.databinding.PopLayoutBinding
import com.yg.pdf.utils.ViewUtils
import java.io.File


class MorePopup(context: Context) : BottomPopupView(context), View.OnClickListener {
    private lateinit var binding: PopLayoutBinding
    lateinit var path: String
    lateinit var onChangePDFListener: OnChangePDFListener
    override fun onCreate() {
        super.onCreate()
        binding = DataBindingUtil.bind(popupImplView)!!
        ViewUtils.setOnClickListeners(
            this,
            binding.tvDelete,
            binding.tvShare,
            binding.tvRename,
            binding.ivClose,
            binding.ivDelete,
            binding.ivRename,
            binding.ivShare,
        )
    }

    override fun getImplLayoutId(): Int {
        return R.layout.pop_layout
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.tvDelete, binding.ivDelete -> {
                dismissWith {
                    FileUtils.deleteFile(path)
                    onChangePDFListener.onChange()
                }
            }
            binding.tvShare, binding.ivShare -> {
                dismissWith {
                    val intent = Intent(Intent.ACTION_SEND)
                    val uri =
                        FileProvider.getUriForFile(
                            context,
                            "com.yg.pdf.fileProvider",
                            File(path)
                        )
                    intent.putExtra(Intent.EXTRA_STREAM, uri)
                    intent.type = "application/pdf"
                    context.startActivity(intent)
                }

            }
            binding.tvRename, binding.ivRename -> {
                dismissWith {
                    onChangePDFListener.onRename()
                }

            }
            binding.ivClose -> {
                dismiss()
            }
        }
    }
}