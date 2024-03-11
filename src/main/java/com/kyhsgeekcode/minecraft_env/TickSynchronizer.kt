package com.kyhsgeekcode.minecraft_env

import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock

internal class TickSynchronizer {
    private val lock = ReentrantLock()
    private val clientActionApplied: Condition = lock.newCondition()
    private val serverTickCompleted: Condition = lock.newCondition()

    @Volatile
    private var terminating = false // 종료 상태 추적

    // 클라이언트에서 액션 적용 후 호출
    fun notifyServerTickStart() {
        println("notifyServerTickStart")
        lock.lock()
        try {
            clientActionApplied.signal()
        } finally {
            lock.unlock()
        }
    }

    // 서버에서 틱 시작 전 대기
    fun waitForClientAction() {
        println("waitForClientAction")
        lock.lock()
        try {
            while (!terminating) {
                clientActionApplied.await()
                if (terminating) { // 깨어난 후 종료 상태 검사
                    break
                }
            }
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        } finally {
            lock.unlock()
        }
    }

    // 서버 틱 완료 후 클라이언트 관찰 시작을 알림
    fun notifyClientSendObservation() {
        println("notifyClientSendObservation")
        lock.lock()
        try {
            serverTickCompleted.signal()
        } finally {
            lock.unlock()
        }
    }

    // 클라이언트에서 서버 틱 완료 후 관찰 전송 대기
    fun waitForServerTickCompletion() {
        println("waitForServerTickCompletion")
        lock.lock()
        try {
            while (!terminating) {
                serverTickCompleted.await()
                if (terminating) { // 깨어난 후 종료 상태 검사
                    break
                }
            }
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        } finally {
            lock.unlock()
        }
    }

    // 종료 메소드
    fun terminate() {
        println("terminate")
        lock.lock()
        try {
            terminating = true
            clientActionApplied.signalAll() // 모든 대기 중인 스레드 깨우기
            serverTickCompleted.signalAll()
        } finally {
            lock.unlock()
        }
    }
}