package org.team401.autolib

import com.team254.frc2017.Kinematics
import com.team254.frc2017.RobotState
import com.team254.lib.util.math.Rotation2d
import org.snakeskin.component.TankDrivetrain
import org.snakeskin.rt.RealTimeExecutor
import org.snakeskin.rt.RealTimeTask

/**
 * @author Cameron Earle
 * @version 7/14/2018
 *
 */

class RobotStateUpdater(val drivetrain: TankDrivetrain, val robotState: RobotState, val kinematics: Kinematics): RealTimeTask {
    override val name = "RobotStateUpdater"

    private val left = drivetrain.left
    private val right = drivetrain.right
    private val imu = drivetrain.imu
    private val imuData = DoubleArray(3) { 0.0 }

    fun getLeftDistanceInches(): Double {
        return left.getPosition(0).value //TODO add unit conversions to SnakeSkin, fix this unit
    }

    fun getRightDistanceInches(): Double {
        return right.getPosition(0).value //TODO add unit conversions to SnakeSkin, fix this unit
    }

    fun getLeftVelocityInchesPerSec(): Double {
        return left.getVelocity(0).value //TODO add unit conversions to SnakeSkin, fix this unit
    }

    fun getRightVelocityInchesPerSec(): Double {
        return right.getVelocity(0).value //TODO add unit conversions to SnakeSkin, fix this unit
    }

    fun getRotationFromImu(): Rotation2d {
        imu.getYawPitchRoll(imuData)
        return Rotation2d.fromDegrees(imuData[0])
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