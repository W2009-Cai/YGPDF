package com.yg.pdf

import ando.file.core.FileUtils
import android.Manifest
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.constraintlayout.widget.ConstraintSet
import com.blankj.utilcode.util.AppUtils
import com.drake.brv.BindingAdapter
import com.drake.brv.utils.grid
import com.drake.brv.utils.setup
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.lxj.xpopup.XPopup
import com.yg.pdf.base.BaseActivity
import com.yg.pdf.databinding.ActivityMainBinding
import com.yg.pdf.listener.OnChangeNameListener
import com.yg.pdf.listener.OnChangePDFListener
import com.yg.pdf.ui.AboutActivity
import com.yg.pdf.ui.CameraActivity
import com.yg.pdf.ui.H5Activity
import com.yg.pdf.ui.PDFActivity
import com.yg.pdf.utils.SavePicUtil
import com.yg.pdf.utils.toast
import com.yg.pdf.weight.*
import java.io.File
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class MainActivity : BaseActivity<ActivityMainBinding>() {
    var pathList = arrayListOf<Any>()

    override fun initData(savedInstanceState: Bundle?) {
        initView()
        AdLoadManager.loadInterstitial(this)
        if (SavePicUtil.getPicList().isNotEmpty()) {
            XPopup.Builder(this).asCustom(ContinueDialog(this)).show()
        }
        mBinding.ivTakePhoto.setOnClickListener {
            if (isGranted()) {
                SavePicUtil.cleanPic(this)
                nextActivity(CameraActivity::class.java)
            } else {
                requestPermissions()
            }
        }
    }

    fun facebookHashKey() {
        try {
            val info: PackageInfo = packageManager
                .getPackageInfo(AppUtils.getAppPackageName(), PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
        } catch (e: NoSuchAlgorithmException) {
        }
    }

    private fun initView() {
        mBinding.rv.grid(3).setup {
            it.addItemDecoration(ImageItemDecoration())
            addType<File>(R.layout.item_pdf)
            R.id.clContent.onFastClick {
                nextActivity(PDFActivity::class.java, Bundle().apply {
                    val model = getModel<File>()
                    putString("path", model.absolutePath)
                })
            }
            R.id.ivMore.onFastClick {
                val model = getModel<File>()
                showMoreDialog(model)
            }
        }.models = pathList
        mBinding.ivTakePhoto.setOnClickListener {
            if (!isGranted()) {
                requestPermissions()
            } else {
//                nextActivity(NewCameraActivity::class.java)
            }
        }
        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            mBinding.drawerLayout,
            mBinding.toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        )
        actionBarDrawerToggle.syncState()
        mBinding.drawerLayout.addDrawerListener(actionBarDrawerToggle)
        mBinding.drawerLayout.findViewById<TextView>(R.id.tv_privacy).setOnClickListener {
            mBinding.drawerLayout.closeDrawers()
            H5Activity.startActivity(
                this,
                getString(R.string.privacy_policy),
                Constant.PRIVATE_URL
            )
        }
        mBinding.drawerLayout.findViewById<TextView>(R.id.tv_about).setOnClickListener {
            mBinding.drawerLayout.closeDrawers()
            nextActivity(AboutActivity::class.java)
        }
        mBinding.drawerLayout.findViewById<TextView>(R.id.tv_exit).setOnClickListener {
            mBinding.drawerLayout.closeDrawers()
            XPopup.Builder(this).isDestroyOnDismiss(true).asCustom(ExitDialog(this)).show()
        }
    }

    private fun BindingAdapter.BindingViewHolder.showMoreDialog(model: File) {
        XPopup.Builder(this@MainActivity)
            .atView(findView(R.id.ivMore))
            .hasShadowBg(false)
            .isDestroyOnDismiss(true)
            .asCustom(MorePopup(this@MainActivity).apply {
                path = model.absolutePath
                onChangePDFListener = object : OnChangePDFListener {
                    override fun onChange() {
                        refreshData()
                    }

                    override fun onRename() {
                        showRenameDialog(model.absolutePath)
                    }
                }
            })
            .show()
    }

    private fun refreshData() {
        pathList.clear()
        getPDFList()
        mBinding.rv.adapter?.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        if (isGranted()) {
            refreshData()
        }
        if (pathList.isEmpty()) {
            mBinding.rv.visibility = View.GONE
            mBinding.llEmpty.visibility = View.VISIBLE
            val constraintSet = ConstraintSet()
            constraintSet.clone(mBinding.cl)
            constraintSet.connect(
                mBinding.ivTakePhoto.id,
                ConstraintSet.START,
                ConstraintSet.PARENT_ID,
                ConstraintSet.START
            )
            constraintSet.applyTo(mBinding.cl)
        } else {
            mBinding.rv.visibility = View.VISIBLE
            mBinding.llEmpty.visibility = View.GONE
            val constraintSet = ConstraintSet()
            constraintSet.clone(mBinding.cl)
            constraintSet.clear(mBinding.ivTakePhoto.id, ConstraintSet.START)
            constraintSet.applyTo(mBinding.cl)
        }
    }

    private fun isGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            XXPermissions.isGranted(
                this,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_MEDIA_IMAGES
            )
        } else {
            XXPermissions.isGranted(
                this,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }

    }

    private fun getPDFList() {
        val filePath = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOCUMENTS
        ).absolutePath + File.separator + "PDF" + File.separator + "PDF File"
        val rootFile = File(filePath)
        rootFile.listFiles()?.let { it1 -> pathList.addAll(it1) }
    }

    private fun showRenameDialog(fileName: String) {
        XPopup.Builder(this).asCustom(RenamePopup(this).apply {
            listener = object : OnChangeNameListener {
                override fun onRename(name: String) {
                    FileUtils.renameFile(oldFilePath = fileName, newFileName = name)
                    refreshData()
                }
            }
        }).show()
    }

    private fun requestPermissions() {
        XXPermissions.with(this)
            .permission(Permission.READ_MEDIA_IMAGES)
            .permission(Permission.WRITE_EXTERNAL_STORAGE)
            .permission(Permission.CAMERA)
            .request(object : OnPermissionCallback {
                override fun onGranted(
                    permissions: MutableList<String>,
                    all: Boolean,
                ) {
                }

                override fun onDenied(
                    permissions: MutableList<String>,
                    never: Boolean,
                ) {
                    if (never) {
                        // 如果是被永久拒绝就跳转到应用权限系统设置页面
                        XPopup.Builder(this@MainActivity).asConfirm(
                            "small limit",
                            "If you need to use our supernatural ability, give it a try"
                        ) {
                            XXPermissions.startPermissionActivity(this@MainActivity, permissions)
                        }
                            .show()
                    } else {
                        toast("failure to capture")
                    }
                }
            })
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }
}