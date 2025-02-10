package com.yellastrodev.ymtserial.ylogger

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class Logger(private val context: Context, private val maxFileSize: Long = 1024 * 1024) { // 1 MB по умолчанию



    companion object{
        val BASE_LOG_FILENAME = "app.log"

        val logFileDateFormat = SimpleDateFormat("yyyy.MM.dd_HH-mm-ss", Locale.getDefault())
    }
    var logFile = File(context.getExternalFilesDir(null), BASE_LOG_FILENAME)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())


    private val customHandlers = mutableListOf<(String) -> Unit>()

    fun addLogHandler(handler: (String) -> Unit ) { customHandlers.add(handler) }

    fun info(tag: String?, message: String) {
        log("INFO", tag, message)
    }

    fun warn(tag: String?, message: String) {
        log("WARN", tag, message)
    }

    fun error(tag: String?, message: String, e: Exception? = null) {
        log("ERROR", tag, message, e)
    }

    private fun log(level: String, tag: String?, message: String, e: Exception? = null) {
        val stackTrace = Thread.currentThread().stackTrace
        val caller = stackTrace.getOrNull(7) // Обычно это вызывающий метод
        val tag = tag ?: caller?.fileName ?: "Unknown"
        val preMessage = "${caller?.className?.substringAfterLast('.') ?: "Unknown"}.${caller?.methodName ?: "Unknown"}:${caller?.lineNumber ?: 0}: "

        val message = "$preMessage: $message"
        var logMessage = "${logFileDateFormat.format(Date())} $level/$tag: $message"

        e?.let { logMessage += "\n${Log.getStackTraceString(e)}" }
        logToFile(logMessage)
        logToConsole(level,tag,message, e)
        customHandlers.forEach { it(logMessage) }
    }

    private fun logToFile(message: String) {
        rotateLogFileIfNeeded()
        try {
            val writer = FileWriter(logFile, true)
            writer.append(message).append("\n")
            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun logToConsole(level: String, tag: String, message: String, e: Exception? = null) {
        when (level) {
            "INFO" -> Log.i(tag, message)
            "WARN" -> Log.w(tag, message)
            "ERROR" -> e?.let {Log.e(tag, message,e) } ?: {Log.e(tag, message)}
            else -> Log.d(tag, message)
        }
    }

    private fun rotateLogFileIfNeeded() {
        if (logFile.length() >= maxFileSize) {
            val newFileName = "app_${logFileDateFormat.format(Date())}.log"
            val newLogFile = File(logFile.parent, newFileName)
            logFile.renameTo(newLogFile)
            logFile = File(context.getExternalFilesDir(null), BASE_LOG_FILENAME)
        }
    }


}
