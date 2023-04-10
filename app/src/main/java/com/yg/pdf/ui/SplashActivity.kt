package com.yg.pdf.ui

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.yg.ad.utils.PDFAdError
import com.yg.ad.utils.PDFAdListener
import com.yg.ad.utils.PDFSplashAd
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.SpanUtils
import com.gyf.immersionbar.ImmersionBar
import com.yg.pdf.AdLoadManager
import com.yg.pdf.Constant
import com.yg.pdf.MainActivity
import com.yg.pdf.R
import com.yg.pdf.base.BaseActivity
import com.yg.pdf.databinding.ActivitySplashBinding
import java.util.*

class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    private var isGotoMain = false
    private var isReady = false
    override fun initData(savedInstanceState: Bundle?) {
        AdLoadManager.loadSplash(this)
        AdLoadManager.loadNative(this)
        initPrivacy()
        ImmersionBar.with(this)
            .statusBarColor(R.color.white)
            .statusBarDarkFont(true)
            .fitsSystemWindows(true)
            .init()
        mBinding.btn.setOnClickListener {
            SPUtils.getInstance().put(Constant.AGREE_PRIVACY, true)
            initPrivacy()
        }
    }

    private fun initPrivacy() {
        val agreePrivacy = SPUtils.getInstance().getBoolean(Constant.AGREE_PRIVACY, false)
        if (agreePrivacy) {
//            AdLoadManager.getInstance().loadSplash(this)
            mBinding.tvPrivacy.visibility = View.GONE
            mBinding.btn.visibility = View.GONE
            mBinding.pb.visibility = View.VISIBLE
            mBinding.tvLoad.visibility = View.VISIBLE
            createTimer(10)
        } else {
            mBinding.tvPrivacy.visibility = View.VISIBLE
            mBinding.btn.visibility = View.VISIBLE
            mBinding.pb.visibility = View.GONE
            mBinding.tvLoad.visibility = View.GONE
            SpanUtils.with(mBinding.tvPrivacy)
                .append(getString(R.string.privacy_info))
                .append(" ")
                .append(getString(R.string.privacy_policy))
                .setClickSpan(ContextCompat.getColor(baseContext, R.color.privacy_color), true) {
                    H5Activity.startActivity(
                        this,
                        getString(R.string.privacy_policy),
                        Constant.PRIVATE_URL
                    )
                }
                .append(".")
                .create()

        }
    }

    private fun createTimer(seconds: Long) {
        val countDownTimer: CountDownTimer = object : CountDownTimer(seconds * 1000, 80) {
            override fun onTick(millisUntilFinished: Long) {
                mBinding.pb.progress += 1
                if (mBinding.pb.progress % 10 == 0) {
                    Log.e("result", mBinding.pb.progress.toString())
                    if (!isReady) {
                        showSwitchAd()
                    } else {

                    }
                }
            }

            override fun onFinish() {
                if (!isShowAd) {
                    gotoMain()
                }
            }
        }
        countDownTimer.start()
    }

    fun gotoMain() {
        if (isGotoMain) {
            return
        }
        isGotoMain = true
        mBinding.pb.progress = 100
        val intent = Intent(this, MainActivity::class.java).apply {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        this.intent.getStringExtra(Constant.TYPE)?.let {
            intent.putExtra(Constant.TYPE, it)
        }
        startActivity(intent)
        val task: TimerTask = object : TimerTask() {
            override fun run() {
                finish()
            }
        }
        val timer = Timer()
        timer.schedule(task, 3 * 1000)

    }
    fun showSwitchAd() {
        if (PDFSplashAd.getInstance().isReady) {
            isReady = true
            AdLoadManager.showSplashAd(this, object : PDFAdListener {
                override fun adClose() {
                    super.adClose()
                    gotoMain()
                }

                override fun adShow(ecpm: Double?, adNetworkId: String?) {
                    super.adShow(ecpm, adNetworkId)
                    isShowAd = true
                }

                override fun adError(adError: PDFAdError?, isLoadFailed: Boolean) {
                    super.adError(adError, isLoadFailed)
                    gotoMain()
                }
            })
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_splash
    }

}