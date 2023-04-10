package com.yg.pdf.adapter

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.yg.pdf.bean.FilterStatusBean
import com.yg.pdf.R
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.GPUImageView


class MyPagerAdapter(private var mViewList: ArrayList<FilterStatusBean>) : PagerAdapter() {
    private var mCurrentView: View? = null
    override fun getCount(): Int {
        return mViewList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflate = View.inflate(container.context, R.layout.item_pager_filter, null)
        val gPUImageView = inflate.findViewById<GPUImageView>(R.id.ivIcon)
        gPUImageView.setScaleType(GPUImage.ScaleType.CENTER_INSIDE)
        inflate.tag = position.toString()
            gPUImageView.setImage(mViewList[position].bitmap)
        container.addView(inflate)
        return inflate
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        mCurrentView = `object` as View
    }

    fun getPrimaryItem(): View? {
        return mCurrentView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)

    }
}