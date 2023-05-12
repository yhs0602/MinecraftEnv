package com.kyhsgeekcode.minecraft_env

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.PlayerLookup
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.server.MinecraftServer
import net.minecraft.util.Identifier

class ServerSideLogic(private val server: MinecraftServer) {
    private val sendingObservationLock = Object()
    private var sendingObservation = false

    private val readingActionLock = Object()
    private var readingAction = false

    private val ID_END_SERVER_TICK = Identifier("minecraft_env", "end_server_tick")
    private val ID_READ_ACTION = Identifier("minecraft_env", "read_action")


    private var resetPhase: ResetPhase = ResetPhase.END_RESET

    fun onWorldTickStart() {
        when (resetPhase) {
            ResetPhase.WAIT_PLAYER_DEATH -> TODO()
            ResetPhase.WAIT_PLAYER_RESPAWN -> TODO()
            ResetPhase.WAIT_INIT_ENDS -> TODO()
            ResetPhase.END_RESET -> {
                // reset just finished. Should send the observation to the clients
                // do nothing
            }

            ResetPhase.IDLE -> {
                // read actions from clients
                // wait until the clients read inputs
                synchronized(readingActionLock) {
                    readingAction = true
                    readingActionLock.notifyAll()
                }
                for (player in PlayerLookup.all(server)) {
                    ServerPlayNetworking.send(
                        player,
                        ID_READ_ACTION,
                        PacketByteBufs.empty()
                    ) // notify client to send observation, as the server tick is ended
                }
                // wait until the clients send observation
                synchronized(sendingObservationLock) {
                    while (sendingObservation) {
                        sendingObservationLock.wait()
                    }
                }
                // the clients sent actions.
                // ClientPlayerEntitiy sends MovementPackets every tick.
            }
        }
    }

    fun onReadActionFinished() {
        synchronized(readingActionLock) {
            while (readingAction) {
                readingActionLock.wait()
            }

        }
        // readingAction is false now
        // reading action is finished
    }

    fun onWorldTickEnd() {
        when (resetPhase) {
            ResetPhase.WAIT_PLAYER_DEATH -> {
                // do nothing
            }

            ResetPhase.WAIT_PLAYER_RESPAWN -> {
                // do nothing
            }

            ResetPhase.WAIT_INIT_ENDS -> {
                // do nothing
            }

            ResetPhase.END_RESET -> {
                sendObservation() // blocks until the observation is sent
            }

            ResetPhase.IDLE -> {
                sendObservation() // blocks until the observation is sent
            }
        }
    }

    private fun sendObservation() {
        synchronized(sendingObservationLock) {
            sendingObservation = true
            sendingObservationLock.notifyAll()
        }
        // tell the clients to send observation
        // Iterate over all players in the world and send the packet to each player
        for (player in PlayerLookup.all(server)) {
            ServerPlayNetworking.send(
                player,
                ID_END_SERVER_TICK,
                PacketByteBufs.empty()
            ) // notify client to send observation, as the server tick is ended
        }
        // wait until it finishes sending observation
        synchronized(sendingObservationLock) {
            while (sendingObservation) {
                sendingObservationLock.wait()
            }
        }
        // sendingObservation is false now
        // sending observation is finished
    }

    fun getInput() {

    }
}