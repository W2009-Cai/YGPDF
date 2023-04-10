package com.yg.pdf.ui

import android.os.Bundle
import android.view.MenuItem
import com.blankj.utilcode.util.AppUtils
import com.gyf.immersionbar.ImmersionBar
import com.yg.pdf.R
import com.yg.pdf.base.BaseActivity
import com.yg.pdf.databinding.ActivityAboutBinding

class AboutActivity : BaseActivity<ActivityAboutBinding>() {
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun initData(savedInstanceState: Bundle?) {
        mBinding.toolbar.setTitle(R.string.about_us)
        setSupportActionBar(mBinding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        ImmersionBar.with(this)
            .reset()
            .statusBarDarkFont(true)
            .fitsSystemWindows(true)
            .init()
        val info = getString(R.string.app_name) + " V" + AppUtils.getAppVersionName()
        mBinding.tvTitle.text = info

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_about
    }
}