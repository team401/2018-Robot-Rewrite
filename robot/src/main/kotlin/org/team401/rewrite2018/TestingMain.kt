package org.team401.rewrite2018

import groovy.lang.Binding
import groovy.lang.GroovyShell
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer
import org.snakeskin.InitManager
import org.snakeskin.hardware.Environment
import org.snakeskin.hardware.Hardware
import org.snakeskin.hardware.impl.SoftwareTimeSource
import org.team401.rewrite2018.subsystems.Drivetrain
import java.util.*

/**
 * @author Cameron Earle
 * @version 8/1/18
 */
object TestingMain {
    @JvmStatic
    fun main(args: Array<String>) {
        Hardware.environment = Environment.SOFTWARE
        Hardware.setTimeSource(SoftwareTimeSource())
        InitManager.init()

        val importCustomizer = ImportCustomizer()
        importCustomizer.addStarImports(javaClass.`package`.name)
        val configuration = CompilerConfiguration()
        configuration.addCompilationCustomizers(importCustomizer)

        val binding = Binding()

        val shell = GroovyShell(this.javaClass.classLoader, binding, configuration)

        while (true) {
            try {
                val cmd = readLine()
                val script = shell.parse(cmd)
                val ans = script.run()
                println(ans)
                binding.setVariable("ans", ans)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}