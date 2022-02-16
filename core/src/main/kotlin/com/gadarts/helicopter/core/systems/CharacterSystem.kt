package com.gadarts.helicopter.core.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.gadarts.helicopter.core.SoundPlayer
import com.gadarts.helicopter.core.assets.GameAssetManager
import com.gadarts.helicopter.core.components.AmbSoundComponent
import com.gadarts.helicopter.core.components.ComponentsMapper

class CharacterSystem(private val data: SystemsData, private val soundPlayer: SoundPlayer) :
    GameEntitySystem<Any?>() {
    private lateinit var ambSoundEntities: ImmutableArray<Entity>

    override fun initialize(assetsManager: GameAssetManager) {

    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        update3dSound()
    }

    private fun update3dSound() {
        for (entity in ambSoundEntities) {
            updateEntity3dSound(entity)
        }
    }

    private fun updateEntity3dSound(entity: Entity?) {
        val transform = ComponentsMapper.modelInstance.get(entity).modelInstance.transform
        val position = transform.getTranslation(auxVector)
        var distance = MathUtils.clamp(data.camera.position.dst2(position), 15F, 64F)
        distance = 1 - MathUtils.norm(15F, 64F, distance)
        val ambSoundComponent = ComponentsMapper.ambSound.get(entity)
        ambSoundComponent.sound.setVolume(ambSoundComponent.soundId, distance)
        if (distance <= 0F) {
            stopSoundOfEntity(ambSoundComponent)
        }
    }

    private fun stopSoundOfEntity(ambSoundComponent: AmbSoundComponent) {
        ambSoundComponent.sound.stop(ambSoundComponent.soundId)
        ambSoundComponent.soundId = -1
    }

    override fun dispose() {

    }

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        ambSoundEntities = engine!!.getEntitiesFor(Family.all(AmbSoundComponent::class.java).get())
        engine.addEntityListener(object : EntityListener {
            override fun entityAdded(entity: Entity?) {
                if (ComponentsMapper.ambSound.has(entity)) {
                    val ambSoundComponent = ComponentsMapper.ambSound.get(entity)
                    if (ambSoundComponent.soundId == -1L) {
                        val id = soundPlayer.play(ambSoundComponent.sound, loop = true)
                        ambSoundComponent.soundId = id
                    }
                }
            }

            override fun entityRemoved(entity: Entity?) {
            }

        })
    }

    companion object {
        val auxVector = Vector3()
    }
}
