package com.yg.pdf.base

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.gyf.immersionbar.ImmersionBar
import com.yg.pdf.R


abstract class BaseActivity<T : ViewDataBinding> : AppCompatActivity() {
    lateinit var mBinding: T
    var isShowAd: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this)
            .statusBarColor(R.color.toolbar_color)
            .statusBarDarkFont(false)
            .fitsSystemWindows(true)
            .init()
        mBinding = DataBindingUtil.setContentView(this, getLayoutId())
        initData(savedInstanceState)
    }

    abstract fun initData(savedInstanceState: Bundle?)
    abstract fun getLayoutId(): Int
    fun nextActivity(cls: Class<*>) {
        val intent = Intent(this, cls)
        startActivity(intent)
    }

    fun nextActivity(cls: Class<*>, bundle: Bundle) {
        val intent = Intent(this, cls).putExtra("data", bundle)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.unbind()
    }


    fun lacksPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) ==
                PackageManager.PERMISSION_DENIED
    }


    fun uploadTroas(valueMicros: Long) {
//        FirebaseUtils.uploadTroas(valueMicros,this)
    }

}