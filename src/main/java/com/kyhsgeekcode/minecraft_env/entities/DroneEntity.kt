package com.kyhsgeekcode.minecraft_env.entities

import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class DroneEntity(
    entityType: EntityType<DroneEntity>,
    world: World
) : Entity(entityType, world) {
    private var pressingLeft = false
    private var pressingRight = false
    private var pressingForward = false
    private var pressingBack = false
    override fun initDataTracker() {
        // do nothing
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound?) {

    }

    override fun writeCustomDataToNbt(nbt: NbtCompound?) {

    }

    override fun tick() {
        super.tick()
        if (isLogicalSideForUpdatingMovement) {
            if (firstPassenger !is PlayerEntity) {
                updateMovements()
            }
            if (world.isClient) {
                updateMovements()
//                world.sendPacket()
            }
        } else {
            velocity = Vec3d.ZERO
        }

        checkBlockCollision()
    }

    override fun updatePassengerPosition(passenger: Entity?) {
        if (!hasPassenger(passenger)) return
        passenger?.apply {
            setPosition(this.pos)
            yaw = this.yaw
            pitch = this.pitch
            headYaw = this.headYaw
            bodyYaw = this.bodyYaw
        }
        super.updatePassengerPosition(passenger)
    }

    override fun interact(player: PlayerEntity?, hand: Hand?): ActionResult {
        if (player == null)
            return super.interact(player, hand)
        if (player.shouldCancelInteraction()) {
            return ActionResult.PASS
        }
        if (!world.isClient) {
            return if (player.startRiding(this)) {
                ActionResult.CONSUME
            } else {
                ActionResult.PASS
            }

        }
        return ActionResult.SUCCESS
    }

    fun setInputs(pressingLeft: Boolean, pressingRight: Boolean, pressingForward: Boolean, pressingBack: Boolean) {
        this.pressingLeft = pressingLeft
        this.pressingRight = pressingRight
        this.pressingForward = pressingForward
        this.pressingBack = pressingBack
    }

    private fun updateMovements() {
        if (pressingLeft) {

        }
        if (pressingRight) {
            yaw += 2.5f
        }
        if (pressingForward) {
//            updateVelocity(0.5f)
        }
        if (pressingBack) {
//            updateVelocity(-0.5f)
        }
    }

}