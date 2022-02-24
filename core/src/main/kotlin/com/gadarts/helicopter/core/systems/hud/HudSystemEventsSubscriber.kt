package com.gadarts.helicopter.core.systems.hud

import com.gadarts.helicopter.core.systems.SystemEventsSubscriber

interface HudSystemEventsSubscriber : SystemEventsSubscriber {
    fun onPrimaryWeaponButtonPressed()
    fun onPrimaryWeaponButtonReleased()
    fun onSecondaryWeaponButtonPressed()
    fun onSecondaryWeaponButtonReleased()

}