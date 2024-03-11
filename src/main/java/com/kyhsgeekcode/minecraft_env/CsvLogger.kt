package com.kyhsgeekcode.minecraft_env

import java.io.File


class CsvLogger(
    path: String,
    private val enabled: Boolean = false,
) {
    private val file = File(path)
    private val writer = file.bufferedWriter()

    fun log(message: String) {
        if (!enabled)
            return
        val timestamp = printWithTimeFormatter.format(java.time.LocalDateTime.now())
        writer.write("$timestamp,$message")
        writer.newLine()
        writer.flush()
    }

    fun close() {
        writer.close()
    }
}