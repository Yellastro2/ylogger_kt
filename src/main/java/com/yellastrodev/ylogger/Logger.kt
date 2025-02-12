package com.yellastrodev.ymtserial.ylogger

import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class Logger(val logDir: File?, private val maxFileSize: Long = 1024 * 1024) { // 1 MB по умолчанию


    val RESET = "\u001B[0m"
    val RED = ""//"\u001B[31m"
    val YELLOW = "\u001B[33m"
    val BLUE = "\u001B[34m"

    companion object{
        val BASE_LOG_FILENAME = "app.log"

        val logFileDateFormat = SimpleDateFormat("yyyy.MM.dd_HH-mm-ss", Locale.getDefault())
    }

    var logFile: File? = null

    init {
        logDir?.let {
            if (!logDir.exists()) logDir.mkdirs()
            logFile = File(logDir, BASE_LOG_FILENAME)
        }

        setPrintLogHandler { level, tag, message, e ->

//            var logMessage = " $level/$tag: $message"


            val color = when (level) {
                "INFO" -> "\u001B[34m"  // Синий
                "WARN" -> "\u001B[33m"  // Желтый
                "ERROR" -> "\u001B[31m" // Красный
                else -> "\u001B[0m"     // Сброс цвета
            }

            val logMessage = "$color${dateFormat.format(Date())} $level $tag: $message$RESET"
            if (level == "ERROR") {
                System.err.println(logMessage)
                e?.printStackTrace()
            } else {
                println(logMessage)
            }
        }

    }

    private var dateFormat = SimpleDateFormat("yyyy.MM.dd_HH-mm-ss", Locale.getDefault())

    private var printLogHandler: ((String, String, String, e: Exception?) -> Unit)? = null

    fun setPrintLogHandler(handler: (level: String, tag: String, message: String, e: Exception?) -> Unit) {
        printLogHandler = handler
    }



    fun setDateFormate(format: String) {
        dateFormat = SimpleDateFormat(format, Locale.getDefault())
    }


    private val customHandlers = mutableListOf<(String, String, String, e: Exception?) -> Unit>()

    fun addLogHandler(handler: (level: String, tag: String, message: String, e: Exception?) -> Unit ) {
        customHandlers.add(handler)
    }

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
        var logMessage = "${dateFormat.format(Date())} $level/$tag: $message"

        e?.let { logMessage += "\n${it.stackTraceToString()}" }
        logDir?.let { logToFile(logMessage) }
        printLogHandler?.invoke(level, tag, message, e)
//        logToConsole(level,tag,message, e)
        customHandlers.forEach { it(level,tag,message, e) }
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



        val color = when (level) {
            "INFO" -> BLUE
            "WARN" -> YELLOW
            "ERROR" -> RED
            else -> RESET
        }

        val logMessage = "$color$level/$tag: $message$RESET"

        if (level == "ERROR") {
            System.err.println(logMessage)
            e?.printStackTrace()
        } else {
            println(logMessage)
        }

//        when (level) {
//            "INFO" -> Log.i(tag, message)
//            "WARN" -> Log.w(tag, message)
//            "ERROR" -> e?.let {Log.e(tag, message,e) } ?: {Log.e(tag, message)}
//            else -> Log.d(tag, message)
//        }
    }

    private fun rotateLogFileIfNeeded() {
        if (logFile!!.length() >= maxFileSize) {
            val newFileName = "app_${logFileDateFormat.format(Date())}.log"
            val newLogFile = File(logFile!!.parent, newFileName)
            logFile!!.renameTo(newLogFile)
            logFile = File(logDir, BASE_LOG_FILENAME)
        }
    }


}
