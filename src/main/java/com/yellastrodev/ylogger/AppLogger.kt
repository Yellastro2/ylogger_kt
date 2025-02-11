package com.yellastrodev.ymtserial.ylogger

import android.annotation.SuppressLint
import android.content.Context
import com.yellastrodev.ymtserial.ylogger.Logger.Companion.logFileDateFormat
import java.io.File
import java.io.*
import java.util.Date
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object AppLogger {
    @SuppressLint("StaticFieldLeak")
    var logger: Logger? = null

    val DEBUG = true

    fun init(context: Context, maxFileSize: Long = 1024 * 1024) {
        if (logger == null) {
            logger = Logger(context, maxFileSize)
        }
    }

    /**
     * Adds a log handler
     * @param handler - a function handler that takes three parameters: log level, tag, and message
     */
    fun addLogHandler(handler: (level: String,tag: String,message: String) -> Unit ) { logger?.addLogHandler(handler) }

    /**
     * Sets the date format for logs
     * @param format - the date format
     */
    fun setDateFormat(format: String) {
        logger?.setDateFormate(format)
    }

    fun info( message: String, tag: String? = null) {
        logger?.info(tag, message)
    }

    fun debug(message: String, tag: String? = null) {
        if (DEBUG)
            logger?.info(tag, message)
    }

    fun warn(message: String, tag: String? = null) {
        logger?.warn(tag, message)
    }

    fun error(message: String, tag: String? = null, e: Exception? = null) {
        logger?.error(tag, message, e)
    }

    /**
     * Returns a zip file with all log files
     * @param context - the context
     * @param fLastFileName - the last log file name, it and previous ones will be ignored
     * @return the zip file
     */
    fun getZipLogs(context: Context,fLastFileName: String): File {
        val filesDir = context.getExternalFilesDir(null) // или другой путь к вашим файлам
        val files = filesDir?.listFiles()?.filter { it.isFile } ?: emptyList()
        val zipFile = File(filesDir, "archive.zip")
        zipFiles(files, zipFile, fLastFileName)

        return zipFile
    }



    private fun zipFiles(files: List<File>, zipFile: File,lastFileName: String) {
        if (zipFile.exists()) {
            zipFile.delete()
        }

        val lastFileDate = if (lastFileName == "0") {
            Date(0)
        }else  logFileDateFormat.parse(lastFileName.substring(4, lastFileName.length -4))

        ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile))).use { out ->
            files.filter { file ->
                if (file.name == "app.log") {
                    true
                } else if (!file.name.startsWith("app")) {
                    false
                }else {
                    val fileName = file.name.replace(" ","_").replace(":","-")
                    val fileDate = logFileDateFormat.parse(fileName.substring(4, file.name.length - 4))
                    fileDate.after(lastFileDate)
                }
            }.forEach { file ->
                FileInputStream(file).use { fi ->
                    BufferedInputStream(fi).use { origin ->
                        val entry = ZipEntry(file.name.replace(" ","_").replace(":","-"))
                        out.putNextEntry(entry)
                        origin.copyTo(out, 1024)
                    }
                }
            }
        }
    }



    fun getLastLogFile(): File? {
        return logger?.logFile
    }

    fun clearLastLog(){
        getLastLogFile()?.let { logFile ->
            if (logFile.exists()) {
                val deleted = logFile.delete()
            }
        }
    }
}
