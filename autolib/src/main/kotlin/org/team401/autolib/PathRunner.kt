package org.team401.autolib

import com.ctre.phoenix.motorcontrol.ControlMode
import com.team254.frc2017.Kinematics
import com.team254.frc2017.RobotState
import com.team254.lib.util.control.Path
import com.team254.lib.util.control.PathFollower
import org.snakeskin.component.TankDrivetrain

/**
 * @author Cameron Earle
 * @version 7/14/2018
 *
 * Intermediate class for executing a path on a drivetrain.  Manages all state related parameters for path following,
 * which will be wrapped by an AutoStep for easy use in autonomous
 */

class PathRunner(val drivetrain: TankDrivetrain, val robotState: RobotState, val kinematics: Kinematics, val parameters: PathFollower.Parameters, val maxVelocitySetpoint: Double) {
    val left = drivetrain.left.master
    val right = drivetrain.right.master

    var currentPath: Path? = null
    var pathFollower: PathFollower? = null

    fun runPath(path: Path, reversed: Boolean) {
        if (currentPath != path) {
            robotState.resetDistanceDriven()
            pathFollower = PathFollower(path, reversed, parameters)
            currentPath = path
        }
    }

    fun hasPassedMarker(marker: String): Boolean {
        return pathFollower?.hasPassedMarker(marker) ?: false
    }

    fun isDoneWithPath(): Boolean {
        return pathFollower?.isFinished ?: false
    }

    fun forceDoneWithPath() {
        pathFollower?.forceFinish()
    }

    private fun updateVelocitySetpoint(leftInchesPerSec: Double, rightInchesPerSec: Double) {
        val maxDesired = Math.max(Math.abs(leftInchesPerSec), Math.abs(rightInchesPerSec))
        val scale = if (maxDesired > maxVelocitySetpoint) {
            maxVelocitySetpoint / maxDesired
        } else {
            1.0
        }
        left.set(ControlMode.Velocity, leftInchesPerSec * scale)
        right.set(ControlMode.Velocity, rightInchesPerSec * scale)
    }

    fun update(time: Double) {
        if (pathFollower != null) {
            val robotPose = robotState.latestFieldToVehicle.value
            val command = pathFollower!!.update(
                    time,
                    robotPose,
                    robotState.distanceDriven,
                    robotState.predictedVelocity.dx
            )
            if (!pathFollower!!.isFinished) {
                val setpoint = kinematics.inverseKinematics(command)
                updateVelocitySetpoint(setpoint.left, setpoint.right)
            } else {
                updateVelocitySetpoint(0.0, 0.0)
            }
        }
    }
}