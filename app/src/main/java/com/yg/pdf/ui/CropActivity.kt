package com.yg.pdf.ui

import ando.file.core.FileUtils
import android.os.Bundle
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.canhub.cropper.CropImageView
import com.yg.ad.utils.PDFAdError
import com.yg.ad.utils.PDFAdListener
import com.yg.pdf.AdLoadManager
import com.yg.pdf.R
import com.yg.pdf.adapter.CropPagerAdapter
import com.yg.pdf.base.BaseActivity
import com.yg.pdf.bean.CropImageBean
import com.yg.pdf.databinding.ActivityCropBinding
import com.yg.pdf.utils.RemoteConfigManager
import com.yg.pdf.utils.SavePicUtil
import com.yg.pdf.utils.log
import com.yg.pdf.utils.saveImageInternal

class CropActivity : BaseActivity<ActivityCropBinding>() {
    private var pathList = arrayListOf<String>()
    private var cropList = arrayListOf<CropImageBean>()
    private lateinit var adapter: CropPagerAdapter
    var mPosition = 0
    private var isDone = false
    override fun initData(savedInstanceState: Bundle?) {
        pathList = intent.getBundleExtra("data")!!.getStringArrayList("list")!!
        pathList.forEach {
            cropList.add(CropImageBean(it, 0, null))
        }
        mBinding.tvTitle.text = "1/${pathList.size}"
        refreshSwitchUI()
        adapter = CropPagerAdapter(cropList)
        mBinding.viewPager.adapter = adapter
        mBinding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int,
            ) {
            }

            override fun onPageSelected(position: Int) {
                mPosition = position
                refreshSwitchUI()
            }

            override fun onPageScrollStateChanged(state: Int) {
            }

        })
        mBinding.viewPager.currentItem = mPosition
        mBinding.ivLeft.setOnClickListener {
            getCropImageView().getCroppedImage()?.let { bitmap ->
                val uri = saveImageInternal(bitmap, this)
                cropList[mPosition].saveUri = uri
            }
            mPosition -= 1
            mBinding.viewPager.currentItem = mPosition
        }
        mBinding.ivRight.setOnClickListener {
            getCropImageView().getCroppedImage()?.let { bitmap ->
                val uri = saveImageInternal(bitmap, this)
                cropList[mPosition].saveUri = uri
            }
            mPosition += 1
            mBinding.viewPager.currentItem = mPosition
        }
        mBinding.tvLeft.setOnClickListener {
            cropList[mPosition].rotate = cropList[mPosition].rotate - 90
            getCropImageView().rotatedDegrees = cropList[mPosition].rotate
        }
        mBinding.tvRight.setOnClickListener {
            cropList[mPosition].rotate = cropList[mPosition].rotate + 90
            getCropImageView().rotatedDegrees = cropList[mPosition].rotate
        }
        mBinding.tvCrop.setOnClickListener {
            getCropImageView().resetCropRect()
        }
        mBinding.btnStart.setOnClickListener {
            if (isDone) {
//                nextFilter()
                AdLoadManager.showInterstitialAd(this, "", object : PDFAdListener {
                    override fun adClose() {
                        super.adClose()
                        nextFilter()
                    }

                    override fun adError(adError: PDFAdError?, isLoadFailed: Boolean) {
                        nextFilter()
                    }
                })
            } else {
                //下一张
                if (mPosition + 1 < pathList.size) {
                    mBinding.ivRight.callOnClick()
                }
            }
        }
        mBinding.ivDelete.setOnClickListener {
            FileUtils.deleteFile(pathList[mPosition])
            pathList.removeAt(mPosition)
            SavePicUtil.save(pathList)
            if (pathList.isEmpty()) {
                onBackPressed()
            }
            adapter.notifyDataSetChanged()
            refreshSwitchUI()
        }
        mBinding.ivBack.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        AdLoadManager.showNative(mBinding.adLayout, object : PDFAdListener {})
    }

    private fun nextFilter() {
        getCropImageView().getCroppedImage()?.let { bitmap ->
            val uri = saveImageInternal(bitmap, this)
            cropList[mPosition].saveUri = uri
        }
        //到滤镜页面
        val cropBitmap = arrayListOf<String>()
        cropList.forEach {
            it.saveUri?.let { uri ->
                log("saveFile = $uri")
                cropBitmap.add(uri.toString())
            }

        }
        //进入滤镜
        nextActivity(ImageFilterActivity::class.java, Bundle().apply {
            putStringArrayList("list", cropBitmap)
        })
    }

    private fun refreshSwitchUI() {
        mBinding.tvTitle.text = "${mPosition + 1}/${pathList.size}"
        if (pathList.size > 1) {
            mBinding.ivLeft.visibility = View.VISIBLE
            mBinding.ivRight.visibility = View.VISIBLE
            if (mPosition == 0) {
                mBinding.ivLeft.visibility = View.GONE
            }
            if (mPosition + 1 == pathList.size) {
                mBinding.ivRight.visibility = View.GONE
                mBinding.btnStart.setText(R.string.done)
                isDone = true
            }
        } else {
            mBinding.ivLeft.visibility = View.GONE
            mBinding.ivRight.visibility = View.GONE
            if (mPosition + 1 == pathList.size) {
                mBinding.btnStart.setText(R.string.done)
                isDone = true
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (RemoteConfigManager.getmRemoteConfig() != null && RemoteConfigManager.getmRemoteConfig()
                ?.inside != null && RemoteConfigManager.getmRemoteConfig()?.inside
                ?.isShowBackAd == 1 && AdLoadManager.canShowGpAd()
        ) {
            AdLoadManager.showInterstitialAd(this, "", null)
        }
    }

    private fun getCropImageView(position: Int? = null): CropImageView {
        return if (position == null) {
            mBinding.viewPager.findViewWithTag<View>(mBinding.viewPager.currentItem.toString())
                .findViewById(R.id.crop_image)
        } else {
            mBinding.viewPager.findViewWithTag<View>(position.toString())
                .findViewById(R.id.crop_image)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_crop
    }
}