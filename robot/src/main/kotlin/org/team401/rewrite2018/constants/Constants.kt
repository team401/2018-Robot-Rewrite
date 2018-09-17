package org.team401.rewrite2018.constants

import org.snakeskin.units.measure.distance.angular.AngularDistanceMeasureCTREMagEncoder
import org.snakeskin.units.measure.distance.linear.LinearDistanceMeasureInches
import org.snakeskin.units.measure.time.TimeMeasureSeconds
import org.snakeskin.units.measure.velocity.angular.AngularVelocityMeasureCTREMagEncoder

/**
 * @author Cameron Earle
 * @version 7/21/2018
 *
 */
object Constants {
    object Drivetrain {
        //Address mappings
        const val DRIVE_LEFT_REAR_CAN = 0
        const val DRIVE_LEFT_MIDR_CAN = 1
        const val DRIVE_LEFT_MIDF_CAN = 2
        const val DRIVE_LEFT_FRONT_CAN = 3
        const val DRIVE_RIGHT_FRONT_CAN = 11
        const val DRIVE_RIGHT_MIDF_CAN = 12
        const val DRIVE_RIGHT_MIDR_CAN = 13
        const val DRIVE_RIGHT_REAR_CAN = 14

        const val SHIFTER_SOLENOID = 2

        //Parameters
        const val INVERT_LEFT = true
        const val INVERT_RIGHT = false

        const val CONTINUOUS_CURRENT_LIMIT = 0 //30
        const val PEAK_CURRENT_LIMIT = 0 //40
        const val PEAK_CURRENT_LIMIT_DURATION = 0 //100

        const val CLOSED_LOOP_RAMP_RATE = 0.0
        const val OPEN_LOOP_RAMP_RATE = 0.0 //0.25

        const val DRIVETRAIN_SHIFTER_EXTENDED_FOR_LOW_GEAR = true
        const val DRIVETRAIN_SOLENOID_ON_FOR_SHIFTER_EXTENDED = true
    }
}