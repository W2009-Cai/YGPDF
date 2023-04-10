package com.yg.pdf.utils

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.yg.pdf.R
import java.text.SimpleDateFormat
import java.util.*

object ViewUtils {

    /**
     * 设置多个view的是否可用属性
     */
    fun setIsEnable(boolean: Boolean, vararg view: View) {
        for (v in view) {
            v.isEnabled = boolean
        }
    }

    /**
     * 设置多个view的是否可见属性
     */
    fun setIsVisibility(int: Int, vararg view: View) {
        for (v in view) {
            v.visibility = int
        }
    }

    /**
     * 设置多个view的hint属性
     */
    fun setTextHint(replaceText: String, vararg view: TextView) {
        for (v in view) {
            v.hint = v.hint.toString().format(replaceText)
        }
    }

    /**
     * 设置多个view的linster
     */
    fun setOnClickListeners(listener: View.OnClickListener, vararg view: View) {
        for (v in view) {
            v.setOnClickListener(listener)
        }
    }

    @BindingAdapter("selectBackground")
    @JvmStatic
    fun selectBackground(view: ConstraintLayout, select: Boolean) {
        if (select) {
            view.setBackgroundResource(R.drawable.border_filter)
        } else {
            view.setBackgroundResource(R.drawable.border_filter_normal)
        }

    }

    @BindingAdapter("timeText")
    @JvmStatic
    fun timeText(view: TextView, time: Long) {
        val date = Date(time)
        val format = SimpleDateFormat("MM-dd hh:mm:ss", Locale.getDefault())
        val localTime = format.format(date)
        view.text = localTime
    }

    @BindingAdapter("imageUrl")
    @JvmStatic
    fun imageUrl(view: ImageView, score: Any) {
        Glide.with(view.context)
            .load(score)
            .into(view)
    }


}