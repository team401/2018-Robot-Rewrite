package org.team401.rewrite2018.constants

import org.snakeskin.units.measure.distance.linear.LinearDistanceMeasure
import org.snakeskin.units.measure.distance.linear.LinearDistanceMeasureInches

/**
 * @author Cameron Earle
 * @version 7/21/2018
 *
 */
class CompBotMeasurements: IMeasurements {
    override val WHEEL_RADIUS = LinearDistanceMeasureInches(3.0)
    override val WHEELBASE = LinearDistanceMeasureInches(0.0)
}