package x.samantha.commandsjda.abs

import x.samantha.commandsjda.managers.CommandManager
import x.samantha.commandsjda.util.CommandSearchUtil
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.IndexSearcher

class CommandContext

private constructor(
    val guild: Guild,
    val channel: TextChannel,
    val member: Member,
    val author: User,
    val message: Message,
    val rawEvent: MessageReceivedEvent
) {

    lateinit var trigger: String
    lateinit var args: Array<String>
    lateinit var rawArgs: String
    lateinit var command: ICommand

    companion object {
        /**
         * Create a CommandContext object
         * @return CommandContext
         */
        fun parse(event: MessageReceivedEvent, prefix: String, enableUnknownCmdMsg: Boolean): CommandContext? {
            val input: String
            val tempArgs: Array<String>
            val message = event.message.contentRaw

            // Check if the message starts with the set prefix, return null if it doesn't
            if (message.startsWith(prefix)) {
                input = message.substring(prefix.length).trim { it <= ' ' }
            } else {
                return null
            }

            // If there is nothing after the prefix, return null, else continue
            if (input.isEmpty()) return null
            tempArgs = input.split("\\s+".toRegex()).toTypedArray()
            if (tempArgs.isEmpty()) return null

            // Try to get the specified command from the command registry
            val command = CommandManager.getCommand(tempArgs[0].toLowerCase())

            // If the command exists create a new CommandContext Object and return it
            if (command != null) {
                val ctx = CommandContext(
                        event.guild,
                        event.textChannel,
                        event.member!!,
                        event.author,
                        event.message,
                        event
                )
                ctx.trigger = tempArgs[0]
                ctx.command = command
                ctx.args = tempArgs.copyOfRange(1, tempArgs.size)
                ctx.rawArgs = input.replaceFirst(tempArgs[0], "")
                return ctx
            } else {
                // If the command doesn't exist and enableUnknownCmdMsg is true then suggest similar commands
                if (enableUnknownCmdMsg) {
                    val query = QueryParser("command", CommandSearchUtil.analyzer).parse(tempArgs[0].toLowerCase())
                    val hitsPerPage = 5
                    val reader = DirectoryReader.open(CommandSearchUtil.index)
                    val searcher = IndexSearcher(reader)
                    val docs = searcher.search(query, hitsPerPage)
                    val hits = docs.scoreDocs
                    val msgBuilder = MessageBuilder("The command `${tempArgs[0].toLowerCase()}` was not found, did you mean any of the following?\n```")
                    hits.forEach {
                        msgBuilder.append("${searcher.doc(it.doc).get("command").toLowerCase()}\n")
                    }
                    reader.close()
                    msgBuilder.append("\n```")
                    event.textChannel.sendMessage(msgBuilder.build()).queue()
                }
                // Return null for commands that don't exist
                return null
            }
        }
    }

    /**
     * Check if the message contained additional arguments other than the command trigger
     * @return boolean
     */
    fun hasArguments(): Boolean {
        return args.isNotEmpty() && !rawArgs.isEmpty()
    }
}
