package x.samantha.commandsjda.managers

import net.dv8tion.jda.api.Permission
import x.samantha.commandsjda.abs.CommandContext
import x.samantha.commandsjda.abs.ICommand
import x.samantha.commandsjda.abs.Module
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import x.samantha.commandsjda.annotations.Permissions

import java.util.HashMap

object CommandManager {

    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    @Suppress("MemberVisibilityCanBePrivate")
    @JvmStatic val commandRegistry = HashMap<String, ICommand>()

    @Suppress("MemberVisibilityCanBePrivate")
    @JvmStatic val moduleRegistry = HashMap<String, Module>()

    fun onCall(ctx: CommandContext) {
        val cmd = ctx.command

        if (!cmd.module().getEnabled()) {
            ctx.channel.sendMessage("The module \"${cmd.module().name}\" has been disabled!").queue()
        } else if (cmd.javaClass.isAnnotationPresent(Permissions::class.java)) {
            log.info("Command has Permissions annotation! Checking perms...")
            val permissions = mutableListOf<Permission>()

            permissions.addAll(cmd.permissions())

            if (ctx.guild.selfMember.hasPermission(ctx.guild.getGuildChannelById(ctx.channel.id)!!, permissions) && ctx.member.hasPermission(ctx.guild.getGuildChannelById(ctx.channel.id)!!, permissions)) {
                log.info("Permission requirements met! Executing command...")
                cmd.onCall(ctx)
            } else {
                log.info("Permission requirements not met. Cancelling command execution...")
                ctx.channel.sendMessage("This bot and the message author both need the following permissions to run this command:\n```css\n${permissions.joinToString { "- $it\n" }}\n```").queue()
            }
        } else {
            cmd.onCall(ctx)
        }
    }

    /**
     * Add a [Command] to the command registry
     * @param command
     * The [Command] to be added
     */
    fun registerCommand(command: ICommand) {
        commandRegistry[command.name().toLowerCase()] = command
        command.aliases().forEach { commandRegistry[it.toLowerCase()] = command}
    }

    /**
     * Add a [Module] to the module registry
     * @param module
     * The [Module] to be added
    */
    fun registerModule(module: Module) {
        moduleRegistry[module.name.toLowerCase()] = module
        module.commands.iterator().forEach { registerCommand(it) }
    }

    /**
     * Fetch a [Command] from the command registry
     * @param name
     * The trigger of the [Command] to fetch
     * @return [Command]
     */
    @JvmStatic fun getCommand(name: String): ICommand? {
        return commandRegistry[name]
    }
}
