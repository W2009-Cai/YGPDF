package com.yg.pdf.ui

import ando.file.core.FileUtils
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.yg.ad.utils.PDFAdError
import com.yg.ad.utils.PDFAdListener
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.yg.pdf.AdLoadManager
import com.yg.pdf.R
import com.yg.pdf.adapter.ImageListAdapter
import com.yg.pdf.base.BaseActivity
import com.yg.pdf.databinding.ActivityImageListBinding
import com.yg.pdf.utils.RemoteConfigManager
import com.yg.pdf.utils.SavePicUtil

class ImageListActivity : BaseActivity<ActivityImageListBinding>() {
    private var pathList = arrayListOf<String>()
    private var mPosition = 0
    private var isDone = false
    private lateinit var imageListAdapter: ImageListAdapter

    override fun initData(savedInstanceState: Bundle?) {
        AdLoadManager.showNative(mBinding.adLayout, object : PDFAdListener {})
        pathList = intent.getBundleExtra("data")!!.getStringArrayList("list")!!
        mBinding.rv.linear(LinearLayoutManager.HORIZONTAL).setup {
            addType<String>(R.layout.item_recycle)
            R.id.cardView.onFastClick {
                mBinding.viewPager.setCurrentItem(modelPosition, false)
                mPosition = modelPosition
                refreshSwitchUI()
            }
            R.id.ivDelete.onFastClick {
                val model = getModel<String>()
                pathList.removeAt(modelPosition)
                //每次都保存图片
                SavePicUtil.save(pathList)
                FileUtils.deleteFile(model)
                if (pathList.isEmpty()) {
                    onBackPressed()
                }
                adapter.notifyItemRemoved(modelPosition)
                imageListAdapter.notifyDataSetChanged()
                //刷新第一张
                mPosition = 0
                mBinding.viewPager.setCurrentItem(mPosition, false)
                refreshSwitchUI()
            }
        }.models = pathList
        mBinding.tvTitle.text = "1/${pathList.size}"
        imageListAdapter = ImageListAdapter(pathList)
        mBinding.viewPager.adapter = imageListAdapter
        mBinding.ivBack.setOnClickListener { onBackPressed() }
        mBinding.btnStart.setOnClickListener {
            AdLoadManager.showInterstitialAd(this, "", object : PDFAdListener {
                override fun adClose() {
                    super.adClose()
                    nextActivity(CropActivity::class.java, Bundle().apply {
                        putStringArrayList("list", pathList)
                    })
                }

                override fun adError(adError: PDFAdError?, isLoadFailed: Boolean) {
                    nextActivity(CropActivity::class.java, Bundle().apply {
                        putStringArrayList("list", pathList)
                    })
                }
            })
//            nextActivity(CropActivity::class.java, Bundle().apply {
//                putStringArrayList("list", pathList)
//            })
        }
        refreshSwitchUI()
    }

    private fun refreshSwitchUI() {
        mBinding.tvTitle.text = "${mPosition + 1}/${pathList.size}"
        if (pathList.size > 1) {
            if (mPosition + 1 == pathList.size) {
                mBinding.btnStart.setText(R.string.next_step)
                isDone = true
            }
        } else {
            if (mPosition + 1 == pathList.size) {
                mBinding.btnStart.setText(R.string.next_step)
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

    override fun getLayoutId(): Int {
        return R.layout.activity_image_list
    }
}