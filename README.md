# yLogger

YLogger is a simple and flexible casual logging library for Kotlin. It provides convenient methods for logging messages, handling log storage, and exporting logs as a zip file.

## Features
- Easy initialization
- Customizable log handlers
- Supports different log levels (info, debug, warn, error)
- Allows setting a custom date format
- Exports logs as a zip file

## Installation
Add YLogger as a module in your project or import it as a dependency.

## Usage

### Initialization
Before using YLogger, initialize it in your application:

```kotlin
AppLogger.init()
```

If a directory is specified, the log files will be written there.

```kotlin
// Initialize with file logging
val logDir = File("/path/to/logs")
AppLogger.init(logDir)
```

You can also specify a maximum file size for log storage:

```kotlin
AppLogger.init(logDir, maxFileSize = 2 * 1024 * 1024) // 2MB
```

Specify the something like this handler for correct prints in the Android console

```kotlin
AppLogger.logger?.setPrintLogHandler { level, tag, message, e ->
			when (level) {
				"INFO" -> Log.i(tag, message)
				"WARN" -> Log.w(tag, message)
				"ERROR" -> e?.let {Log.e(tag, message,e) } ?: {Log.e(tag, message)}
				else -> Log.d(tag, message)
			}
		}
```

### Logging Messages
YLogger provides multiple logging methods:

```kotlin
AppLogger.info("This is an info message")
AppLogger.debug("This is a debug message")
AppLogger.warn("This is a warning message")
AppLogger.error("This is an error message", e)
```

You can also specify a custom tag:

```kotlin
AppLogger.info("This is an info message", tag = "MyTag")
```

### Custom Log Handlers
To add a custom log handler:

```kotlin
AppLogger.addLogHandler { level, tag, message, e ->
    println("Custom Handler: [$level] [$tag]: $message")
}
```

### Custom Date Format
Set a custom date format for logs:

```kotlin
AppLogger.setDateFormat("yyyy-MM-dd HH:mm:ss")
```

### Exporting Logs
You can export all log files as a zip archive:

```kotlin
val zipFile = AppLogger.getZipLogs(context, "2024.07.15_14-37-52.log")
```

This will create a zip file containing all logs except those after `2024.07.15_14-37-52.log`.

## License
This project is open-source and free to use.

