package com.kyhsgeekcode.minecraft_env

import java.time.format.DateTimeFormatter

val printWithTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSS")
var doPrintWithTime = false
fun printWithTime(msg: String) {
    if (doPrintWithTime)
        println("${printWithTimeFormatter.format(java.time.LocalDateTime.now())} $msg")
}