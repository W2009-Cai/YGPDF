package com.yg.pdf.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebSettings
import com.gyf.immersionbar.ImmersionBar
import com.yg.pdf.R
import com.yg.pdf.base.BaseActivity
import com.yg.pdf.databinding.ActivityWebBinding

class H5Activity : BaseActivity<ActivityWebBinding>() {
    companion object {
        fun startActivity(context: Context, title: String, url: String) {
            context.startActivity(Intent(context, H5Activity::class.java).apply {
                putExtra("data", Bundle().apply {
                    putString("url", url)
                    putString("title", title)
                })
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun initData(savedInstanceState: Bundle?) {
        val title =
            intent.getBundleExtra("data")!!.getString("title", getString(R.string.privacy_policy))
        val url = intent.getBundleExtra("data")!!
            .getString("url", "https://www.simplepdf.xyz/privacy.html")
        mBinding.toolbar.title = title
        setSupportActionBar(mBinding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        ImmersionBar.with(this)
            .reset()
            .statusBarDarkFont(true)
            .fitsSystemWindows(true)
            .init()
        //        binding.titleNameTv.setText(title);
        val webSettings: WebSettings = mBinding.web.settings
//如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.javaScriptEnabled = true
        //设置自适应屏幕，两者合用
        webSettings.useWideViewPort = true //将图片调整到适合webview的大小
        webSettings.loadWithOverviewMode = true // 缩放至屏幕的大小
        //缩放操作
        webSettings.setSupportZoom(true) //支持缩放，默认为true。是下面那个的前提。
        webSettings.builtInZoomControls = true //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.displayZoomControls = false //隐藏原生的缩放控件
        //其他细节操作
        webSettings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK //关闭webview中缓存
        webSettings.allowFileAccess = true //设置可以访问文件
        webSettings.javaScriptCanOpenWindowsAutomatically = true //支持通过JS打开新窗口
        webSettings.loadsImagesAutomatically = true //支持自动加载图片
        webSettings.defaultTextEncodingName = "uft-8" //设置编码格式
        webSettings.domStorageEnabled = true
        mBinding.web.loadUrl(url)


    }

    override fun getLayoutId(): Int {
        return R.layout.activity_web
    }
}