package org.team401.rewrite2018.constants

import org.snakeskin.template.TankDrivetrainGeometryTemplate
import org.snakeskin.units.Inches
import org.snakeskin.units.measure.distance.linear.LinearDistanceMeasure
import org.snakeskin.units.measure.distance.linear.LinearDistanceMeasureInches
import org.team401.taxis.template.DriveDynamicsTemplate
import org.team401.taxis.template.PathFollowingTemplate

/**
 * @author Cameron Earle
 * @version 7/21/2018
 *
 */
object CompBotMeasurements: IMeasurements {
    val DriveDynamics = object : DriveDynamicsTemplate {
        override val angularDrag = 0.0
        override val inertialMass = 0.0
        
        override val kA = 0.0 
        override val kS = 0.0
        override val kV = 0.0
        override val momentOfInertia = 0.0
        override val trackScrubFactor = 1.0779845191991568
    }

    val DrivePathFollowing = object : PathFollowingTemplate {
        override val maxErrorTheta = 0.0
        override val maxErrorX = 0.0
        override val maxErrorY = 0.0
    }


    override val driveGeometry = object : TankDrivetrainGeometryTemplate {
        override val wheelRadius = 2.931308788118874.Inches
        override val wheelbase = 24.Inches
    }
}