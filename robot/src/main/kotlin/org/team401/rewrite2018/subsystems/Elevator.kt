package org.team401.rewrite2018.subsystems

import org.snakeskin.component.Gearbox
import org.snakeskin.dsl.*
import org.snakeskin.hardware.CTREHardware
import org.team401.rewrite2018.constants.Constants

/**
 * @author Cameron Earle
 * @version 8/2/18
 */
object Elevator: Subsystem("Elevator") {
    private val master = CTREHardware.TalonSRX(Constants.Elevator.ELEVATOR_MASTER_CAN)
    private val slave1 = CTREHardware.VictorSPX(Constants.Elevator.ELEVATOR_SLAVE_1_CAN)
    private val slave2 = CTREHardware.VictorSPX(Constants.Elevator.ELEVATOR_SLAVE_2_CAN)
    private val slave3 = CTREHardware.VictorSPX(Constants.Elevator.ELEVATOR_SLAVE_3_CAN)

    val gearbox = Gearbox(master, slave1, slave2, slave3)
}