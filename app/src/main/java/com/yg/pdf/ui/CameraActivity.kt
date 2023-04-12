package com.yg.pdf.ui

import android.net.Uri
import android.os.Bundle
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.gyf.immersionbar.ImmersionBar
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.controls.Flash
import com.yg.ad.utils.PDFAdError
import com.yg.ad.utils.PDFAdListener
import com.yg.pdf.AdLoadManager
import com.yg.pdf.R
import com.yg.pdf.base.BaseActivity
import com.yg.pdf.databinding.ActivityCameraBinding
import com.yg.pdf.utils.RemoteConfigManager
import com.yg.pdf.utils.SavePicUtil
import com.yg.pdf.utils.saveImageInternal

class CameraActivity : BaseActivity<ActivityCameraBinding>() {
    private var pathList = arrayListOf<String>()
    override fun initData(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .reset()
            .transparentStatusBar()
            .init()
        mBinding.textureView.setLifecycleOwner(this)
        mBinding.textureView.open()
        mBinding.textureView.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                val uri = saveImageInternal(result.data, this@CameraActivity)
                mBinding.ivImages.setImageURI(uri)
//                Glide.with(this@CameraActivity).load(uri)
//                    .centerCrop()
//                    .into(mBinding.ivImages)
                pathList.add(uri.toString())
                mBinding.tvImageSize.text = pathList.size.toString()
                mBinding.tvImageSize.visibility = View.VISIBLE
                SavePicUtil.save(pathList)
            }
        })

        mBinding.btnCapture.setOnClickListener {
            if (pathList.size == 15) {
                ToastUtils.showShort(getString(R.string.max_pic))
                return@setOnClickListener
            }
            mBinding.textureView.takePictureSnapshot()
        }
        mBinding.ivImages.setOnClickListener {
            //跳转到已拍摄列表
            if (pathList.isEmpty()) {
                return@setOnClickListener
            }
//            nextActivity(ImageListActivity::class.java, Bundle().apply {
//                putStringArrayList("list", pathList)
//            })
            AdLoadManager.showInterstitialAd(this, "", object : PDFAdListener {
                override fun adClose() {
                    super.adClose()
                    nextActivity(ImageListActivity::class.java, Bundle().apply {
                        putStringArrayList("list", pathList)
                    })
                }

                override fun adError(adError: PDFAdError?, isLoadFailed: Boolean) {
                    nextActivity(ImageListActivity::class.java, Bundle().apply {
                        putStringArrayList("list", pathList)
                    })
                }
            })

        }
        mBinding.ivFinish.setOnClickListener {
            //下一步
            if (pathList.isEmpty()) {
                return@setOnClickListener
            }
            AdLoadManager.showInterstitialAd(this, "", object : PDFAdListener {
                override fun adClose() {
                    super.adClose()
                    nextActivity(CropActivity::class.java, Bundle().apply {
                        putStringArrayList("list", pathList)
                    })
                }

                override fun adError(adError: PDFAdError?, isLoadFailed: Boolean) {
                    super.adError(adError, isLoadFailed)
                    nextActivity(CropActivity::class.java, Bundle().apply {
                        putStringArrayList("list", pathList)
                    })
                }
            })
        }
        mBinding.ivBack.setOnClickListener {
            onBackPressed()
        }
        mBinding.ivShut.setOnClickListener {
            mBinding.ivShut.isSelected = !mBinding.ivShut.isSelected
            if (mBinding.ivShut.isSelected) {
                mBinding.textureView.flash = Flash.TORCH
            } else {
                mBinding.textureView.flash = Flash.OFF
            }
        }
        mBinding.ivTable.setOnClickListener {
            mBinding.ivTable.isSelected = !mBinding.ivTable.isSelected
            if (mBinding.ivTable.isSelected) {
                mBinding.grid.visibility = View.VISIBLE
            } else {
                mBinding.grid.visibility = View.GONE
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

    override fun onResume() {
        super.onResume()
        val list = SavePicUtil.getPicList()
        pathList.clear()
        pathList.addAll(list)
        if (pathList.isEmpty()) {
            mBinding.tvImageSize.visibility = View.GONE
        } else {
            mBinding.tvImageSize.visibility = View.VISIBLE
            mBinding.tvImageSize.text = pathList.size.toString()
            mBinding.ivImages.setImageURI(Uri.parse(pathList.last()))
//            Glide.with(this).load(pathList.last()).into(mBinding.ivImages)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_camera
    }
}