package org.team401.autolib

import com.team254.frc2017.Kinematics
import com.team254.frc2017.RobotState
import com.team254.lib.util.math.Rotation2d
import org.snakeskin.component.TankDrivetrain
import org.snakeskin.rt.RealTimeExecutor
import org.snakeskin.rt.RealTimeTask
import org.snakeskin.units.AngularDistanceUnit
import org.snakeskin.units.LinearDistanceUnit
import org.snakeskin.units.LinearVelocityUnit

/**
 * @author Cameron Earle
 * @version 7/14/2018
 *
 */

class RobotStateUpdater(val drivetrain: TankDrivetrain<*, *>, val robotState: RobotState, val kinematics: Kinematics): RealTimeTask {
    override val name = "RobotStateUpdater"

    private val left = drivetrain.left
    private val right = drivetrain.right

    fun getLeftDistanceInches(): Double {
        return left.getPosition().toLinearDistance(drivetrain.wheelRadius).toUnit(LinearDistanceUnit.Standard.INCHES).value
    }

    fun getRightDistanceInches(): Double {
        return right.getPosition().toLinearDistance(drivetrain.wheelRadius).toUnit(LinearDistanceUnit.Standard.INCHES).value
    }

    fun getLeftVelocityInchesPerSec(): Double {
        return left.getVelocity().toLinearVelocity(drivetrain.wheelRadius).toUnit(LinearVelocityUnit.Standard.INCHES_PER_SECOND).value
    }

    fun getRightVelocityInchesPerSec(): Double {
        return right.getVelocity().toLinearVelocity(drivetrain.wheelRadius).toUnit(LinearVelocityUnit.Standard.INCHES_PER_SECOND).value
    }

    fun getRotationFromImu(): Rotation2d {
        return Rotation2d.fromDegrees(drivetrain.getYaw().toUnit(AngularDistanceUnit.Standard.DEGREES).value)
    }

    private var leftDistanceLast = getLeftDistanceInches()
    private var rightDistanceLast = getRightDistanceInches()

    override fun action(ctx: RealTimeExecutor.RealTimeContext) {
        val leftDistance = getLeftDistanceInches()
        val rightDistance = getRightDistanceInches()
        val gyroAngle = getRotationFromImu()

        val odometryVelocity = robotState.generateOdometryFromSensors(
                leftDistance - leftDistanceLast,
                rightDistance - rightDistanceLast,
                gyroAngle
        )
        val predictedVelocity = kinematics.forwardKinematics(
                getLeftVelocityInchesPerSec(),
                getRightVelocityInchesPerSec()
        )
        robotState.addObservations(ctx.time, odometryVelocity, predictedVelocity)
        leftDistanceLast = leftDistance
        rightDistanceLast = rightDistance
    }
}