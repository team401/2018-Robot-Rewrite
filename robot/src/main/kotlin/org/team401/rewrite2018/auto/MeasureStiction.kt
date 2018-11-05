package org.team401.rewrite2018.auto

import com.ctre.phoenix.motorcontrol.ControlMode
import edu.wpi.first.wpilibj.PowerDistributionPanel
import org.snakeskin.auto.steps.AutoStep
import org.snakeskin.component.TankDrivetrain
import org.snakeskin.units.FeetPerSecond
import org.snakeskin.units.RadiansPerSecond
import org.snakeskin.utility.Recorder
import org.team401.rewrite2018.Measurements

/**
 * @author Cameron Earle
 * @version 10/7/2018
 *
 */
class MeasureStiction(val drivetrain: TankDrivetrain, val power: Double, val rampRate: Double): AutoStep() {
    private lateinit var rec: Recorder

    private val voltage = drivetrain.left.master.busVoltage
    private var startTime = 0.0

    override fun entry(currentTime: Double) {
        rec = Recorder.toCSV("/home/lvuser", "StictionData")
        rec.setColumnTitle("percent", "Percent Output")
        rec.setColumnTitle("vbus", "Bus Voltage (volts)")
        rec.setColumnTitle("volts", "Applied Voltage (volts)")
        rec.setColumnTitle("vel", "Drivetrain Velocity (ft/s)")

        startTime = currentTime
    }

    override fun action(currentTime: Double, lastTime: Double): Boolean {
        rec.recordTimestamp()
        val percentOutput = rampRate * (currentTime - startTime)
        if (percentOutput > power) return true

        drivetrain.arcade(ControlMode.PercentOutput, percentOutput, 0.0)
        rec.record("percent", percentOutput)
        rec.record("vbus", (drivetrain.left.master.busVoltage + drivetrain.right.master.busVoltage) / 2.0)
        rec.record("voltage", (drivetrain.left.master.motorOutputVoltage + drivetrain.right.master.motorOutputVoltage) / 2.0)
        rec.record("vel", (drivetrain.left.getVelocity() + drivetrain.right.getVelocity()).toUnit(RadiansPerSecond).value / 2.0)

        return false
    }

    override fun exit(currentTime: Double) {
        drivetrain.stop()
        rec.write()
    }
}