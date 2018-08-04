package org.team401.rewrite2018.subsystems

import com.ctre.phoenix.motorcontrol.*
import org.snakeskin.component.Gearbox
import org.snakeskin.component.TankDrivetrain
import org.snakeskin.component.impl.SmartTankDrivetrain
import org.snakeskin.dsl.*
import org.snakeskin.event.Events
import org.snakeskin.hardware.CTREHardware
import org.snakeskin.hardware.FRCHardware
import org.snakeskin.logic.scalars.Scalar
import org.snakeskin.logic.scalars.ScalarGroup
import org.snakeskin.logic.scalars.SquareScalar
import org.snakeskin.state.StateMachine
import org.snakeskin.units.measure.distance.angular.AngularDistanceMeasureCTREMagEncoder
import org.team401.rewrite2018.LeftStick
import org.team401.rewrite2018.Measurements
import org.team401.rewrite2018.RightStick
import org.team401.rewrite2018.constants.Constants

/**
 * @author Cameron Earle
 * @version 7/21/2018
 *
 */
object Drivetrain: Subsystem("Drivetrain"), TankDrivetrain by SmartTankDrivetrain(
        Measurements.WHEEL_RADIUS,
        Measurements.WHEELBASE,
        Gearbox(
                CTREHardware.TalonSRX(Constants.Drivetrain.DRIVE_LEFT_FRONT_CAN),
                CTREHardware.TalonSRX(Constants.Drivetrain.DRIVE_LEFT_MIDF_CAN),
                CTREHardware.TalonSRX(Constants.Drivetrain.DRIVE_LEFT_MIDR_CAN),
                CTREHardware.TalonSRX(Constants.Drivetrain.DRIVE_LEFT_REAR_CAN)
        ),
        Gearbox(
                CTREHardware.TalonSRX(Constants.Drivetrain.DRIVE_RIGHT_FRONT_CAN),
                CTREHardware.TalonSRX(Constants.Drivetrain.DRIVE_RIGHT_MIDF_CAN),
                CTREHardware.TalonSRX(Constants.Drivetrain.DRIVE_RIGHT_MIDR_CAN),
                CTREHardware.TalonSRX(Constants.Drivetrain.DRIVE_RIGHT_REAR_CAN)
        ),
        CTREHardware.TalonPigeonIMU(Constants.Drivetrain.DRIVE_LEFT_REAR_CAN)
) {
    //State enums
    enum class DriveStates {
        EXTERNAL_CONTROL,
        OPEN_LOOP
    }

    val ShifterStates = ShifterState(
            Constants.Drivetrain.DRIVETRAIN_SOLENOID_ON_FOR_SHIFTER_EXTENDED,
            Constants.Drivetrain.DRIVETRAIN_SHIFTER_EXTENDED_FOR_LOW_GEAR
    )

    //Hardware
    private val shifter = FRCHardware.Solenoid(Constants.Drivetrain.SHIFTER_SOLENOID)

    fun shift(value: Boolean) {
        shifter.set(value)
    }

    override fun setup() {
        left.setInverted(Constants.Drivetrain.INVERT_LEFT)
        right.setInverted(Constants.Drivetrain.INVERT_RIGHT)

        setCurrentLimit(
                Constants.Drivetrain.CONTINUOUS_CURRENT_LIMIT,
                Constants.Drivetrain.PEAK_CURRENT_LIMIT,
                Constants.Drivetrain.PEAK_CURRENT_LIMIT_DURATION
        )

        setRampRate(
                Constants.Drivetrain.CLOSED_LOOP_RAMP_RATE,
                Constants.Drivetrain.OPEN_LOOP_RAMP_RATE
        )

        setNeutralMode(NeutralMode.Coast)

        left.setSensor(FeedbackDevice.CTRE_MagEncoder_Relative)
        right.setSensor(FeedbackDevice.CTRE_MagEncoder_Relative)
        left.setPosition(AngularDistanceMeasureCTREMagEncoder(0.0))
        right.setPosition(AngularDistanceMeasureCTREMagEncoder(0.0))

        on (Events.TELEOP_ENABLED) {
            driveMachine.setState(DriveStates.OPEN_LOOP)
        }

        on (Events.AUTO_ENABLED) {
            driveMachine.setState(DriveStates.EXTERNAL_CONTROL)
        }
    }

    val driveMachine: StateMachine<DriveStates> = stateMachine {
        state(DriveStates.EXTERNAL_CONTROL) {}

        state(DriveStates.OPEN_LOOP) {
            val cheesyParameters = CheesyDriveParameters(
                    0.65,
                    0.5,
                    4.0,
                    0.65,
                    3.5,
                    4.0,
                    5.0,
                    0.95,
                    1.3,
                    0.2,
                    0.1,
                    5.0,
                    3,
                    2,
                    quickTurnScalar = ScalarGroup(SquareScalar, object : Scalar {
                        override fun scale(input: Double) = input / 3.33
                    })
            )

            entry {
                cheesyParameters.reset()
            }

            action {
                cheesy(
                        ControlMode.PercentOutput,
                        cheesyParameters,
                        LeftStick.readAxis { PITCH },
                        RightStick.readAxis { ROLL },
                        shifter.get() == ShifterStates.HIGH,
                        RightStick.readButton { TRIGGER }
                )
            }
        }
    }
}