package com.yg.pdf.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.yg.pdf.bean.FilterBean
import com.yg.pdf.bean.FilterStatusBean
import com.yg.pdf.listener.OnAdjustChange
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.Image
import com.itextpdf.text.PageSize
import com.itextpdf.text.pdf.PdfWriter
import com.lxj.xpopup.XPopup
import com.yg.pdf.MainActivity
import com.yg.pdf.R
import com.yg.pdf.adapter.MyPagerAdapter
import com.yg.pdf.base.BaseActivity
import com.yg.pdf.databinding.ActivityImageFilterBinding
import com.yg.pdf.utils.FileUtil
import com.yg.pdf.utils.SavePicUtil
import com.yg.pdf.utils.log
import com.yg.pdf.weight.AdjustPopup
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.GPUImageView
import jp.co.cyberagent.android.gpuimage.filter.*
import jp.co.cyberagent.android.gpuimage.util.Rotation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream


class ImageFilterActivity : BaseActivity<ActivityImageFilterBinding>() {
    private var pathList = arrayListOf<String>()
    private var filterStatusList = arrayListOf<FilterStatusBean>()
    private lateinit var adapter: MyPagerAdapter
    var mPosition = 0
    private var isDone = false
    private var filterList = arrayListOf(
        FilterBean(GPUImageFilter(), isSelect = true, title = R.string.filter_original),
        FilterBean(GPUImageGrayscaleFilter(), isSelect = false, title = R.string.filter_gray),
        FilterBean(GPUImageBrightnessFilter(0F), isSelect = false, title = R.string.filter_super),
        FilterBean(GPUImageRGBDilationFilter(), isSelect = false, title = R.string.filter_enhance),
        FilterBean(GPUImageSaturationFilter(0F), isSelect = false, title = R.string.filter_bw),
        FilterBean(GPUImageColorInvertFilter(), isSelect = false, title = R.string.filter_invert)
    )
    private var sharpenFilter = GPUImageSharpenFilter()
    private var brightnessFilter = GPUImageBrightnessFilter()
    private var saturationFilter = GPUImageSaturationFilter()
    private lateinit var adjustPopup: AdjustPopup
    override fun initData(savedInstanceState: Bundle?) {
//        AdLoadManager.showNative(mBinding.adLayout, object : PDFAdListener {})
        pathList = intent.getBundleExtra("data")!!.getStringArrayList("list")!!
        pathList.forEach {
            val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(Uri.parse(it)))
            filterStatusList.add(FilterStatusBean(0, GPUImageFilter(), it, bitmap))
        }
        adjustPopup = AdjustPopup(this)
        adjustPopup.listener = object : OnAdjustChange {
            override fun onAdjustChange(type: String, progress: Int) {
                val gpuImageView = getGPUImage()
                when (type) {
                    AdjustPopup.ContrastRatio -> {
                        sharpenFilter.setSharpness(progress / 100F)
                        gpuImageView.filter = sharpenFilter

                    }
                    AdjustPopup.Brightness -> {
                        brightnessFilter.setBrightness(progress / 100F)
                        gpuImageView.filter = brightnessFilter
                    }
                    AdjustPopup.Details -> {
                        saturationFilter.setSaturation(progress / 100F)
                        gpuImageView.filter = saturationFilter

                    }
                }
                filterStatusList[mPosition].bitmap = gpuImageView.capture()
            }
        }
        mBinding.tvTitle.text = "1/${pathList.size}"
        adapter = MyPagerAdapter(filterStatusList)
        mBinding.viewPager.adapter = adapter
        mBinding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                mPosition = position
                refreshSwitchUI()
            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })
        mBinding.ivLeft.setOnClickListener {
            mPosition -= 1
            mBinding.viewPager.currentItem = mPosition
        }
        mBinding.ivRight.setOnClickListener {
            mPosition += 1
            mBinding.viewPager.currentItem = mPosition
        }
        mBinding.rv.linear(orientation = LinearLayoutManager.HORIZONTAL).setup {
            addType<FilterBean>(R.layout.item_filter)
            onBind {
                val model = getModel<FilterBean>()
                val uiScope = CoroutineScope(Dispatchers.IO)
                findView<ImageView>(R.id.gpu_iv).apply {
                    uiScope.launch {
                        val gpuImage = GPUImage(context)
                        gpuImage.setFilter(model.gpuImageFilter)
                        gpuImage.setImage(
                            BitmapFactory.decodeResource(
                                resources,
                                R.drawable.ic_filter_example
                            )
                        )
                        withContext(Dispatchers.Main) {
                            setImageBitmap(gpuImage.bitmapWithFilterApplied)
                        }
                    }
                }
            }
            R.id.view.onFastClick {
                val model = getModel<FilterBean>(modelPosition)
                if (!model.isSelect) {
                    val gpuImageView = getGPUImage()
                    gpuImageView.filter = model.gpuImageFilter
                    filterStatusList[mPosition].filterPosition = modelPosition
                    filterStatusList[mPosition].bitmap = gpuImageView.capture()
                    refreshSelectFilter()
                }
            }

        }.models = filterList
        mBinding.tvLeft.setOnClickListener {
            when (filterStatusList[mPosition].rotation) {
                0 -> {
                    getGPUImage().setRotation(Rotation.ROTATION_90)
                    filterStatusList[mPosition].rotation = 90
                }
                90 -> {
                    getGPUImage().setRotation(Rotation.ROTATION_180)
                    filterStatusList[mPosition].rotation = 180
                }
                180 -> {
                    getGPUImage().setRotation(Rotation.ROTATION_270)
                    filterStatusList[mPosition].rotation = 270
                }
                270 -> {
                    getGPUImage().setRotation(Rotation.NORMAL)
                    filterStatusList[mPosition].rotation = 0
                }
            }
        }
        mBinding.tvCenter.setOnClickListener {
            XPopup.Builder(this).asCustom(adjustPopup).show()
        }
        mBinding.tvRight.setOnClickListener {
            mPosition += 1
            mBinding.viewPager.currentItem = mPosition
        }
        mBinding.tvRight.setOnClickListener {
            if (isDone) {
                createPDF()
            } else {
                //下一张
                if (mPosition + 1 < pathList.size) {
                    mBinding.ivRight.callOnClick()
                }
            }
        }
        mBinding.ivBack.setOnClickListener {
            onBackPressed()
        }
        refreshSwitchUI()
    }

    private fun getGPUImage(): GPUImageView {
        return mBinding.viewPager.findViewWithTag<View>(mBinding.viewPager.currentItem.toString())
            .findViewById(R.id.ivIcon)
    }

    private fun createPDF() {
        //生成PDF
        val uiScope = CoroutineScope(Dispatchers.IO)
        uiScope.launch {
            try {
                val document = Document()
                val file = FileUtil.createPDFFile()
                withContext(Dispatchers.IO) {
                    PdfWriter.getInstance(document, FileOutputStream(file))
                }
                document.open()
                document.pageSize = PageSize.A4
                filterStatusList.forEach {
                    document.newPage()
                    val stream = ByteArrayOutputStream()
                    it.bitmap?.compress(
                        Bitmap.CompressFormat.JPEG,
                        100,
                        stream
                    )
                    val byteArray: ByteArray = stream.toByteArray()
                    val image = Image.getInstance(byteArray)
                    // 设置图片的大小和位置
                    image.scaleToFit(document.pageSize.width, document.pageSize.height)
                    image.alignment = Element.ALIGN_CENTER
                    document.add(image)
                }
                document.close()
                ActivityUtils.finishOtherActivities(MainActivity::class.java)
                withContext(Dispatchers.Main) {
                    ToastUtils.showShort("create success！")
//                    AdLoadManager.showInterstitialAd(this@ImageFilterActivity, "", null)
                }
                //生成成功后删除
                SavePicUtil.cleanPic(this@ImageFilterActivity)
            } catch (e: Exception) {
                e.message?.let {
                    log(it)
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
//        AdLoadManager.showInterstitialAd(this, "", null)
    }

    private fun refreshSelectFilter() {
        filterList.forEachIndexed { index, filterBean ->
            filterBean.isSelect = index == filterStatusList[mPosition].filterPosition
            filterBean.notifyChange()
        }
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
                mBinding.tvRight.setText(R.string.done)
                mBinding.tvRight.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    null,
                    ContextCompat.getDrawable(this, R.drawable.ic_done),
                    null,
                    null
                )
                isDone = true
            }
        } else {
            mBinding.ivLeft.visibility = View.GONE
            mBinding.ivRight.visibility = View.GONE
            if (mPosition + 1 == pathList.size) {
                mBinding.tvRight.setText(R.string.done)
                mBinding.tvRight.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    null,
                    ContextCompat.getDrawable(this, R.drawable.ic_done),
                    null,
                    null
                )
                isDone = true
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_image_filter
    }
}