package org.team401.rewrite2018.subsystems

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.FeedbackDevice
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import org.snakeskin.dsl.*
import org.snakeskin.event.Events

/**
 * @author Cameron Earle
 * @version 9/28/2018
 *
 */
object TestSpinner: Subsystem() {
    val talon = TalonSRX(1)

    override fun setup() {
        talon.configSelectedFeedbackSensor(FeedbackDevice.Analog, 0, 0)
        on (Events.ENABLED) {
            machine.setState("testTime")
        }
    }

    val machine: StateMachine<String> = stateMachine {
        state("testTime") {
            action {
                println(talon.getSelectedSensorPosition(0))
                talon.set(ControlMode.Position, (950.0 - 10.0) / 2.0)
            }
        }

        disabled {
            action(1000L) {
                action {
                    println(talon.getSelectedSensorPosition(0))
                }
            }
        }
    }
}