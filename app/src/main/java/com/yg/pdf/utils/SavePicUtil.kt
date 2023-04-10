package com.yg.pdf.utils

import android.content.Context
import android.net.Uri
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.SPUtils
import com.yg.pdf.Constant

object SavePicUtil {
    //保存图片
    fun save(picList: ArrayList<String>) {
        val savePic = GsonUtils.toJson(picList)
        SPUtils.getInstance().put(Constant.NO_SAVE_PATH, savePic)
    }

    //获取图片
    fun getPicList(): ArrayList<String> {
        val list = arrayListOf<String>()
        val savePic = SPUtils.getInstance().getString(Constant.NO_SAVE_PATH)
        val saveList = GsonUtils.fromJson<ArrayList<String>>(
            savePic,
            GsonUtils.getListType(String::class.java)
        )
        if (!saveList.isNullOrEmpty()) {
            list.addAll(saveList)
        }
        return list
    }

    //清除保存的图片
    fun cleanPic(context: Context) {
        val list = getPicList()
        list.forEach {
            log("删除了" + context.contentResolver.delete(Uri.parse(it), null, null) + "行")
        }
        SPUtils.getInstance().put(Constant.NO_SAVE_PATH, "")
    }
}