package org.team401.rewrite2018.subsystems

import com.ctre.phoenix.motorcontrol.*
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import edu.wpi.first.wpilibj.Solenoid
import org.snakeskin.component.Gearbox
import org.snakeskin.component.TalonPigeonIMU
import org.snakeskin.component.TankDrivetrain
import org.snakeskin.component.impl.SmartTankDrivetrain
import org.snakeskin.dsl.*
import org.snakeskin.event.Events
import org.snakeskin.logic.scalars.Scalar
import org.snakeskin.logic.scalars.ScalarGroup
import org.snakeskin.logic.scalars.SquareScalar
import org.snakeskin.state.StateMachine
import org.snakeskin.units.measure.distance.angular.AngularDistanceMeasureCTREMagEncoder
import org.team401.rewrite2018.LeftStick
import org.team401.rewrite2018.Measurements
import org.team401.rewrite2018.RightStick
import org.team401.rewrite2018.constants.Constants
import org.team401.taxis.diffdrive.component.PathFollowingDiffDrive
import org.team401.taxis.diffdrive.component.impl.SmartPathFollowingDiffDrive
import org.team401.taxis.diffdrive.control.DrivetrainPathManager
import org.team401.taxis.diffdrive.control.NoOpPathController
import org.team401.taxis.diffdrive.control.NonlinearFeedbackPathController
import org.team401.taxis.geometry.Pose2d
import org.team401.taxis.geometry.Rotation2d
import org.team401.taxis.geometry.Translation2d

/**
 * @author Cameron Earle
 * @version 7/21/2018
 *
 */
object Drivetrain: Subsystem(), PathFollowingDiffDrive by SmartPathFollowingDiffDrive(
        Measurements.driveGeometry,
        Measurements.DriveDynamics,
        Measurements.DrivePathFollowing,
        Gearbox(
                TalonSRX(Constants.Drivetrain.DRIVE_LEFT_FRONT_CAN),
                TalonSRX(Constants.Drivetrain.DRIVE_LEFT_MIDF_CAN),
                TalonSRX(Constants.Drivetrain.DRIVE_LEFT_MIDR_CAN),
                TalonSRX(Constants.Drivetrain.DRIVE_LEFT_REAR_CAN)
        ),
        Gearbox(
                TalonSRX(Constants.Drivetrain.DRIVE_RIGHT_FRONT_CAN),
                TalonSRX(Constants.Drivetrain.DRIVE_RIGHT_MIDF_CAN),
                TalonSRX(Constants.Drivetrain.DRIVE_RIGHT_MIDR_CAN),
                TalonSRX(Constants.Drivetrain.DRIVE_RIGHT_REAR_CAN)
        ),
        TalonPigeonIMU(Constants.Drivetrain.DRIVE_LEFT_REAR_CAN),
        NoOpPathController()
) {
    //State enums
    enum class DriveStates {
        EXTERNAL_CONTROL,
        PATH_FOLLOWING,
        OPEN_LOOP
    }

    val ShifterStates = ShifterState(
            Constants.Drivetrain.DRIVETRAIN_SOLENOID_ON_FOR_SHIFTER_EXTENDED,
            Constants.Drivetrain.DRIVETRAIN_SHIFTER_EXTENDED_FOR_LOW_GEAR
    )

    //Hardware
    private val shifter = Solenoid(Constants.Drivetrain.SHIFTER_SOLENOID)

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
        
        state(DriveStates.PATH_FOLLOWING) {
            entry {
                //TODO what do I put here
            }

            rtAction {
                println(pathManager.update(time, Pose2d.identity())) //We'll make this useful later
                if (pathManager.isDone()) {
                    //Drop into open loop i guess?
                    setState(DriveStates.OPEN_LOOP)
                }
            }
        }

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
                setRampRate(
                        Constants.Drivetrain.CLOSED_LOOP_RAMP_RATE,
                        Constants.Drivetrain.OPEN_LOOP_RAMP_RATE
                )
                setPose(Pose2d(
                        Translation2d.identity(),
                        Rotation2d.identity()
                ))
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
                println(driveState.getLatestFieldToVehicle().value)
            }
        }
    }
}