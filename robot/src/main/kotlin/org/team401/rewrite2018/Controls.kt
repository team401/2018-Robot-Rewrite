package org.team401.rewrite2018

import org.snakeskin.dsl.HumanControls
import org.team401.rewrite2018.subsystems.Drivetrain

/**
 * @author Cameron Earle
 * @version 7/21/2018
 *
 */
val LeftStick = HumanControls.t16000m(0) {
    invertAxis(Axes.PITCH)

    whenButton(Buttons.TRIGGER) {
        pressed {
            Drivetrain.shiftMachine.setState(Drivetrain.ShifterStates.LOW)
        }
        released {
            Drivetrain.shiftMachine.setState(Drivetrain.ShifterStates.HIGH)
        }
    }
}

val RightStick = HumanControls.t16000m(1)