package org.team401.rewrite2018.auto

import org.snakeskin.auto.RobotAuto
import org.snakeskin.auto.steps.SequentialSteps
import org.team401.rewrite2018.subsystems.Drivetrain

/**
 * @author Cameron Earle
 * @version 10/7/2018
 *
 */
object CalibrationAuto: RobotAuto(10L) {
    override fun assembleAuto(): SequentialSteps {
        return SequentialSteps(MeasureStiction(Drivetrain, .2, .02))
    }
}