package com.gadarts.helicopter.core.systems

interface HudSystemEventsSubscriber : SystemEventsSubscriber {
    fun onPrimaryWeaponButtonPressed()
    fun onPrimaryWeaponButtonReleased()
    fun onSecondaryWeaponButtonPressed()
    fun onSecondaryWeaponButtonReleased()

}
