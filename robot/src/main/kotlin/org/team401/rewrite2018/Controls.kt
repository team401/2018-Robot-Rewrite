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
            Drivetrain.shift(Drivetrain.ShifterStates.HIGH)
        }
        released {
            Drivetrain.shift(Drivetrain.ShifterStates.LOW)
        }
    }
}

val RightStick = HumanControls.t16000m(1)

val Gamepad = HumanControls.f310(2) {

}