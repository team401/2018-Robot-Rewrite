package org.team401.rewrite2018

import com.ctre.phoenix.motorcontrol.ControlMode
import org.snakeskin.annotation.Setup
import org.snakeskin.auto.AutoManager
import org.snakeskin.auto.RobotAuto
import org.snakeskin.auto.steps.AutoStep
import org.snakeskin.auto.steps.SequentialSteps
import org.snakeskin.component.TankDrivetrain
import org.snakeskin.debug.DebuggerShell
import org.snakeskin.dsl.Subsystems
import org.snakeskin.registry.Controllers
import org.snakeskin.rt.RealTimeExecutor
import org.snakeskin.units.*
import org.snakeskin.units.measure.distance.angular.AngularDistanceMeasureCTREMagEncoder
import org.snakeskin.units.measure.distance.angular.AngularDistanceMeasureDegrees
import org.snakeskin.units.measure.distance.angular.AngularDistanceMeasureRevolutions
import org.team401.rewrite2018.constants.CompBotMeasurements
import org.team401.rewrite2018.constants.IMeasurements
import org.team401.rewrite2018.subsystems.Drivetrain
import org.team401.taxis.diffdrive.autotune.AutotunePhysics
import org.team401.taxis.diffdrive.autotune.autos.TuningAutoCharacterizeAngularDynamics
import org.team401.taxis.diffdrive.autotune.autos.TuningAutoCharacterizeLinearDynamics
import org.team401.taxis.diffdrive.autotune.autos.TuningAutoTuneWheelRadius
import org.team401.taxis.diffdrive.odometry.OdometryTracker

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
    RealTimeExecutor.rate = .005

    Controllers.add(LeftStick, RightStick)
    Subsystems.add(Drivetrain)

    val driveModel = AutotunePhysics.IdealDiffDriveModel(
            AutotunePhysics.IdealTransmissionModel(
                    24.79,
                    4,
                    AutotunePhysics.FRCMotors.MOTOR_775_PRO,
                    Drivetrain.wheelRadius.toUnit(Meters).value
            ),
            AutotunePhysics.IdealTransmissionModel(
                    24.79,
                    4,
                    AutotunePhysics.FRCMotors.MOTOR_775_PRO,
                    Drivetrain.wheelRadius.toUnit(LinearDistanceUnit.Standard.METERS).value
            ),
            Drivetrain.wheelbase.toUnit(Meters).value / 2.0
    )

    //AutoManager.setAutoLoop(TuningAutoTuneWheelRadius(Drivetrain, 60.Inches))

    AutoManager.setAutoLoop(TuningAutoCharacterizeLinearDynamics(
            Drivetrain,
            driveModel,
            .2,
            .05,
            .5,
            2.Seconds))

    /*
    AutoManager.setAutoLoop(TuningAutoCharacterizeAngularDynamics(
            Drivetrain,
            driveModel,
            .5,
            1.0,
            5.Seconds
    ))
    */

    RealTimeExecutor.addTask(OdometryTracker(Drivetrain))
}