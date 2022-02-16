package com.gadarts.helicopter.core.systems

import com.gadarts.helicopter.core.systems.player.PlayerSystemEventsSubscriber
import kotlin.reflect.KClass

enum class Systems(
    val implementation: KClass<out GameEntitySystem<out SystemEventsSubscriber>>,
    val subscriberClass: KClass<out SystemEventsSubscriber>
) {
    CAMERA(CameraSystem::class, CameraSystemEventsSubscriber::class),
    CHARACTER(CameraSystem::class, CharacterSystemEventsSubscriber::class),
    HUD(CameraSystem::class, HudSystemEventsSubscriber::class),
    INPUT(CameraSystem::class, InputSystemEventsSubscriber::class),
    PROFILING(CameraSystem::class, ProfilingSystemEventsSubscriber::class),
    PLAYER(CameraSystem::class, PlayerSystemEventsSubscriber::class),
    RENDER(CameraSystem::class, RenderSystemEventsSubscriber::class)
}