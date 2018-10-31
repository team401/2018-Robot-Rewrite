package org.team401.rewrite2018.subsystems

import com.ctre.phoenix.motorcontrol.*
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import edu.wpi.first.wpilibj.Solenoid
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
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
import org.snakeskin.units.FeetPerSecond
import org.snakeskin.units.MagEncoderTicksPer100Ms
import org.snakeskin.units.measure.distance.angular.AngularDistanceMeasureCTREMagEncoder
import org.snakeskin.units.measure.velocity.angular.AngularVelocityMeasureRadiansPerSecond
import org.team401.rewrite2018.LeftStick
import org.team401.rewrite2018.Measurements
import org.team401.rewrite2018.RightStick
import org.team401.rewrite2018.constants.Constants
import org.team401.taxis.diffdrive.Path
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
        NonlinearFeedbackPathController(5.0, .7)
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

        (left.slaves[2] as TalonSRX).setStatusFramePeriod(StatusFrameEnhanced.Status_11_UartGadgeteer, 5, 1000)

        left.setSensor(FeedbackDevice.CTRE_MagEncoder_Relative)
        right.setSensor(FeedbackDevice.CTRE_MagEncoder_Relative)
        left.setPosition(AngularDistanceMeasureCTREMagEncoder(0.0))
        right.setPosition(AngularDistanceMeasureCTREMagEncoder(0.0))

        on (Events.TELEOP_ENABLED) {
            driveMachine.setState(DriveStates.PATH_FOLLOWING)
        }

        on (Events.AUTO_ENABLED) {
            driveMachine.setState(DriveStates.EXTERNAL_CONTROL)
        }

        pathManager.addPath("test", Path(
                false,
                arrayListOf(
                        Pose2d(
                                Translation2d(0.0, 0.0),
                                Rotation2d.fromDegrees(0.0)
                        ),
                        Pose2d(
                                Translation2d(120.0, -64.0),
                                Rotation2d.fromDegrees(0.0)
                        )
                ),
                4.0 * 12.0,
                4.0 * 12.0,
                9.0
        ))
    }

    val driveMachine: StateMachine<DriveStates> = stateMachine {
        state(DriveStates.EXTERNAL_CONTROL) {}
        
        state(DriveStates.PATH_FOLLOWING) {
            val kP = .3
            val kF = 0.0//0.235877
            val kD = 16.0

            entry {
                left.master.config_kP(0, kP, 0)
                left.master.config_kI(0, 0.0, 0)
                left.master.config_kD(0, kD, 0)
                left.master.config_kF(0, kF, 0)
                left.master.config_IntegralZone(0, 0, 0)
                right.master.config_kP(0, kP, 0)
                right.master.config_kI(0, 0.0, 0)
                right.master.config_kD(0, kD, 0)
                right.master.config_kF(0, kF, 0)
                right.master.config_IntegralZone(0, 0, 0)


                left.master.selectProfileSlot(0, 0);
                right.master.selectProfileSlot(0, 0);
                left.master.configNeutralDeadband(0.0, 0);
                right.master.configNeutralDeadband(0.0, 0)

                setPose(Pose2d(Translation2d(0.0, 0.0), Rotation2d.fromDegrees(0.0)))
                pathManager.reset()
                pathManager.setPath("test")
            }

            rtAction {
                val output = pathManager.update(time, driveState.getFieldToVehicle(time)) //We'll make this useful later

                val leftVelocity = AngularVelocityMeasureRadiansPerSecond(output.leftVelocity).toUnit(MagEncoderTicksPer100Ms).value
                val rightVelocity = AngularVelocityMeasureRadiansPerSecond(output.rightVelocity).toUnit(MagEncoderTicksPer100Ms).value
                val leftFf = output.leftFeedforwardVoltage / 12.0
                val rightFf = output.rightFeedforwardVoltage / 12.0
                val leftAccel = AngularVelocityMeasureRadiansPerSecond(output.leftAccel).toUnit(MagEncoderTicksPer100Ms).value / 1000.0
                val rightAccel = AngularVelocityMeasureRadiansPerSecond(output.rightAccel).toUnit(MagEncoderTicksPer100Ms).value / 1000.0


                left.master.set(ControlMode.Velocity, leftVelocity, DemandType.ArbitraryFeedForward,
                        leftFf + kD * leftAccel / 1023.0)

                right.master.set(ControlMode.Velocity, rightVelocity, DemandType.ArbitraryFeedForward,
                        rightFf + kD * rightAccel / 1023.0)



                //tank(ControlMode.Velocity, leftVelocity, rightVelocity)
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
                    outputScalar = 14.FeetPerSecond.toAngularVelocity(wheelRadius).toUnit(MagEncoderTicksPer100Ms).value,
                    quickTurnScalar = ScalarGroup(SquareScalar, object : Scalar {
                        override fun scale(input: Double) = input / 3.33
                    })
            )

            val scalar = 14.FeetPerSecond.toAngularVelocity(wheelRadius).toUnit(MagEncoderTicksPer100Ms).value

            entry {
                left.master.configNeutralDeadband(0.04, 0)
                right.master.configNeutralDeadband(0.04, 0)
                cheesyParameters.reset()
                setRampRate(
                        Constants.Drivetrain.CLOSED_LOOP_RAMP_RATE,
                        Constants.Drivetrain.OPEN_LOOP_RAMP_RATE
                )
            }

            action {
                /*
                cheesy(
                        ControlMode.Velocity,
                        cheesyParameters,
                        LeftStick.readAxis { PITCH },
                        RightStick.readAxis { ROLL },
                        shifter.get() == ShifterStates.HIGH,
                        RightStick.readButton { TRIGGER }
                )
                */
                val leftCommand = (LeftStick.readAxis { PITCH } + RightStick.readAxis { ROLL }) * scalar
                val rightCommand = (LeftStick.readAxis { PITCH } - RightStick.readAxis { ROLL }) * scalar
                tank(ControlMode.Velocity, leftCommand, rightCommand)

                SmartDashboard.putNumber("driveVelMax", Math.max(left.getVelocity().value, SmartDashboard.getNumber("driveVelMax", 0.0)))
                SmartDashboard.putNumber("driveVelSetpoint", leftCommand)
                SmartDashboard.putNumber("driveVelActual", left.getVelocity().value)

                println(driveState.getLatestFieldToVehicle().value)
            }
        }
    }
}