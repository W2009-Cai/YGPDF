package com.yg.pdf.utils

import android.annotation.SuppressLint
import android.os.Environment
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


/**
 * author :  ChenSen
 * data  :  2018/3/15
 * desc :
 */
object FileUtil {
    private val rootFolderPath =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + File.separator + "PDF"
    private val rootFolderDocumentsPath =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath + File.separator + "PDF"
//    private val rootFolderPath = App.getRootPath() + File.separator + "CameraDemo"


    @SuppressLint("SimpleDateFormat")
    fun createPDFFile(folderName: String = "PDF File"): File? {
        return try {
            val rootFile = File(rootFolderDocumentsPath + File.separator + folderName)
            if (!rootFile.exists())
                rootFile.mkdirs()

            val timeStamp = SimpleDateFormat("yyyyMMddHHmmssSSS").format(Date())
            val fileName = "PDF$timeStamp.pdf"
            File(rootFile.absolutePath + File.separator + fileName).apply {
                if (!exists())
                    createNewFile()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


}