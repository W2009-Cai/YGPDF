package com.yg.pdf.weight

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ConvertUtils

class ImageItemDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        when {
            position % 3 == 0 -> {
                outRect.set(
                    ConvertUtils.dp2px(8F),
                    ConvertUtils.dp2px(8F),
                    ConvertUtils.dp2px(4F),
                    0
                )
            }
            position % 2 == 0 -> {
                outRect.set(
                    ConvertUtils.dp2px(4F),
                    ConvertUtils.dp2px(8F),
                    ConvertUtils.dp2px(8F),
                    0
                )
            }
            else -> {
                outRect.set(
                    ConvertUtils.dp2px(4F),
                    ConvertUtils.dp2px(8F),
                    ConvertUtils.dp2px(4F),
                    0
                )
            }
        }
    }
}