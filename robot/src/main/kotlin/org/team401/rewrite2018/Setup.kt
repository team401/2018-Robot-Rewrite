package org.team401.rewrite2018

import org.snakeskin.dsl.*
import org.snakeskin.event.Events
import org.snakeskin.registry.RealTimeTasks
import org.snakeskin.rt.RealTimeExecutor
import org.snakeskin.rt.RealTimeTask
import org.snakeskin.state.StateMachine

/**
 * @author Cameron Earle
 * @version 7/14/2018
 *
 */

object MySub: Subsystem("mySub") {
    override fun setup() {
        on (Events.ENABLED) {
            println("ENABLED START")
            myMachine.setState(States.TEST1)
            println("ENABLED DONE")
        }
    }

    enum class States {
        TEST1,
        TEST2
    }

    val myMachine: StateMachine<States> = stateMachine {
        state(States.TEST1) {
            action {
                println("entered state")
            }
        }
    }
}

@Setup
fun setup() {
    RealTimeExecutor.rate = .005
    Subsystems.add(MySub)
}