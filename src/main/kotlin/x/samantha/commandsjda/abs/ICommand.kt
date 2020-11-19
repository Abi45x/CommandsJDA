package x.samantha.commandsjda.abs

import x.samantha.commandsjda.annotations.Command
import x.samantha.commandsjda.annotations.Description
import x.samantha.commandsjda.annotations.Permissions
import x.samantha.commandsjda.managers.CommandManager
import net.dv8tion.jda.api.Permission

interface ICommand {

    /**
     * The method called when someone tries to execute a command
     * @param ctx
     * The context in which the command is executed
     */
    fun onCall(ctx: CommandContext)

    /**
     * The name of this command
     * @return String
     */
    fun name(): String {
        return this.javaClass.getAnnotation(Command::class.java).name
    }

    /**
     * A list of aliases that can activate this command
     * @return List<String>
     */
    fun aliases(): List<String> {
        return this.javaClass.getAnnotation(Command::class.java).aliases.asList()
    }

    /**
     * The description of this command
     * @return String
     */
    fun description(): String {
        return this.javaClass.getAnnotation(Description::class.java).description
    }

    /**
     * The permissions the user and bot need to execute this command
     * @return List<Permission>
     */
    fun permissions(): List<Permission> {
        return this.javaClass.getAnnotation(Permissions::class.java).permissions.asList()
    }

    fun module(): Module {
        var module: Module = Module("NO MODULE")
        CommandManager.moduleRegistry.values.forEach { if (it.commands.contains(this)) module = it }
        return module
    }
}
