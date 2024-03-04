package com.kyhsgeekcode.minecraft_env

import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock


class TickSynchronizer {
    private val lock = ReentrantLock()

    // 클라이언트가 액션 적용을 완료했음을 나타내는 조건
    private val clientActionApplied: Condition = lock.newCondition()

    // 서버 틱이 완료되어 클라이언트가 관찰을 보낼 준비가 됨을 나타내는 조건
    private val serverTickCompleted: Condition = lock.newCondition()

    // 클라이언트에서 액션 적용 후 호출
    fun notifyServerTickStart() {
        lock.lock()
        try {
            clientActionApplied.signal()
        } finally {
            lock.unlock()
        }
    }

    // 서버에서 틱 시작 전 대기
    fun waitForClientAction() {
        lock.lock()
        try {
            clientActionApplied.await()
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        } finally {
            lock.unlock()
        }
    }

    // 서버 틱 완료 후 클라이언트 관찰 시작을 알림
    fun notifyClientSendObservation() {
        lock.lock()
        try {
            serverTickCompleted.signal()
        } finally {
            lock.unlock()
        }
    }

    // 클라이언트에서 서버 틱 완료 후 관찰 전송 대기
    fun waitForServerTickCompletion() {
        lock.lock()
        try {
            serverTickCompleted.await()
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        } finally {
            lock.unlock()
        }
    }
}
