package com.yg.pdf.adapter

import android.graphics.BitmapFactory
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.yg.pdf.bean.CropImageBean
import com.canhub.cropper.CropImageView
import com.yg.pdf.R


class CropPagerAdapter(private var mViewList: ArrayList<CropImageBean>) : PagerAdapter() {
    override fun getCount(): Int {
        return mViewList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflate = View.inflate(container.context, R.layout.item_image, null)
        val cropImageView = inflate.findViewById<CropImageView>(R.id.crop_image)
        inflate.tag = position.toString()
        cropImageView.setImageBitmap(
            BitmapFactory.decodeStream(
                container.context.contentResolver.openInputStream(
                    Uri.parse(mViewList[position].path)
                )
            )
        )
        cropImageView.rotateImage(mViewList[position].rotate)
        cropImageView.scaleType = CropImageView.ScaleType.CENTER_CROP
        container.addView(inflate)
        return inflate
    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}