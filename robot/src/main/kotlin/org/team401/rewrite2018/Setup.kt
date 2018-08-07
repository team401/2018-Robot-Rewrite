package org.team401.rewrite2018

import com.google.gson.Gson
import org.snakeskin.annotation.Setup
import org.snakeskin.debug.DebuggerShell
import org.snakeskin.dsl.Subsystems
import org.snakeskin.registry.Controllers
import org.snakeskin.registry.RealTimeTasks
import org.snakeskin.rt.RealTimeExecutor
import org.team401.rewrite2018.constants.CompBotMeasurements
import org.team401.rewrite2018.constants.IMeasurements
import org.team401.rewrite2018.subsystems.Drivetrain

/**
 * @author Cameron Earle
 * @version 7/14/2018
 *
 */

//Practice / Comp selectors
val Measurements: IMeasurements = CompBotMeasurements()

@Setup
fun setup() {
    DebuggerShell.basePackage = "org.team401.rewrite2018"

    RealTimeExecutor.rate = .005

    Controllers.add(LeftStick, RightStick, Gamepad)
    Subsystems.add(Drivetrain)

    //RealTimeTasks.add(RobotStateUpdater(Drivetrain, RobotState, Kinematics))
}