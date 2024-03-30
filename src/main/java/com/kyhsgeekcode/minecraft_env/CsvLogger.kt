package com.kyhsgeekcode.minecraft_env

import java.io.File


class CsvLogger(
    path: String,
    private val enabled: Boolean = false,
    private val profile: Boolean = false,
) {
    private val file = File(path)
    private val writer = file.bufferedWriter()

    fun log(message: String) {
        if (!enabled && !profile)
            return
        val timestamp = printWithTimeFormatter.format(java.time.LocalDateTime.now())
        writer.write("$timestamp,$message")
        writer.newLine()
        writer.flush()
    }

    fun profileStartPrint(tag: String) {
        if (profile) {
            writer.write("${printWithTimeFormatter.format(java.time.LocalDateTime.now())}, start, $tag")
            writer.newLine()
            writer.flush()
        }
    }

    fun profileEndPrint(tag: String) {
        if (profile) {
            writer.write("${printWithTimeFormatter.format(java.time.LocalDateTime.now())}, end, $tag")
            writer.newLine()
            writer.flush()
        }
    }

    fun close() {
        writer.close()
    }
}