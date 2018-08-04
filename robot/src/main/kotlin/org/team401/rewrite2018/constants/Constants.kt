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

        const val CONTINUOUS_CURRENT_LIMIT = 30
        const val PEAK_CURRENT_LIMIT = 40
        const val PEAK_CURRENT_LIMIT_DURATION = 100

        const val CLOSED_LOOP_RAMP_RATE = 0.0
        const val OPEN_LOOP_RAMP_RATE = 0.25

        const val DRIVETRAIN_SHIFTER_EXTENDED_FOR_LOW_GEAR = true
        const val DRIVETRAIN_SOLENOID_ON_FOR_SHIFTER_EXTENDED = true
    }

    object Elevator {
        const val ELEVATOR_MASTER_CAN = 7
        const val ELEVATOR_SLAVE_1_CAN = 10
        const val ELEVATOR_SLAVE_2_CAN = 9
        const val ELEVATOR_SLAVE_3_CAN = 8

        const val ELEVATOR_SHIFTER_SOLENOID = 3
        const val ELEVATOR_DEPLOY_SOLENOID = 4
        const val ELEVATOR_KICKER_SOLENOID = 6
        const val ELEVATOR_CLAMP_SOLENOID = 5
        const val ELEVATOR_RATCHET_SOLENOID = 7

        const val ELEVATOR_CURRENT_LIMIT_CONTINUOUS = 30

        val ELEVATOR_DEPLOY_TIMEOUT = TimeMeasureSeconds(3.0)

        const val ELEVATOR_SHIFTER_EXTENDED_FOR_LOW_GEAR = true
        const val ELEVATOR_SOLENOID_ON_FOR_SHIFTER_EXTENDED = false

        const val ELEVATOR_SOLENOID_ON_FOR_CLAMP_EXTENDED = false

        const val ELEVATOR_SOLENOID_ON_FOR_RATCHET_EXTENDED = true

        const val ELEVATOR_SOLENOID_ON_FOR_KICKER_EXTENDED = true

        //Linear distances are as measured from bottom most surface of elevator carriage to floor
        //Angular distances are absolute encoder positions
        val ELEVATOR_PITCH_RADIUS = LinearDistanceMeasureInches(1.805 / 2.0)
        val ZERO_POS = LinearDistanceMeasureInches(0.0)
        val COLLECTION_POS = AngularDistanceMeasureCTREMagEncoder(500.0)
        val DRIVING_POS = LinearDistanceMeasureInches(6.0)
        val SWITCH_POS = LinearDistanceMeasureInches(32.0)
        val SCALE_LOW_POS = AngularDistanceMeasureCTREMagEncoder(37000.0)
        val SCALE_POS = AngularDistanceMeasureCTREMagEncoder(48880.0)
        val SCALE_HIGH_POS = AngularDistanceMeasureCTREMagEncoder(60000.0)
        val CLIMB_PREP_POS = AngularDistanceMeasureCTREMagEncoder(40000.0)
        val CLIMB_BOTTOM_POS = LinearDistanceMeasureInches(36.0)
        val HIGH_CLIMB_BOTTOM_POS = LinearDistanceMeasureInches(9.0)
        val MAX_POS = AngularDistanceMeasureCTREMagEncoder(60000.0)

        val ELEVATOR_RUN_CRUISE_VELOCITY = AngularVelocityMeasureCTREMagEncoder(21680.0)
        val ELEVATOR_CLIMB_CRUISE_VELOCITY = AngularVelocityMeasureCTREMagEncoder(217.0 * 14.0)
        val ELEVATOR_ACCEL = AngularVelocityMeasureCTREMagEncoder(21680.0)
    }

    object Intake {
        const val INTAKE_LEFT_CAN = 5
        const val INTAKE_RIGHT_CAN = 6
        const val INTAKE_FOLDING_CAN = 4
    }
}