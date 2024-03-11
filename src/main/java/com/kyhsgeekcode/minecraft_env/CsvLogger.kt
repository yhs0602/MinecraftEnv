package com.kyhsgeekcode.minecraft_env

import java.io.File


class CsvLogger(
    path: String,
) {
    private val file = File(path)
    private val writer = file.bufferedWriter()

    fun log(message: String) {
        val timestamp = printWithTimeFormatter.format(java.time.LocalDateTime.now())
        writer.write("$timestamp,$message")
        writer.newLine()
    }

    fun close() {
        writer.close()
    }
}