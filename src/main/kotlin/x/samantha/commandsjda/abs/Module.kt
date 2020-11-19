package x.samantha.commandsjda.abs

import java.util.ArrayList

class Module(val name: String) {
    var commands: MutableList<ICommand> = ArrayList()
    private var isEnabled: Boolean = true

    /**
     * Add a command to the module
     * @param command
     * The command to be added to the module
     */
    fun addCommand(command: ICommand): Module {
        this.commands.add(command)
        return this
    }

    /**
     * Add commands to the module
     * @param commands
     * The commands to be added to the module
     */
    fun addCommands(vararg commands: ICommand): Module {
        commands.forEach { this.commands.add(it) }
        return this
    }

    /**
     * Enable or disable this module
     * @param value
     * Whether to enable or disable the module
     */
    fun setEnabled(value: Boolean) {
        this.isEnabled = value
    }

    /**
     * Whether the module is enabled
     * @return Boolean
     */
    fun getEnabled(): Boolean {
        return isEnabled
    }
}
