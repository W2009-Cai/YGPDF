package com.yg.pdf.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.yg.pdf.R
import com.yg.pdf.utils.ViewUtils


class ImageListAdapter(private var mViewList: ArrayList<String>) : PagerAdapter() {
    override fun getCount(): Int {
        return mViewList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflate = View.inflate(container.context, R.layout.item_pager_img, null)
        val gPUImageView = inflate.findViewById<ImageView>(R.id.ivIcon)
        ViewUtils.imageUrl(gPUImageView,mViewList[position])
        container.addView(inflate)
        return inflate
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}