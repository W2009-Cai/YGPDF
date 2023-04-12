package com.yg.pdf.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import com.wdeo3601.pdfview.PDFView
import com.yg.pdf.AdLoadManager
import com.yg.pdf.R
import com.yg.pdf.base.BaseActivity
import com.yg.pdf.databinding.ActivityPdfBinding
import com.yg.pdf.utils.RemoteConfigManager
import java.io.File

class PDFActivity : BaseActivity<ActivityPdfBinding>() {
    private lateinit var filePath: String
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun initData(savedInstanceState: Bundle?) {
        filePath = intent.getBundleExtra("data")!!.getString("path")!!
        mBinding.toolbar.title = File(filePath).name
        setSupportActionBar(mBinding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        mBinding.pdfView.setOffscreenPageLimit(2)
        // 是否支持缩放
        mBinding.pdfView.isCanZoom(true)
        // 设置最大缩放倍数,最大支持20倍
        mBinding.pdfView.setMaxScale(10f)
        // 添加水印
//        mBinding.pdfView.setWatermark(R.drawable.ic_default_watermark)
        // 设置当前页变化的回调监听
        mBinding.pdfView.setOnPageChangedListener(object : PDFView.OnPageChangedListener {
            @SuppressLint("SetTextI18n")
            override fun onPageChanged(currentPageIndex: Int, totalPageCount: Int) {
                mBinding.tvPages.text = "${currentPageIndex + 1}/$totalPageCount"
                // show current page number
            }
        })
// 从本地文件打开 pdf
        mBinding.pdfView.showPdfFromPath(filePath)
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
        return R.layout.activity_pdf
    }
}