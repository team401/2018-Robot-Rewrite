package org.team401.rewrite2018

import org.snakeskin.annotation.Setup
import org.snakeskin.auto.AutoManager
import org.snakeskin.debug.DebuggerShell
import org.snakeskin.registry.Subsystems
import org.snakeskin.rt.RealTimeExecutor
import org.team401.rewrite2018.auto.CalibrationAuto
import org.team401.rewrite2018.constants.CompBotMeasurements
import org.team401.rewrite2018.subsystems.Drivetrain

/**
 * @author Cameron Earle
 * @version 7/14/2018
 *
 */

//Practice / Comp selectors
val Measurements = CompBotMeasurements

@Setup
fun setup() {
    DebuggerShell.basePackage = "org.team401.rewrite2018"
    RealTimeExecutor.rate = .01

    AutoManager.setAutoLoop(CalibrationAuto)

    Subsystems.add(Drivetrain)
}