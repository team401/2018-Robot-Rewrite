package org.team401.rewrite2018.subsystems

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.FeedbackDevice
import org.snakeskin.component.Gearbox
import org.snakeskin.dsl.*
import org.snakeskin.event.Events
import org.snakeskin.hardware.CTREHardware
import org.snakeskin.hardware.FRCHardware
import org.snakeskin.logic.LockingDelegate
import org.snakeskin.state.StateMachine
import org.snakeskin.units.AngularDistanceUnitCTREMagEncoder
import org.snakeskin.units.AngularVelocityUnitCTREMagEncoder
import org.snakeskin.units.LinearDistanceUnit
import org.snakeskin.units.measure.Measure
import org.snakeskin.units.measure.distance.angular.AngularDistanceMeasure
import org.snakeskin.units.measure.distance.angular.AngularDistanceMeasureCTREMagEncoder
import org.snakeskin.units.measure.distance.linear.LinearDistanceMeasure
import org.team401.rewrite2018.constants.Constants

/**
 * @author Cameron Earle
 * @version 8/2/18
 */
object Elevator: Subsystem("Elevator") {
    // States
    enum class DeployStates {
        STOWED,
        DEPLOYING,
        DEPLOYED
    }

    enum class ElevatorStates {
        OPEN_LOOP,
        MANUAL_ADJUSTMENT,
        HOLD_POSITION,
        SCALE_POS_NOT_HOMED,
        HOMING,

        GOING_TO_DRIVE,
        GOING_TO_COLLECTION,

        POS_COLLECTION,
        POS_DRIVE,
        POS_SWITCH,
        POS_SCALE_LOW,
        POS_SCALE,
        POS_SCALE_HIGH,
        POS_MAX,
        POS_VAULT_RUNNER,

        STARTING_CLIMB,
        CLIMB_MANUAL_ADJUSTMENT,
        CLIMBING,
        CLIMBING_HIGH
    }

    val ShifterStates = ShifterState(
            Constants.Elevator.ELEVATOR_SOLENOID_ON_FOR_SHIFTER_EXTENDED,
            Constants.Elevator.ELEVATOR_SHIFTER_EXTENDED_FOR_LOW_GEAR
    )

    object ClampStates: PistonState(Constants.Elevator.ELEVATOR_SOLENOID_ON_FOR_CLAMP_EXTENDED) {
        val CLOSED = EXTENDED
        val OPEN = RETRACTED
    }

    object RatchetStates: PistonState(Constants.Elevator.ELEVATOR_SOLENOID_ON_FOR_RATCHET_EXTENDED) {
        val LOCKED = EXTENDED
        val UNLOCKED = RETRACTED
    }

    object KickerStates: PistonState(Constants.Elevator.ELEVATOR_SOLENOID_ON_FOR_KICKER_EXTENDED) {
        val KICK = EXTENDED
        val STOW = RETRACTED
    }

    // Hardware
    private val master = CTREHardware.TalonSRX(Constants.Elevator.ELEVATOR_MASTER_CAN)
    private val slave1 = CTREHardware.VictorSPX(Constants.Elevator.ELEVATOR_SLAVE_1_CAN)
    private val slave2 = CTREHardware.VictorSPX(Constants.Elevator.ELEVATOR_SLAVE_2_CAN)
    private val slave3 = CTREHardware.VictorSPX(Constants.Elevator.ELEVATOR_SLAVE_3_CAN)

    val gearbox = Gearbox(master, slave1, slave2, slave3)
    val shifter = FRCHardware.Solenoid(Constants.Elevator.ELEVATOR_SHIFTER_SOLENOID)
    val deployer = FRCHardware.Solenoid(Constants.Elevator.ELEVATOR_DEPLOY_SOLENOID)
    val ratchet = FRCHardware.Solenoid(Constants.Elevator.ELEVATOR_RATCHET_SOLENOID)
    val kicker = FRCHardware.Solenoid(Constants.Elevator.ELEVATOR_KICKER_SOLENOID)
    val clamp = FRCHardware.Solenoid(Constants.Elevator.ELEVATOR_CLAMP_SOLENOID)

    /**
     * Helper function to take elevator estop state into account
     */
    private fun setElevator(controlMode: ControlMode, value: Double) {
        if (estopped) {
            gearbox.set(ControlMode.PercentOutput, 0.0)
        } else {
            gearbox.set(controlMode, value)
        }
    }

    // State Machines

    val deployMachine: StateMachine<DeployStates> = stateMachine {
        state (DeployStates.STOWED) {
            entry {
                deployer.set(false)
            }
        }

        state (DeployStates.DEPLOYING) {
            timeout(Constants.Elevator.ELEVATOR_DEPLOY_TIMEOUT, DeployStates.DEPLOYED) //TODO Make time use Measure and make state typed (fixed in 2.0b2)

            entry {
                deployer.set(true)
            }
        }

        state (DeployStates.DEPLOYED) {
            entry {
                deployer.set(false)
            }
        }
    }

    val elevatorMachine: StateMachine<ElevatorStates> = commandMachine(stateMap(
            ElevatorStates.POS_COLLECTION to Constants.Elevator.COLLECTION_POS,
            ElevatorStates.POS_DRIVE to Constants.Elevator.DRIVING_POS,
            ElevatorStates.POS_SWITCH to Constants.Elevator.SWITCH_POS,
            ElevatorStates.POS_SCALE_LOW to Constants.Elevator.SCALE_LOW_POS,
            ElevatorStates.POS_SCALE to Constants.Elevator.SCALE_POS,
            ElevatorStates.POS_SCALE_HIGH to Constants.Elevator.SCALE_HIGH_POS,
            ElevatorStates.POS_MAX to Constants.Elevator.MAX_POS,
            ElevatorStates.POS_VAULT_RUNNER to Constants.Elevator.SWITCH_POS
    ), {
        master.configMotionCruiseVelocity(Constants.Elevator.ELEVATOR_RUN_CRUISE_VELOCITY.toUnit(AngularVelocityUnitCTREMagEncoder).value.toInt(), 0)
        if (value is LinearDistanceMeasure) {
            setElevator(ControlMode.MotionMagic, (value as LinearDistanceMeasure)
                    .toAngularDistance(Constants.Elevator.ELEVATOR_PITCH_RADIUS)
                    .toUnit(AngularDistanceUnitCTREMagEncoder).value)
        }

        if (value is AngularDistanceMeasure) {
            setElevator(ControlMode.MotionMagic, (value as AngularDistanceMeasure)
                    .toUnit(AngularDistanceUnitCTREMagEncoder).value)
        }
    }) {

    }


    // Boolean state logic (solenoids)

    /**
     * Shifts the elevator into the given state
     */
    fun shift(state: Boolean) {
        shifter.set(state)
    }

    /**
     * Sets the clamp to the given state
     */
    fun setClampState(state: Boolean) {
        clamp.set(state)
    }

    /**
     * Toggles the state of the clamp
     */
    fun toggleClampState() {
        clamp.set(!clamp.get())
    }

    /**
     * Sets the ratchet to the given state
     */
    fun setRatchetState(state: Boolean) {
        ratchet.set(state)
    }

    /**
     * Sets the state of the kicker to the given state
     */
    fun setKickerState(state: Boolean) {
        kicker.set(state)
    }

    //Flags and data
    /**
     * Represents whether or not the elevator is deployed (in the DEPLOYED state)
     */
    val deployed: Boolean
        get() = deployMachine.isInState(DeployStates.DEPLOYED)

    /**
     * Represents whether or not the elevator has been homed
     */
    var homed by LockingDelegate(false)

    /**
     * Represents whether or not the elevator has been estopped
     */
    var estopped by LockingDelegate(false)

    /**
     * Estops the elevator, immediately zeroing its output, unhoming it,
     * and preventing any movement command
     */
    fun estop() {
        estopped = true
        gearbox.set(ControlMode.PercentOutput, 0.0)
        homed = false
    }

    /**
     * Brings the elevator out of estop, and sets the state to homing
     */
    fun restart() {
        estopped = false
        elevatorMachine.setState(ElevatorStates.HOMING)
    }


    override fun setup() {
        gearbox.setSensor(FeedbackDevice.CTRE_MagEncoder_Absolute)
        gearbox.setCurrentLimit(Constants.Elevator.ELEVATOR_CURRENT_LIMIT_CONTINUOUS)

        master.setSelectedSensorPosition(0, 0, 0)
        master.configMotionAcceleration(Constants.Elevator.ELEVATOR_ACCEL.toUnit(AngularVelocityUnitCTREMagEncoder).value.toInt(), 0)

        on (Events.TELEOP_ENABLED) {
            deployMachine.setState(DeployStates.DEPLOYED)

            if (!homed) {
                shift(ShifterStates.HIGH)

            }
        }
    }
}