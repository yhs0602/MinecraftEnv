package com.kyhsgeekcode.minecraft_env

import java.time.format.DateTimeFormatter

val printWithTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSS")
fun printWithTime(msg: String) {
    if (true)
        println("${printWithTimeFormatter.format(java.time.LocalDateTime.now())} $msg")
}