package x.samantha.commandsjda.handlers

import x.samantha.commandsjda.managers.CommandManager
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import x.samantha.commandsjda.abs.CommandContext
import x.samantha.commandsjda.abs.ICommand
import x.samantha.commandsjda.abs.Module
import org.slf4j.LoggerFactory

import java.text.MessageFormat
import java.util.Objects

class CommandHandler

internal constructor(private val canBeSelf: Boolean, private val canBeBot: Boolean, private val prefix: String, private val enableUnknownCmdMsg: Boolean) : ListenerAdapter() {

    private val log = LoggerFactory.getLogger(CommandHandler::class.java)

    override fun onMessageReceived(event: MessageReceivedEvent) {
        log.info(MessageFormat.format("[{0}] {1}:\t\"{2}\"",
                if (event!!.guild == null) "PRIVATE" else event.guild.name, event.author.name, event.message.contentRaw))

        if (event.privateChannel != null) return

        if (event.author.isFake) return

        if (!canBeSelf && event.author == event.jda.selfUser) return

        if (!canBeBot && event.author.isBot) return

        val ctx = CommandContext.parse(event, prefix, enableUnknownCmdMsg)
        if (Objects.isNull(ctx)) return

        CommandManager.onCall(ctx!!)
    }

    data class Builder (
        var canBeSelfUser: Boolean = false,
        var canBeBot: Boolean = false,
        var enableUnknownCmdMsg: Boolean = false,
        var prefix: String = "!",
        var commands: MutableList<ICommand> = mutableListOf(),
        var modules: MutableList<Module> = mutableListOf()
    ) {
        /**
         * Add a command to the builder, this will be added to the command registry when Builder#build() is called
         * @param command
         * The command to be added to the Builder
         */
        fun addCommand(command: ICommand): Builder {
            commands.add(command)
            return this
        }

        /**
         * Add a module to the builder, this will be added to the module registry when Builder#build() is called
         * @param module
         * The module to be added to the Builder
         */
        fun addModule(module: Module): Builder {
            modules.add(module)
            return this
        }

        /**
         * Create a new CommandHandler instance with the provided options, commands and modules
         */
        fun build(): CommandHandler {
            this.commands.forEach { CommandManager.registerCommand(it) }
            this.modules.forEach { CommandManager.registerModule(it) }
            return CommandHandler(this.canBeSelfUser, this.canBeBot, this.prefix, this.enableUnknownCmdMsg)
        }
    }
}
